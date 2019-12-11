package org.ku8eye.service;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.EventList;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.joda.time.DateTime;
import org.ku8eye.Constants;
import org.ku8eye.bean.monitor.AppAndPubSerInfo;
import org.ku8eye.bean.monitor.HisttoryResourceUsage;
import org.ku8eye.bean.monitor.MonitorAjaxReponse;
import org.ku8eye.bean.monitor.MonitorResponse;
import org.ku8eye.bean.monitor.ResourceUsage;
import org.ku8eye.bean.monitor.ServiceInfo;
import org.ku8eye.bean.monitor.SystemEvent;
import org.ku8eye.bean.project.Project;
import org.ku8eye.domain.Ku8Project;
import org.ku8eye.domain.Ku8Service;
import org.ku8eye.domain.User;
import org.ku8eye.mapping.Ku8ProjectMapper;
import org.ku8eye.mapping.Ku8ServiceMapper;
import org.ku8eye.service.k8s.K8sAPIService;
import org.ku8eye.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MonitorService {
	private Logger log = Logger.getLogger(this.toString());

	@Autowired
	private K8sAPIService k8sAPIService;
	@Autowired
	private Ku8ProjectMapper projectDao;
	@Autowired
	private Ku8ServiceMapper serviceDao;
	@Autowired
	private Ku8ResPartionService ku8ResPartionService;
	@Autowired
	private ServiceAndRcService serviceAndRcService;
	@Autowired
	private ApplicationService applicationService;
	
	private String dateTimeFormatter = "HH:mm:ss";
	private String dateTimeFormatterPre = "yyyy-MM-dd'T'HH:mm:ss";
		
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public MonitorAjaxReponse getHostResourceUsages() {
		String cpuUsageSqlPrefix = "select * from \"cpu/usage_ns_cumulative\" where container_name = 'machine' and host_id = '";
		String cpuUsageSqlSuffix = "' order by time desc limit 2";
		
		String memoryUsageUsedSqlPrefix = "select value from \"memory/usage_bytes_gauge\" where container_name = 'machine' and host_id = '";
		String memoryUsageUsedSqlSuffix = "' order by time desc limit 1";
		
		String memoryUsageLimitSqlPrefix = "select value from \"memory/limit_bytes_gauge\" where container_name = 'machine' and host_id = '";
		String memoryUsageLimitSqlSuffix = "' order by time desc limit 1";
		
		List<ResourceUsage> resourceUsages = new ArrayList<ResourceUsage>();
		
		//获取所有node的名字，即ip地址
		List<String> hostIps = new ArrayList<String>();
		Map<String, List<String>> nodesInfoMap = new HashMap<String, List<String>>();
		System.out.println("ku8api start: " + System.currentTimeMillis());
		try{
			List<Node> nodes = k8sAPIService.getAllNodes(0);
			if(nodes != null){
				int cpuAmount = 24;
				double memoryAmount = 260;
				for(Node node : nodes){
					if(node.getMetadata() != null){
						hostIps.add(node.getMetadata().getName());
						if(node.getStatus() != null && node.getStatus().getCapacity() != null){
							if(node.getStatus().getCapacity().containsKey("cpu") && node.getStatus().getCapacity().get("cpu") != null){
								String cpuAmountString = node.getStatus().getCapacity().get("cpu").getAmount();
								if(cpuAmountString != null && cpuAmountString.length() > 0){
									cpuAmount = Integer.parseInt(cpuAmountString);
								}
							}
							if(node.getStatus().getCapacity().containsKey("memory") && node.getStatus().getCapacity().get("memory") != null){
								String memoryAmountString = node.getStatus().getCapacity().get("memory").getAmount();
								if(memoryAmountString != null && memoryAmountString.length() > 0){
									memoryAmountString = memoryAmountString.replaceAll("\\D", "");
									memoryAmount =new BigDecimal(memoryAmountString).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).doubleValue();
								}
							}
						}
						List<String> valueList = new ArrayList<String>();
						valueList.add("cpu:" + cpuAmount + " memory:" + memoryAmount + "G" + "\n" + node.getMetadata().getName());
						valueList.add(cpuAmount+"");
						nodesInfoMap.put(node.getMetadata().getName(),valueList);
					}
				}
			}
		} catch(Exception e){
			log.error("get ku8api nodes error:" + e.getMessage());;
			return new MonitorAjaxReponse(-1, "get ku8 nodes error!");
		}
		System.out.println("ku8api end: " + System.currentTimeMillis());
		InfluxDB influxDB = getInfluxDB();
		if(hostIps.size() > 0){//从各个node中获取cpu数量和memory数量，默认值设为cpu=24个，memory=260G
			for(String hostIp : hostIps){
				ResourceUsage resourceUsage = new ResourceUsage();
				resourceUsage.setTitle(nodesInfoMap.get(hostIp).get(0));
				
				if(influxDB != null){
					//查询主机cpu利用率
					List<List<Object>> cpuUsageValues = null;
					try {
						cpuUsageValues = getQueryResultValues(cpuUsageSqlPrefix + hostIp + cpuUsageSqlSuffix, influxDB);
					} catch (Exception e) {
						log.error("influxdb error: " + e.getMessage());
						return new MonitorAjaxReponse(-2, "influxdb error");
					}
					
					//获取查询结果，用两值相减，并转换单位，同时求平均值
					if(cpuUsageValues != null && cpuUsageValues.size() > 1){
						if(cpuUsageValues.get(0).size() > 10 && cpuUsageValues.get(1).size() > 10){
							BigDecimal value1 = new BigDecimal((Double)cpuUsageValues.get(0).get(10));
							BigDecimal value2 = new BigDecimal((Double)cpuUsageValues.get(1).get(10));
							BigDecimal subtractValue = value1.subtract(value2);
							BigDecimal divideValue = subtractValue.divide(new BigDecimal(1000000000L)).divide(new BigDecimal(30), BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(nodesInfoMap.get(hostIp).get(1)), BigDecimal.ROUND_HALF_UP);
							resourceUsage.setCpuUsage(divideValue.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
							resourceUsage.setCpuAveliable(1-resourceUsage.getCpuUsage());
						}
					}
					
					//查询主机memory利用率
					List<List<Object>> memoryUsageValues = null;
					try {
						memoryUsageValues = getQueryResultValues(memoryUsageUsedSqlPrefix + hostIp + memoryUsageUsedSqlSuffix,influxDB);
					} catch (Exception e) {
						log.error("influxdb error: " + e.getMessage());
						return new MonitorAjaxReponse(-2, "influxdb error");
					}
					
					//获取查询结果，并转换单位
					if(memoryUsageValues != null && memoryUsageValues.size() > 0){
						if(memoryUsageValues.get(0).size() > 1){
							BigDecimal memoryUsedValue = new BigDecimal((Double)memoryUsageValues.get(0).get(1));
							double memoryUsedValue2 = memoryUsedValue.divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							resourceUsage.setMemoryUsage(memoryUsedValue2);
						}
					}
					
					//查询主机memory限制
					List<List<Object>> memoryLimitValues = null;
					try {
						memoryLimitValues = getQueryResultValues(memoryUsageLimitSqlPrefix + hostIp + memoryUsageLimitSqlSuffix,influxDB);
					} catch (Exception e) {
						log.error("influxdb error: " + e.getMessage());
						return new MonitorAjaxReponse(-2, "influxdb error");
					}
					if(memoryLimitValues != null && memoryLimitValues.size() > 0){
						if(memoryLimitValues.get(0).size() > 1){
							BigDecimal memoryLimitValue = new BigDecimal((Double)memoryLimitValues.get(0).get(1));
							double memoryLimitValue2 = memoryLimitValue.divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();;
							resourceUsage.setMemoryAvelibale(memoryLimitValue2 - resourceUsage.getMemoryUsage());
							
						}
					}
				}
				resourceUsages.add(resourceUsage);
			}
		} else{
			return new MonitorAjaxReponse(1, "ku8 master has no nodes");
		}
		MonitorAjaxReponse monitorAjaxReponse = null;
		if(influxDB == null){
			monitorAjaxReponse = new MonitorAjaxReponse(-2, "connect influxdb failed");
		} else{
			monitorAjaxReponse = new MonitorAjaxReponse(0, "is ok");
		}
		monitorAjaxReponse.setData(resourceUsages);
		return monitorAjaxReponse;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public MonitorAjaxReponse getHostAmount(){
		//获取所有node的名字，即ip地址
		List<String> hostIps = new ArrayList<String>();
		try{
			List<Node> nodes = k8sAPIService.getAllNodes(0);
			if(nodes != null){
				for(Node node : nodes){
					if(node.getMetadata() != null){
						hostIps.add(node.getMetadata().getName());
					}
				}
				MonitorAjaxReponse monitorAjaxReponse = new MonitorAjaxReponse(0, "ok");
				monitorAjaxReponse.setData(hostIps);
				return monitorAjaxReponse;
			} else{
				return new MonitorAjaxReponse(1, "ku8 has no nodes!");
			}
		} catch(Exception e){
			log.error("get ku8api nodes error:" + e.getMessage());;
			return new MonitorAjaxReponse(-1, "get ku8 nodes error!");
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public MonitorAjaxReponse getHostAllInfo(String hostIP){
		String cpuUsageSqlPrefix = "select * from \"cpu/usage_ns_cumulative\" where container_name = 'machine' and host_id = '";
		String cpuUsageSqlSuffix = "' order by time desc limit 2";
		
		String memoryUsageUsedSqlPrefix = "select value from \"memory/usage_bytes_gauge\" where container_name = 'machine' and host_id = '";
		String memoryUsageUsedSqlSuffix = "' order by time desc limit 1";
		
		String memoryUsageLimitSqlPrefix = "select value from \"memory/limit_bytes_gauge\" where container_name = 'machine' and host_id = '";
		String memoryUsageLimitSqlSuffix = "' order by time desc limit 1";
		
		MonitorAjaxReponse monitorAjaxReponse = null;
		ResourceUsage resourceUsage = new ResourceUsage();
		int cpuAmount = 24;
		double memoryAmount = 260;
		
		Node node = null;
		try{
			node = k8sAPIService.getNodeByName(0, hostIP);
		} catch(Exception e){
			log.error("get ku8api nodes error:" + e.getMessage());;
			return new MonitorAjaxReponse(-1, "get ku8 node info error!");
		}
		if(node != null){
			if(node.getMetadata() != null){
				if(node.getStatus() != null && node.getStatus().getCapacity() != null){
					if(node.getStatus().getCapacity().containsKey("cpu") && node.getStatus().getCapacity().get("cpu") != null){
						String cpuAmountString = node.getStatus().getCapacity().get("cpu").getAmount();
						if(cpuAmountString != null && cpuAmountString.length() > 0){
							cpuAmount = Integer.parseInt(cpuAmountString);
						}
					}
					if(node.getStatus().getCapacity().containsKey("memory") && node.getStatus().getCapacity().get("memory") != null){
						String memoryAmountString = node.getStatus().getCapacity().get("memory").getAmount();
						if(memoryAmountString != null && memoryAmountString.length() > 0){
							memoryAmountString = memoryAmountString.replaceAll("\\D", "");
							memoryAmount =new BigDecimal(memoryAmountString).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).doubleValue();
						}
					}
				}
			}
		}
		
		resourceUsage.setTitle("cpu:" + cpuAmount + " memory:" + memoryAmount + "G" + "\n" + hostIP);
		
		InfluxDB influxDB = getInfluxDB();
		if(influxDB != null){
			//查询主机cpu利用率
			List<List<Object>> cpuUsageValues = null;
			try {
				cpuUsageValues = getQueryResultValues(cpuUsageSqlPrefix + hostIP + cpuUsageSqlSuffix, influxDB);
			} catch (Exception e) {
				log.error("influxdb error: " + e.getMessage());
				return new MonitorAjaxReponse(-2, "influxdb error");
			}
			
			//获取查询结果，用两值相减，并转换单位，同时求平均值
			if(cpuUsageValues != null && cpuUsageValues.size() > 1){
				if(cpuUsageValues.get(0).size() > 10 && cpuUsageValues.get(1).size() > 10){
					BigDecimal value1 = new BigDecimal((Double)cpuUsageValues.get(0).get(10));
					BigDecimal value2 = new BigDecimal((Double)cpuUsageValues.get(1).get(10));
					BigDecimal subtractValue = value1.subtract(value2);
					BigDecimal divideValue = subtractValue.divide(new BigDecimal(1000000000L)).divide(new BigDecimal(30), BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(cpuAmount), BigDecimal.ROUND_HALF_UP);
					resourceUsage.setCpuUsage(divideValue.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					resourceUsage.setCpuAveliable(1-resourceUsage.getCpuUsage());
				}
			}
			
			//查询主机memory利用率
			List<List<Object>> memoryUsageValues = null;
			try {
				memoryUsageValues = getQueryResultValues(memoryUsageUsedSqlPrefix + hostIP + memoryUsageUsedSqlSuffix,influxDB);
			} catch (Exception e) {
				log.error("influxdb error: " + e.getMessage());
				return new MonitorAjaxReponse(-2, "influxdb error");
			}
			
			//获取查询结果，并转换单位
			if(memoryUsageValues != null && memoryUsageValues.size() > 0){
				if(memoryUsageValues.get(0).size() > 1){
					BigDecimal memoryUsedValue = new BigDecimal((Double)memoryUsageValues.get(0).get(1));
					double memoryUsedValue2 = memoryUsedValue.divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					resourceUsage.setMemoryUsage(memoryUsedValue2);
				}
			}
			
			//查询主机memory限制
			List<List<Object>> memoryLimitValues = null;
			try {
				memoryLimitValues = getQueryResultValues(memoryUsageLimitSqlPrefix + hostIP + memoryUsageLimitSqlSuffix,influxDB);
			} catch (Exception e) {
				log.error("influxdb error: " + e.getMessage());
				return new MonitorAjaxReponse(-2, "influxdb error");
			}
			if(memoryLimitValues != null && memoryLimitValues.size() > 0){
				if(memoryLimitValues.get(0).size() > 1){
					BigDecimal memoryLimitValue = new BigDecimal((Double)memoryLimitValues.get(0).get(1));
					double memoryLimitValue2 = memoryLimitValue.divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();;
					resourceUsage.setMemoryAvelibale(memoryLimitValue2 - resourceUsage.getMemoryUsage());
					
				}
			}
			
			monitorAjaxReponse = new MonitorAjaxReponse(0, "is ok");
			monitorAjaxReponse.setData(resourceUsage);
			return monitorAjaxReponse;
		} else {
			return new MonitorAjaxReponse(-2, "connect influxdb failed");
		}
	}
	
	//查询某一台主机过去30分钟内的cpu使用率和memory使用率
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<HisttoryResourceUsage> getHostResourceUsages(String hostIP) {
		
		List<HisttoryResourceUsage> histtoryResourceUsages = new ArrayList<HisttoryResourceUsage>();
		String[] cpuTimeList = new String[60];
		String[] memoryTimeList = new String[60];
		double[] cpuValues = new double[60];
		double[] memoryValues = new double[60];
		double cpuLimit = 1;
		double memoryLimit = 260;
		
		String host_ip = hostIP;//要查询的主机ip地址
		
		//查询过去30分钟内cpu使用率的sql语句
		String cpuUsageSqlPrefix = "select * from \"cpu/usage_ns_cumulative\" where container_name = 'machine' and host_id = '";
		String cpuUsageSqlSuffix = "'order by time desc limit 61";
		
		//查询过去30分钟内memory使用率的sql语句
		String memoryUsageUsedSqlPrefix = "select value from \"memory/usage_bytes_gauge\" where container_name = 'machine' and host_id = '";
		String memoryUsageUsedSqlSuffix = "' order by time desc limit 60";
		
		//查询memory限制sql语句
		String memoryUsageLimitSqlPrefix = "select value from \"memory/limit_bytes_gauge\" where container_name = 'machine' and host_id = '";
		String memoryUsageLimitSqlSuffix = "' order by time desc limit 1";
		
		HisttoryResourceUsage historyreResourceUsage = new HisttoryResourceUsage();
		InfluxDB influxDB = getInfluxDB();
		if(influxDB != null){
			List<List<Object>> cpuUsageValues = getQueryResultValues(cpuUsageSqlPrefix + host_ip + cpuUsageSqlSuffix,influxDB);
			if(cpuUsageValues != null){
				//当数据不足30分钟的条数时，计算出缺少的，置为0值，并计算出对应的时间
				int initIndex = 0;
				if(cpuUsageValues.size() < 61){
					initIndex = 61 - cpuUsageValues.size();
				}
				for(int i=0; i < cpuUsageValues.size()-1; i++){
					if(cpuUsageValues.get(i).size() > 10 && cpuUsageValues.get(i+1).size() > 10){
						BigDecimal value1 = new BigDecimal((Double)cpuUsageValues.get(i).get(10));
						BigDecimal value2 = new BigDecimal((Double)cpuUsageValues.get(i+1).get(10));
						BigDecimal subtractValue = value1.subtract(value2);
						Node node = k8sAPIService.getNodeByName(0, host_ip);//从kubernetes api中查询相应node，获取cpu数量和memory限制
						int cpuAmount = 24;
						if(node != null && node.getStatus() != null && node.getStatus().getCapacity() != null && node.getStatus().getCapacity().containsKey("cpu")){
							String cpuAmountString = node.getStatus().getCapacity().get("cpu").getAmount();
							if(cpuAmountString != null && cpuAmountString.length() > 0){
								cpuAmount = Integer.parseInt(cpuAmountString);
							}
						}
						BigDecimal divideValue = subtractValue.divide(new BigDecimal(1000000000L)).divide(new BigDecimal(30), BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(cpuAmount), BigDecimal.ROUND_HALF_UP);//单位换算，并根据cpu数量求均值
						cpuValues[cpuUsageValues.size()-2-i+initIndex] = divideValue.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						cpuTimeList[cpuUsageValues.size()-2-i+initIndex] = new DateTime(cpuUsageValues.get(i).get(0)).toString(dateTimeFormatterPre); 
					}
				}
				//计算时间差值，填充数据源，因为dateTime类使用字符串创建时必须有日期，所以分为两步
				for(int i = initIndex-1; i >=0; i--){
					cpuTimeList[i] = new DateTime(cpuTimeList[i+1]).minusSeconds(30).toString(dateTimeFormatterPre);
				}
				
				for(int i = 0; i < cpuTimeList.length; i++){
					cpuTimeList[i] = new DateTime(cpuTimeList[i]).toString(dateTimeFormatter);
				}
			}
			
			historyreResourceUsage.setLimit(cpuLimit);
			historyreResourceUsage.setTime(cpuTimeList);
			historyreResourceUsage.setValue(cpuValues);
			historyreResourceUsage.setName(host_ip);
			histtoryResourceUsages.add(historyreResourceUsage);
			
			//执行memory使用情况sql语句，一样执行单位换算
			historyreResourceUsage = new HisttoryResourceUsage();
			List<List<Object>> memoryUsageValues = getQueryResultValues(memoryUsageUsedSqlPrefix + host_ip + memoryUsageUsedSqlSuffix,influxDB);
			if(memoryUsageValues != null){
				//当数据不足30分钟的条数时，计算出缺少的，置为0值，并计算出对应的时间
				int initIndex = 0;
				if(memoryUsageValues.size() < 60){
					initIndex = 60 - memoryUsageValues.size();
				}
				for(int i = 0; i < memoryUsageValues.size(); i++){
					if(memoryUsageValues.get(i).size() > 1){
						BigDecimal memoryUsedValue = new BigDecimal((Double)memoryUsageValues.get(i).get(1));
						memoryValues[memoryUsageValues.size()-1-i+initIndex] = memoryUsedValue.divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						memoryTimeList[memoryUsageValues.size()-1-i+initIndex] = new DateTime(memoryUsageValues.get(i).get(0)).toString(dateTimeFormatterPre); 
					}
				}
				//计算时间差值，填充数据源，因为dateTime类使用字符串创建时必须有日期，所以分为两步
				for(int i = initIndex-1; i >=0; i--){
					memoryTimeList[i] = new DateTime(memoryTimeList[i+1]).minusSeconds(30).toString(dateTimeFormatterPre);
				}
				
				for(int i = 0; i < cpuTimeList.length; i++){
					memoryTimeList[i] = new DateTime(memoryTimeList[i]).toString(dateTimeFormatter);
				}
			}
			
			//执行memory限制sql语句查询，一样执行单位换算
			List<List<Object>> memoryLimitValues = getQueryResultValues(memoryUsageLimitSqlPrefix + host_ip + memoryUsageLimitSqlSuffix,influxDB);
			if(memoryLimitValues != null && memoryLimitValues.size() > 0){
				if(memoryLimitValues.get(0).size() > 1){
					BigDecimal memoryLimitValue = new BigDecimal((Double)memoryLimitValues.get(0).get(1));
					memoryLimit = memoryLimitValue.divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();;
				}
			}
			historyreResourceUsage.setLimit(memoryLimit);
			historyreResourceUsage.setTime(memoryTimeList);
			historyreResourceUsage.setValue(memoryValues);
			histtoryResourceUsages.add(historyreResourceUsage);
		}
		
		return histtoryResourceUsages;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public MonitorAjaxReponse getServicePodResourceUsages(String podName) {
		String cpuUsageSqlPrefix = "select * from \"cpu/usage_ns_cumulative\" where pod_name = '";
		String cpuUsageSqlSuffix = "'order by time desc limit 2";
		
		String memoryUsageUsedSqlPrefix = "select value from \"memory/usage_bytes_gauge\" where pod_name = '";
		String memoryUsageUsedSqlSuffix = "' order by time desc limit 1";
		
		String memoryUsageLimitSqlPrefix = "select value,host_id from \"memory/limit_bytes_gauge\" where pod_name = '";
		String memoryUsageLimitSqlSuffix = "' order by time desc limit 1";
		
		String machineMemoryUsageLimitSqlPrefix = "select value from \"memory/limit_bytes_gauge\" where container_name = 'machine' and host_id = '";
		String machineMemoryUsageLimitSqlSuffix = "' order by time desc limit 1";
		
		InfluxDB influxDB = getInfluxDB();
		if(influxDB != null){
			ResourceUsage resourceUsage = new ResourceUsage();
			String title = podName + " ";
			List<List<Object>> cpuUsageValues = null;
			try {
				cpuUsageValues = getQueryResultValues(cpuUsageSqlPrefix + podName + cpuUsageSqlSuffix,influxDB);
			} catch (Exception e) {
				log.error("influxdb error: " + e.getMessage());
				return new MonitorAjaxReponse(-2, "influxdb error");
			}
			if(cpuUsageValues != null && cpuUsageValues.size() > 1){
				if(cpuUsageValues.get(0).size() > 10 && cpuUsageValues.get(1).size() > 10){
					BigDecimal value1 = new BigDecimal((Double)cpuUsageValues.get(0).get(10));
					BigDecimal value2 = new BigDecimal((Double)cpuUsageValues.get(1).get(10));
					BigDecimal subtractValue = value1.subtract(value2);
					BigDecimal divideValue = subtractValue.divide(new BigDecimal(1000000000L)).divide(new BigDecimal(30), BigDecimal.ROUND_HALF_UP);
					resourceUsage.setCpuUsage(divideValue.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					resourceUsage.setCpuAveliable(1-resourceUsage.getCpuUsage());
					title = title + "\n" + cpuUsageValues.get(0).get(3);
				}
			}
			resourceUsage.setTitle(title);
			
			List<List<Object>> memoryUsageValues = null;
			try {
				memoryUsageValues = getQueryResultValues(memoryUsageUsedSqlPrefix + podName + memoryUsageUsedSqlSuffix,influxDB);
			} catch (Exception e) {
				log.error("influxdb error: " + e.getMessage());
				return new MonitorAjaxReponse(-2, "influxdb error");
			}
			if(memoryUsageValues != null && memoryUsageValues.size() > 0){
				if(memoryUsageValues.get(0).size() > 1){
					BigDecimal memoryUsedValue = new BigDecimal((Double)memoryUsageValues.get(0).get(1));
					double memoryUsedValue2 = memoryUsedValue.divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					resourceUsage.setMemoryUsage(memoryUsedValue2);
				}
			}
			
			List<List<Object>> memoryLimitValues = null;
			try {
				memoryLimitValues = getQueryResultValues(memoryUsageLimitSqlPrefix + podName + memoryUsageLimitSqlSuffix,influxDB);
			} catch (Exception e) {
				log.error("influxdb error: " + e.getMessage());
				return new MonitorAjaxReponse(-2, "influxdb error");
			}
			if(memoryLimitValues != null && memoryLimitValues.size() > 0){
				if(memoryLimitValues.get(0).size() > 2){
					BigDecimal memoryLimitValue = new BigDecimal((Double)memoryLimitValues.get(0).get(1));
					if(memoryLimitValue.compareTo(new BigDecimal(0)) < 0){
						List<List<Object>> machineMemoryLimitValues = getQueryResultValues(machineMemoryUsageLimitSqlPrefix + memoryLimitValues.get(0).get(2) + machineMemoryUsageLimitSqlSuffix,influxDB);
						if(machineMemoryLimitValues != null && machineMemoryLimitValues.size() > 0){
							if(machineMemoryLimitValues.get(0).size() > 1){
								BigDecimal machineMemoryLimitValue = new BigDecimal((Double)machineMemoryLimitValues.get(0).get(1));
								double machineMemoryLimitValue2 = machineMemoryLimitValue.divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
								resourceUsage.setMemoryAvelibale(machineMemoryLimitValue2 - resourceUsage.getMemoryUsage());
							}
						}
					} else {
						double memoryLimitValue2 = memoryLimitValue.divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();;
						resourceUsage.setMemoryAvelibale(memoryLimitValue2 - resourceUsage.getMemoryUsage());
					}
				}
			}
			MonitorAjaxReponse monitorAjaxReponse = new MonitorAjaxReponse(0, "ok");
			monitorAjaxReponse.setData(resourceUsage);
			return monitorAjaxReponse;
		} else{
			return new MonitorAjaxReponse(-2, "influxdb error");
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public MonitorAjaxReponse getServiceResourceUsages(String serviceName) {
		String cpuUsageSqlPrefix = "select * from \"cpu/usage_ns_cumulative\" where pod_name = '";
		String cpuUsageSqlSuffix = "'order by time desc limit 2";
		
		String memoryUsageUsedSqlPrefix = "select value from \"memory/usage_bytes_gauge\" where pod_name = '";
		String memoryUsageUsedSqlSuffix = "' order by time desc limit 1";
		
		String memoryUsageLimitSqlPrefix = "select value,host_id from \"memory/limit_bytes_gauge\" where pod_name = '";
		String memoryUsageLimitSqlSuffix = "' order by time desc limit 1";
		
		String machineMemoryUsageLimitSqlPrefix = "select value from \"memory/limit_bytes_gauge\" where container_name = 'machine' and host_id = '";
		String machineMemoryUsageLimitSqlSuffix = "' order by time desc limit 1";
		
		MonitorResponse monitorResponse = new MonitorResponse();
		monitorResponse.setService_name(serviceName);
		List<ResourceUsage> resourceUsages = new ArrayList<ResourceUsage>();
		
		Map<String,List<String>> partionPodsMap = new HashMap<String, List<String>>();
		io.fabric8.kubernetes.api.model.Service service = null;
		int podAmount = 0;
		List<String> namesapce_names = ku8ResPartionService.getAllNameSpaceName(0);
		for(String namespace_name : namesapce_names){
			try {
				service = k8sAPIService.getServicesByName(0, namespace_name, serviceName);
			} catch (Exception e) {
				log.error("get service info for eCharts error: " + e.getMessage());
				return new MonitorAjaxReponse(-1, "ku8 error");
			}
			if(service != null){
				if(service.getMetadata() != null && !"kubernetes".equals(service.getMetadata().getName())){
					if(service.getSpec() != null && service.getSpec().getSelector() != null){
						PodList podList = null;
						try {
							podList = k8sAPIService.getPodsByLabelsSelector(0, namespace_name, service.getSpec().getSelector());
						} catch (Exception e) {
							log.error("get pod info for eCharts error: " + e.getMessage());
							return new MonitorAjaxReponse(-1, "ku8 error");
						}
						if(podList != null){
							List<Pod> podItems = podList.getItems();
							if(podItems != null && podItems.size() > 0){
								List<String> podNames = new ArrayList<String>();
								podAmount += podItems.size();
								for(Pod podItem : podItems){
									if(podItem.getMetadata() != null){
										podNames.add(podItem.getMetadata().getName());
									}
								}
								partionPodsMap.put(namespace_name, podNames);
							}
						}
					}
				}
			}
		}
		monitorResponse.setPodAmount(podAmount);
		
		InfluxDB influxDB = getInfluxDB();
		if(influxDB != null){
			for(String partion_name : partionPodsMap.keySet()){
				List<String> podNames = partionPodsMap.get(partion_name);
				for(String podName : podNames){
					ResourceUsage resourceUsage = new ResourceUsage();
					String title = podName + " ";
					List<List<Object>> cpuUsageValues = getQueryResultValues(cpuUsageSqlPrefix + podName + cpuUsageSqlSuffix,influxDB);
					if(cpuUsageValues != null && cpuUsageValues.size() > 1){
						if(cpuUsageValues.get(0).size() > 10 && cpuUsageValues.get(1).size() > 10){
							BigDecimal value1 = new BigDecimal((Double)cpuUsageValues.get(0).get(10));
							BigDecimal value2 = new BigDecimal((Double)cpuUsageValues.get(1).get(10));
							BigDecimal subtractValue = value1.subtract(value2);
							BigDecimal divideValue = subtractValue.divide(new BigDecimal(1000000000L)).divide(new BigDecimal(30), BigDecimal.ROUND_HALF_UP);
							resourceUsage.setCpuUsage(divideValue.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
							resourceUsage.setCpuAveliable(1-resourceUsage.getCpuUsage());
							title = title + "\n" + cpuUsageValues.get(0).get(3);
						}
					}
					resourceUsage.setTitle(title);
					resourceUsage.setPartionName(partion_name);
					
					List<List<Object>> memoryUsageValues = getQueryResultValues(memoryUsageUsedSqlPrefix + podName + memoryUsageUsedSqlSuffix,influxDB);
					if(memoryUsageValues != null && memoryUsageValues.size() > 0){
						if(memoryUsageValues.get(0).size() > 1){
							BigDecimal memoryUsedValue = new BigDecimal((Double)memoryUsageValues.get(0).get(1));
							double memoryUsedValue2 = memoryUsedValue.divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							resourceUsage.setMemoryUsage(memoryUsedValue2);
						}
					}
					
					List<List<Object>> memoryLimitValues = getQueryResultValues(memoryUsageLimitSqlPrefix + podName + memoryUsageLimitSqlSuffix,influxDB);
					if(memoryLimitValues != null && memoryLimitValues.size() > 0){
						if(memoryLimitValues.get(0).size() > 2){
							BigDecimal memoryLimitValue = new BigDecimal((Double)memoryLimitValues.get(0).get(1));
							if(memoryLimitValue.compareTo(new BigDecimal(0)) < 0){
								List<List<Object>> machineMemoryLimitValues = getQueryResultValues(machineMemoryUsageLimitSqlPrefix + memoryLimitValues.get(0).get(2) + machineMemoryUsageLimitSqlSuffix,influxDB);
								if(machineMemoryLimitValues != null && machineMemoryLimitValues.size() > 0){
									if(machineMemoryLimitValues.get(0).size() > 1){
										BigDecimal machineMemoryLimitValue = new BigDecimal((Double)machineMemoryLimitValues.get(0).get(1));
										double machineMemoryLimitValue2 = machineMemoryLimitValue.divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
										resourceUsage.setMemoryAvelibale(machineMemoryLimitValue2 - resourceUsage.getMemoryUsage());
									}
								}
							} else {
								double memoryLimitValue2 = memoryLimitValue.divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();;
								resourceUsage.setMemoryAvelibale(memoryLimitValue2 - resourceUsage.getMemoryUsage());
							}
						}
					}
					resourceUsages.add(resourceUsage);
				}
			}
		} else{
			return new MonitorAjaxReponse(-2, "influxdb error");
		}
		monitorResponse.setData(resourceUsages);
		MonitorAjaxReponse monitorAjaxReponse = new MonitorAjaxReponse(0, "ok");
		monitorAjaxReponse.setData(monitorResponse);
		return monitorAjaxReponse;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<HisttoryResourceUsage> getPodResourceUsages(String pod_name) {
		
		List<HisttoryResourceUsage> histtoryResourceUsages = new ArrayList<HisttoryResourceUsage>();
		String[] cpuTimeList = new String[60];
		String[] memoryTimeList = new String[60];
		double[] cpuValues = new double[60];
		double[] memoryValues = new double[60];
		double cpuLimit = 1;
		double memoryLimit = 260;
		
		String podName = pod_name;//要查询的pod名字
		
		String cpuUsageSqlPrefix = "select * from \"cpu/usage_ns_cumulative\" where pod_name = '";
		String cpuUsageSqlSuffix = "'order by time desc limit 61";
		
		String memoryUsageUsedSqlPrefix = "select value from \"memory/usage_bytes_gauge\" where pod_name = '";
		String memoryUsageUsedSqlSuffix = "' order by time desc limit 60";
		
		String memoryUsageLimitSqlPrefix = "select value,host_id from \"memory/limit_bytes_gauge\" where pod_name = '";
		String memoryUsageLimitSqlSuffix = "' order by time desc limit 1";
		
		String machineMemoryUsageLimitSqlPrefix = "select value from \"memory/limit_bytes_gauge\" where container_name = 'machine' and host_id = '";
		String machineMemoryUsageLimitSqlSuffix = "' order by time desc limit 1";
		
		HisttoryResourceUsage historyreResourceUsage = new HisttoryResourceUsage();
		InfluxDB influxDB = getInfluxDB();
		if(influxDB != null){
			List<List<Object>> cpuUsageValues = getQueryResultValues(cpuUsageSqlPrefix + podName + cpuUsageSqlSuffix,influxDB);
			if(cpuUsageValues != null){
				//当数据不足30分钟的条数时，计算出缺少的，置为0值，并计算出对应的时间
				int initIndex = 0;
				if(cpuUsageValues.size() < 61){
					initIndex = 61 - cpuUsageValues.size();
				}
				for(int i=0; i < cpuUsageValues.size()-1; i++){
					if(cpuUsageValues.get(i).size() > 10 && cpuUsageValues.get(i+1).size() > 10){
						BigDecimal value1 = new BigDecimal((Double)cpuUsageValues.get(i).get(10));
						BigDecimal value2 = new BigDecimal((Double)cpuUsageValues.get(i+1).get(10));
						BigDecimal subtractValue = value1.subtract(value2);
						BigDecimal divideValue = subtractValue.divide(new BigDecimal(1000000000L)).divide(new BigDecimal(30), BigDecimal.ROUND_HALF_UP);
						cpuValues[cpuUsageValues.size()-2-i+initIndex] = divideValue.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						cpuTimeList[cpuUsageValues.size()-2-i+initIndex] = new DateTime(cpuUsageValues.get(i).get(0)).toString(dateTimeFormatterPre);  
					}
				}
				//计算时间差值，填充数据源，因为dateTime类使用字符串创建时必须有日期，所以分为两步
				for(int i = initIndex-1; i >=0; i--){
					cpuTimeList[i] = new DateTime(cpuTimeList[i+1]).minusSeconds(30).toString(dateTimeFormatterPre);
				}
				
				for(int i = 0; i < cpuTimeList.length; i++){
					cpuTimeList[i] = new DateTime(cpuTimeList[i]).toString(dateTimeFormatter);
				}
			}
			historyreResourceUsage.setLimit(cpuLimit);
			historyreResourceUsage.setTime(cpuTimeList);
			historyreResourceUsage.setValue(cpuValues);
			historyreResourceUsage.setName(podName);
			histtoryResourceUsages.add(historyreResourceUsage);
			
			historyreResourceUsage = new HisttoryResourceUsage();
			List<List<Object>> memoryUsageValues = getQueryResultValues(memoryUsageUsedSqlPrefix + podName + memoryUsageUsedSqlSuffix,influxDB);
			if(memoryUsageValues != null){
				//当数据不足30分钟的条数时，计算出缺少的，置为0值，并计算出对应的时间
				int initIndex = 0;
				if(memoryUsageValues.size() < 60){
					initIndex = 60 - memoryUsageValues.size();
				}
				for(int i = 0; i < memoryUsageValues.size(); i++){
					if(memoryUsageValues.get(i).size() > 1){
						BigDecimal memoryUsedValue = new BigDecimal((Double)memoryUsageValues.get(i).get(1));
						memoryValues[memoryUsageValues.size()-1-i+initIndex] = memoryUsedValue.divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						memoryTimeList[memoryUsageValues.size()-1-i+initIndex] = new DateTime(memoryUsageValues.get(i).get(0)).toString(dateTimeFormatterPre); 
					}
				}
				//计算时间差值，填充数据源，因为dateTime类使用字符串创建时必须有日期，所以分为两步
				for(int i = initIndex-1; i >=0; i--){
					memoryTimeList[i] = new DateTime(memoryTimeList[i+1]).minusSeconds(30).toString(dateTimeFormatterPre);
				}
				
				for(int i = 0; i < cpuTimeList.length; i++){
					memoryTimeList[i] = new DateTime(memoryTimeList[i]).toString(dateTimeFormatter);
				}
			}
			
			List<List<Object>> memoryLimitValues = getQueryResultValues(memoryUsageLimitSqlPrefix + podName + memoryUsageLimitSqlSuffix,influxDB);
			if(memoryLimitValues != null && memoryLimitValues.size() > 0){
				if(memoryLimitValues.get(0).size() > 2){
					BigDecimal memoryLimitValue = new BigDecimal((Double)memoryLimitValues.get(0).get(1));
					if(memoryLimitValue.compareTo(new BigDecimal(0)) < 0){
						List<List<Object>> machineMemoryLimitValues = getQueryResultValues(machineMemoryUsageLimitSqlPrefix + memoryLimitValues.get(0).get(2) + machineMemoryUsageLimitSqlSuffix,influxDB);
						if(machineMemoryLimitValues != null && machineMemoryLimitValues.size() > 0){
							if(machineMemoryLimitValues.get(0).size() > 1){
								BigDecimal machineMemoryLimitValue = new BigDecimal((Double)machineMemoryLimitValues.get(0).get(1));
								memoryLimit = machineMemoryLimitValue.divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							}
						}
					} else {
						memoryLimit = memoryLimitValue.divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();;
					}
				}
			}
			historyreResourceUsage.setLimit(memoryLimit);
			historyreResourceUsage.setTime(memoryTimeList);
			historyreResourceUsage.setValue(memoryValues);
			histtoryResourceUsages.add(historyreResourceUsage);
		}
		
		return histtoryResourceUsages;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public MonitorAjaxReponse getAllInfo(){
		MonitorAjaxReponse resourceUsagesDetail = getHostResourceUsages();
		
		ResourceUsage resourceUsage = new ResourceUsage();
		double allCpuUsage = 0;
		double allCpuAveliable = 0;
		double allMemoryUsage = 0;
		double allMemoryAveliable = 0;
		if(resourceUsagesDetail.getStatus() != 0){
			return resourceUsagesDetail;
		} else{
			List<ResourceUsage> resourceUsages = (List<ResourceUsage>) resourceUsagesDetail.getData();
			if(resourceUsages.size() > 0){
				for(ResourceUsage ru : resourceUsages){
					allCpuUsage = allCpuUsage + ru.getCpuUsage();
					allCpuAveliable = allCpuAveliable + ru.getCpuAveliable();
					allMemoryUsage = allMemoryUsage + ru.getMemoryUsage();
					allMemoryAveliable = allMemoryAveliable + ru.getMemoryAvelibale();
				}
			}
			resourceUsage.setCpuUsage(new BigDecimal(allCpuUsage).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			resourceUsage.setCpuAveliable(new BigDecimal(allCpuAveliable).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			resourceUsage.setMemoryUsage(new BigDecimal(allMemoryUsage).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			resourceUsage.setMemoryAvelibale(new BigDecimal(allMemoryAveliable).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			
			MonitorAjaxReponse monitorAjaxReponse = new MonitorAjaxReponse(0, "is ok");
			monitorAjaxReponse.setData(resourceUsage);
			return monitorAjaxReponse;
		}
	}
	
	
	//方理增加DCOS监控信息
	
	/*
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public MonitorAjaxReponse getAllDCOSInfo(){
		//MonitorAjaxReponse resourceUsagesDetail = getHostResourceUsages();
		
		ResourceUsage resourceUsage = new ResourceUsage();
		double allCpuUsage = 0;
		double allCpuAveliable = 0;
		double allMemoryUsage = 0;
		double allMemoryAveliable = 0;
		double allDiskUsage = 0;
		double allDiskAveliable = 0;
		if(resourceUsagesDetail.getStatus() != 0){
			return resourceUsagesDetail;
		} else{
			List<ResourceUsage> resourceUsages = (List<ResourceUsage>) resourceUsagesDetail.getData();
			if(resourceUsages.size() > 0){
				for(ResourceUsage ru : resourceUsages){
					allCpuUsage = allCpuUsage + ru.getCpuUsage();
					allCpuAveliable = allCpuAveliable + ru.getCpuAveliable();
					allMemoryUsage = allMemoryUsage + ru.getMemoryUsage();
					allMemoryAveliable = allMemoryAveliable + ru.getMemoryAvelibale();
				}
			}
			resourceUsage.setCpuUsage(new BigDecimal(allCpuUsage).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			resourceUsage.setCpuAveliable(new BigDecimal(allCpuAveliable).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			resourceUsage.setMemoryUsage(new BigDecimal(allMemoryUsage).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			resourceUsage.setMemoryAvelibale(new BigDecimal(allMemoryAveliable).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			
			MonitorAjaxReponse monitorAjaxReponse = new MonitorAjaxReponse(0, "is ok");
			monitorAjaxReponse.setData(resourceUsage);
			return monitorAjaxReponse;
		}
	}*/
	
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<AppAndPubSerInfo> getAppliactionInfo(String user_id){
		List<AppAndPubSerInfo> appAndPubSerInfos = new ArrayList<AppAndPubSerInfo>();
		List<Ku8Project> ku8Projects = projectDao.selectByOwner(user_id);
		if(ku8Projects != null){
			for(Ku8Project ku8Project : ku8Projects){
				AppAndPubSerInfo andPubSerInfo = new AppAndPubSerInfo();
				List<ServiceInfo> serviceInfos = new ArrayList<ServiceInfo>();
				Project project = null;
				if(ku8Project.getJsonSpec() != null){
					project = Project.getFromJSON(ku8Project.getJsonSpec());
				}
				if(project != null && project.getServices() != null){
					for(org.ku8eye.bean.project.Service service : project.getServices()){
						ServiceInfo serviceInfo = new ServiceInfo();
						double allCpuUsage = 0;
						double allCpuAveliable = 0;
						double allMemoryUsage = 0;
						double allMomoryAvelibale = 0;
						MonitorAjaxReponse monitorAjaxReponse = getServiceResourceUsages(service.getName());
						MonitorResponse monitorResponse = (MonitorResponse) monitorAjaxReponse.getData();
						List<ResourceUsage> resourceUsages = monitorResponse.getData();
						for(ResourceUsage resourceUsage : resourceUsages){
							allCpuAveliable += resourceUsage.getCpuAveliable();
							allCpuUsage += resourceUsage.getCpuUsage();
							allMomoryAvelibale += resourceUsage.getMemoryAvelibale();
							allMemoryUsage += resourceUsage.getMemoryUsage();
						}
						serviceInfo.setAllCpuLimit(new BigDecimal(allCpuUsage + allCpuAveliable).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						serviceInfo.setAllCpuUsage(new BigDecimal(allCpuUsage).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						serviceInfo.setAllMemoryLimit(new BigDecimal(allMemoryUsage + allMomoryAvelibale).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						serviceInfo.setAllMemoryUsage(new BigDecimal(allMemoryUsage).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						serviceInfo.setService_name(service.getName());
						serviceInfo.setPod_amount(monitorResponse.getPodAmount());
						serviceInfos.add(serviceInfo);
					}
				}
				andPubSerInfo.setName(ku8Project.getName());
				andPubSerInfo.setServiceInfo(serviceInfos);
				appAndPubSerInfos.add(andPubSerInfo);
			}
		}
		
		return appAndPubSerInfos;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<AppAndPubSerInfo> getPublicServiceInfo(String user_id){
		List<AppAndPubSerInfo> appAndPubSerInfos = new ArrayList<AppAndPubSerInfo>();
		
		List<Ku8Service> ku8Services = serviceDao.selectByOwner(user_id);
		if(ku8Services != null){
			for(Ku8Service ku8Service : ku8Services){
				AppAndPubSerInfo andPubSerInfo = new AppAndPubSerInfo();
				List<ServiceInfo> serviceInfos = new ArrayList<ServiceInfo>();
				org.ku8eye.bean.project.Service service = null;
				if(ku8Service.getJsonSpec() != null){
					service = org.ku8eye.bean.project.Service.getFromJSON(ku8Service.getJsonSpec());
				}
				if(service != null){
					ServiceInfo serviceInfo = new ServiceInfo();
					double allCpuUsage = 0;
					double allCpuAveliable = 0;
					double allMemoryUsage = 0;
					double allMomoryAvelibale = 0;
					MonitorAjaxReponse monitorAjaxReponse = getServiceResourceUsages(service.getName());
					MonitorResponse monitorResponse = (MonitorResponse) monitorAjaxReponse.getData();
					List<ResourceUsage> resourceUsages = monitorResponse.getData();
					for(ResourceUsage resourceUsage : resourceUsages){
						allCpuAveliable += resourceUsage.getCpuAveliable();
						allCpuUsage += resourceUsage.getCpuUsage();
						allMomoryAvelibale += resourceUsage.getMemoryAvelibale();
						allMemoryUsage += resourceUsage.getMemoryUsage();
					}
					serviceInfo.setAllCpuLimit(new BigDecimal(allCpuUsage + allCpuAveliable).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					serviceInfo.setAllCpuUsage(new BigDecimal(allCpuUsage).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					serviceInfo.setAllMemoryLimit(new BigDecimal(allMemoryUsage + allMomoryAvelibale).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					serviceInfo.setAllMemoryUsage(new BigDecimal(allMemoryUsage).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					serviceInfo.setService_name(service.getName());
					serviceInfo.setPod_amount(monitorResponse.getPodAmount());
					serviceInfos.add(serviceInfo);
				}
				andPubSerInfo.setName(ku8Service.getName());
				andPubSerInfo.setServiceInfo(serviceInfos);
				appAndPubSerInfos.add(andPubSerInfo);
			}
		}
		
		return appAndPubSerInfos;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public MonitorAjaxReponse getPartionHostResourceUsages(String group) {
		String cpuUsageSqlPrefix = "select * from \"cpu/usage_ns_cumulative\" where container_name = 'machine' and host_id = '";
		String cpuUsageSqlSuffix = "' order by time desc limit 2";
		
		String memoryUsageUsedSqlPrefix = "select value from \"memory/usage_bytes_gauge\" where container_name = 'machine' and host_id = '";
		String memoryUsageUsedSqlSuffix = "' order by time desc limit 1";
		
		String memoryUsageLimitSqlPrefix = "select value from \"memory/limit_bytes_gauge\" where container_name = 'machine' and host_id = '";
		String memoryUsageLimitSqlSuffix = "' order by time desc limit 1";
		
		List<ResourceUsage> resourceUsages = new ArrayList<ResourceUsage>();
		Map<String, String> partionLabelMap = new HashMap<String,String>();
		partionLabelMap.put(Ku8ResPartionService.NODE_GROUP_NAME_PREFIX + group, group);
		//获取所有node的名字，即ip地址
		List<String> hostIps = new ArrayList<String>();
		try{
			NodeList nodeList = k8sAPIService.getNodeByLabelSelector(0, partionLabelMap);
			if(nodeList != null){
				List<Node> nodes = nodeList.getItems();
				if(nodes != null){
					for(Node node : nodes){
						if(node.getMetadata() != null){
							hostIps.add(node.getMetadata().getName());
						}
					}
				}
			}
		} catch(Exception e){
			log.error("get ku8api nodes error:" + e.getMessage());;
			return new MonitorAjaxReponse(-1, "get ku8 nodes error!");
		}
		
		InfluxDB influxDB = getInfluxDB();
		if(hostIps.size() > 0){
			//从各个node中获取cpu数量和memory数量，默认值设为cpu=24个，memory=260G
			for(String hostIp : hostIps){
				ResourceUsage resourceUsage = new ResourceUsage();
				Node node = null;
				try{
					node = k8sAPIService.getNodeByName(0, hostIp);
				}catch(Exception e){
					log.error("get ku8 node error:" + e.getMessage());
				}
				int cpuAmount = 24;
				double memoryAmount = 260;
				if(node != null && node.getStatus() != null && node.getStatus().getCapacity() != null){
					if(node.getStatus().getCapacity().containsKey("cpu") && node.getStatus().getCapacity().get("cpu") != null){
						String cpuAmountString = node.getStatus().getCapacity().get("cpu").getAmount();
						if(cpuAmountString != null && cpuAmountString.length() > 0){
							cpuAmount = Integer.parseInt(cpuAmountString);
						}
					}
					if(node.getStatus().getCapacity().containsKey("memory") && node.getStatus().getCapacity().get("memory") != null){
						String memoryAmountString = node.getStatus().getCapacity().get("memory").getAmount();
						if(memoryAmountString != null && memoryAmountString.length() > 0){
							memoryAmountString = memoryAmountString.replaceAll("\\D", "");
							memoryAmount =new BigDecimal(memoryAmountString).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).doubleValue();
						}
					}
				}
				
				resourceUsage.setTitle("cpu:" + cpuAmount + " memory:" + memoryAmount + "G" + "\n" + hostIp);
				
				if(influxDB != null){
					//查询主机cpu利用率
					List<List<Object>> cpuUsageValues = getQueryResultValues(cpuUsageSqlPrefix + hostIp + cpuUsageSqlSuffix, influxDB);
					
					//获取查询结果，用两值相减，并转换单位，同时求平均值
					if(cpuUsageValues != null && cpuUsageValues.size() > 1){
						if(cpuUsageValues.get(0).size() > 10 && cpuUsageValues.get(1).size() > 10){
							BigDecimal value1 = new BigDecimal((Double)cpuUsageValues.get(0).get(10));
							BigDecimal value2 = new BigDecimal((Double)cpuUsageValues.get(1).get(10));
							BigDecimal subtractValue = value1.subtract(value2);
							BigDecimal divideValue = subtractValue.divide(new BigDecimal(1000000000L)).divide(new BigDecimal(30), BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(cpuAmount), BigDecimal.ROUND_HALF_UP);
							resourceUsage.setCpuUsage(divideValue.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
							resourceUsage.setCpuAveliable(1-resourceUsage.getCpuUsage());
						}
					}
					
					//查询主机memory利用率
					List<List<Object>> memoryUsageValues = getQueryResultValues(memoryUsageUsedSqlPrefix + hostIp + memoryUsageUsedSqlSuffix,influxDB);
					
					//获取查询结果，并转换单位
					if(memoryUsageValues != null && memoryUsageValues.size() > 0){
						if(memoryUsageValues.get(0).size() > 1){
							BigDecimal memoryUsedValue = new BigDecimal((Double)memoryUsageValues.get(0).get(1));
							double memoryUsedValue2 = memoryUsedValue.divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							resourceUsage.setMemoryUsage(memoryUsedValue2);
						}
					}
					
					//查询主机memory限制
					List<List<Object>> memoryLimitValues = getQueryResultValues(memoryUsageLimitSqlPrefix + hostIp + memoryUsageLimitSqlSuffix,influxDB);
					if(memoryLimitValues != null && memoryLimitValues.size() > 0){
						if(memoryLimitValues.get(0).size() > 1){
							BigDecimal memoryLimitValue = new BigDecimal((Double)memoryLimitValues.get(0).get(1));
							double memoryLimitValue2 = memoryLimitValue.divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(1024),BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();;
							resourceUsage.setMemoryAvelibale(memoryLimitValue2 - resourceUsage.getMemoryUsage());
							
						}
					}
				}
				resourceUsages.add(resourceUsage);
			}
		} else{
			return new MonitorAjaxReponse(1, "ku8 master has no nodes");
		}
		MonitorAjaxReponse monitorAjaxReponse = null;
		if(influxDB == null){
			monitorAjaxReponse = new MonitorAjaxReponse(-2, "connect influxdb failed");
		} else{
			monitorAjaxReponse = new MonitorAjaxReponse(0, "is ok");
		}
		monitorAjaxReponse.setData(resourceUsages);
		return monitorAjaxReponse;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public MonitorAjaxReponse getServiceNameList(User user) {
		
		List<String> service_name_list = new ArrayList<String>();
		List<Ku8Service> ku8Services = null;
		List<Ku8Project> ku8Projects = null;
		
		String userType = user.getUserType();
		
		if(Constants.USERTYPE_ADMIN.equals(userType)){
			ku8Services = serviceAndRcService.getAllServiceAndRc();
			ku8Projects = applicationService.getApplications();
		} else if (Constants.USERTYPE_TENANT.equals(userType)) {
//			ku8Services = serviceAndRcService.getAllServiceAndRc(user.getUserId());
			ku8Projects = applicationService.getApplications(user.getUserId());
		} else {
			return new MonitorAjaxReponse(-1, "user not allowed !");
		}
		
		if(ku8Services != null){
			for(Ku8Service ku8Service : ku8Services){
				org.ku8eye.bean.project.Service service = JSONUtil.toObject(ku8Service.getJsonSpec(), org.ku8eye.bean.project.Service.class);
				if(service != null && !service_name_list.contains(service.getName()) && Constants.KU8_APP_DEPLOYED_STATUS == ku8Service.getStatus()){
					service_name_list.add(service.getName());
				}
			}
		}
		
		if(ku8Projects != null){
			for(Ku8Project ku8Project : ku8Projects){
				Project project = JSONUtil.toObject(ku8Project.getJsonSpec(), Project.class);
				List<org.ku8eye.bean.project.Service> services = project.getServices();
				if(services != null){
					for(org.ku8eye.bean.project.Service service : services){
						if(!service_name_list.contains(service.getName()) && Constants.KU8_APP_DEPLOYED_STATUS == ku8Project.getStatus()){
							service_name_list.add(service.getName());
						}
					}
				}
			}
		}
		MonitorAjaxReponse monitorAjaxReponse = new MonitorAjaxReponse(0, "ok");
		monitorAjaxReponse.setData(service_name_list);
		return monitorAjaxReponse;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public MonitorAjaxReponse getServicePodAmountByService(String service_name){
		
		Map<String,List<String>> partionPodsMap = new HashMap<String, List<String>>();
		io.fabric8.kubernetes.api.model.Service service = null;
		List<String> namesapce_names = null;
		try {
			namesapce_names = ku8ResPartionService.getAllNameSpaceName(0);
		} catch (Exception e) {
			log.error("get ku8 namespaces error: " + e.getMessage());
			return new MonitorAjaxReponse(-1, "ku8 error");
		}
		if(namesapce_names != null){
			for(String namespace_name : namesapce_names){
				try {
					service = k8sAPIService.getServicesByName(0, namespace_name, service_name);
				} catch (Exception e) {
					log.error("get service info for eCharts error: " + e.getMessage());
					return new MonitorAjaxReponse(-1, "ku8 error");
				}
				if(service != null){
					if(service.getMetadata() != null && !"kubernetes".equals(service.getMetadata().getName())){
						if(service.getSpec() != null && service.getSpec().getSelector() != null){
							PodList podList = null;
							try {
								podList = k8sAPIService.getPodsByLabelsSelector(0, namespace_name, service.getSpec().getSelector());
							} catch (Exception e) {
								log.error("get pod info for eCharts error: " + e.getMessage());
								return new MonitorAjaxReponse(-1, "ku8 error");
							}
							if(podList != null){
								List<Pod> podItems = podList.getItems();
								if(podItems != null && podItems.size() > 0){
									List<String> podNames = new ArrayList<String>();
									for(Pod podItem : podItems){
										if(podItem.getMetadata() != null){
											podNames.add(podItem.getMetadata().getName());
										}
									}
									partionPodsMap.put(namespace_name, podNames);
								}
							}
						}
					}
				}
			}
		}
		MonitorAjaxReponse monitorAjaxReponse = new MonitorAjaxReponse(0, "ok");
		monitorAjaxReponse.setData(partionPodsMap);
		return monitorAjaxReponse;
		
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<SystemEvent> getSystemEventsInfo(){
		List<SystemEvent> systemEvents = new ArrayList<SystemEvent>();
		EventList eventList = null;
		try {
			eventList = k8sAPIService.getEvents(0);
		} catch (Exception e) {
			log.error("get ku8 events error: " + e.getMessage());
		}
		if(eventList != null){
			List<Event> events = eventList.getItems();
			if(events != null){
				for(Event event : events){
					SystemEvent systemEvent = new SystemEvent();
					systemEvent.setCount(event.getCount() + "");
					systemEvent.setFirstSeen(event.getFirstTimestamp());
					systemEvent.setKind(event.getInvolvedObject().getKind());
					systemEvent.setLastSeen(event.getLastTimestamp());
					systemEvent.setMessage(event.getMessage());
					systemEvent.setName(event.getInvolvedObject().getName());
					systemEvent.setReason(event.getReason());
					systemEvent.setSource("{" + event.getSource().getComponent() + " " + event.getSource().getHost() + "}");
					systemEvent.setSubObject(event.getInvolvedObject().getFieldPath());
					systemEvents.add(systemEvent);
				}
			}
		}
		return systemEvents;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<List<Object>> getQueryResultValues(String querySql, InfluxDB dbClient){
		InfluxDB influxDB = dbClient;
		if(influxDB != null){
			Query query = new Query(querySql, "k8s");
			QueryResult queryResult = influxDB.query(query);
			List<Result> queryResults = queryResult.getResults();
			if(queryResults != null){
					List<Series> seriess = queryResults.get(0).getSeries();
					if(seriess != null){
							 return seriess.get(0).getValues();
					}
			}
		}
		return null;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public InfluxDB getInfluxDB(){
		InfluxDB influxDB = null;
		try {
//			System.setProperty("http.proxyHost", "10.1.128.200");  
//			System.setProperty("http.proxyPort", "9000");  
			influxDB = InfluxDBFactory.connect(k8sAPIService.fetchMasterURLFromDB(1), "root", "root");
			influxDB.setReadTimeout(30000, TimeUnit.MILLISECONDS);
			influxDB.setConnectTimeout(30000, TimeUnit.MILLISECONDS);
			return influxDB;
		} catch (Exception e) {
			log.error("get influxdb connection error: " + e.getMessage());
			return null;
		}
	}
}