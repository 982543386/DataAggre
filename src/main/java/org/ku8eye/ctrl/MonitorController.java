package org.ku8eye.ctrl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.ku8eye.bean.GridData;
import org.ku8eye.bean.monitor.AppAndPubSerInfo;
import org.ku8eye.bean.monitor.HisttoryResourceUsage;
import org.ku8eye.bean.monitor.MonitorAjaxReponse;
import org.ku8eye.bean.monitor.NodeInfo;
import org.ku8eye.bean.monitor.ResourceUsage;
import org.ku8eye.bean.monitor.SystemEvent;
import org.ku8eye.domain.User;
import org.ku8eye.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cudcos.util.CacheDcosUrlConfig;
import com.cudcos.util.CallRestUtil;
import com.cudcos.util.CallResult;

@RestController
@SessionAttributes("user")
public class MonitorController {
	private Logger log = Logger.getLogger(this.toString());

	@Autowired
	private MonitorService monitorService;
	
	//host view
	@RequestMapping(value = "/monitor/host/amount")
	public MonitorAjaxReponse getHostAmount(ModelMap model) {
		return monitorService.getHostAmount();
	}
	
	@RequestMapping(value = "/monitor/host/allInfo/{hostIP}")
	public MonitorAjaxReponse getHostAllInfo(@PathVariable("hostIP") String hostIP, ModelMap model) {
		return monitorService.getHostAllInfo(hostIP);
	}
	
	@RequestMapping(value = "/monitor/host/{hostIP}")
	public List<HisttoryResourceUsage> getHostResourceUsagesByIp(@PathVariable("hostIP") String hostIP, ModelMap model) {
		return monitorService.getHostResourceUsages(hostIP);
	}
	
	//
	@RequestMapping(value = "/monitor/partionGroup/{group}")
	public MonitorAjaxReponse getHostResourceUsages(@PathVariable("group") String group, ModelMap model) {
		return monitorService.getPartionHostResourceUsages(group);
	}
	
	//dashboard
	@RequestMapping(value = "/monitor/all")
	public MonitorAjaxReponse getAllInfo(ModelMap model) {
		//return monitorService.getAllInfo();

		
		ResourceUsage resourceUsage = new ResourceUsage();		
		try {
			String monitorpath=CacheDcosUrlConfig.dcosApiUri+"/dcos-history-service/history/last?_timestamp="+Calendar.getInstance().getTimeInMillis();
			CallResult callResult = CallRestUtil.callSendJsonObject(monitorpath, HttpMethod.GET, null, null, null);
			System.out.println("=================="+monitorpath);
			System.out.println(callResult);
			String body = callResult.getBody();
			JSONObject jSONObject = JSONObject.parseObject(body);
			JSONArray jSONArray = jSONObject.getJSONArray("slaves");
			double  disk= 0;
			double  mem= 0;
			double cpus= 0;
			Iterator<Object> it = jSONArray.iterator();
		    while (it.hasNext()) {
		    	JSONObject ob = (JSONObject)it.next();
		    	JSONObject cob=(JSONObject)ob.get("resources");
		    	BigDecimal tempdisk=cob.getBigDecimal("disk");
		    	BigDecimal tempmem=cob.getBigDecimal("mem");
		    	BigDecimal tempcpus=cob.getBigDecimal("cpus");
		    	disk+=Double.valueOf(tempdisk.toString());
		    	mem+=Double.valueOf(tempmem.toString());
		    	cpus+=Double.valueOf(tempcpus.toString());
		    	
		    }
		  
		    //used_resources
		    double  useddisk= 0;
			double  usedmem= 0;
			double usedcpus= 0;
			Iterator<Object> usedit = jSONArray.iterator();
		    while (null != usedit && usedit.hasNext()) {
		    	JSONObject ob = (JSONObject)usedit.next();
		    	JSONObject cob=(JSONObject)ob.get("used_resources");
		    	BigDecimal tempdisk=cob.getBigDecimal("disk");
		    	BigDecimal tempmem=cob.getBigDecimal("mem");
		    	BigDecimal tempcpus=cob.getBigDecimal("cpus");
		    	useddisk+=Double.valueOf(tempdisk.toString());
		    	usedmem+=Double.valueOf(tempmem.toString());
		    	usedcpus+=Double.valueOf(tempcpus.toString());
		    	
		    	
		    }
			resourceUsage.setCpuUsage(usedcpus);
			resourceUsage.setCpuAveliable(cpus-usedcpus);
			resourceUsage.setDiskUsage(useddisk/(1024*1024));
			resourceUsage.setDiskAvelibale((disk-useddisk)/(1024*1024));
			resourceUsage.setMemoryUsage(usedmem/1024);
			resourceUsage.setMemoryAvelibale((mem-usedmem)/1024);
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MonitorAjaxReponse monitorAjaxReponse = new MonitorAjaxReponse(0, "is ok");
		monitorAjaxReponse.setData(resourceUsage);
		return monitorAjaxReponse;
	
	}
	
	@RequestMapping(value = "/monitor/dcosnodes")
	public List<NodeInfo> getNodesInfo(ModelMap model) {
		
		List <NodeInfo> list = new ArrayList<NodeInfo>();
		
		try {
			CallResult callResult = CallRestUtil.callSendJsonObject(CacheDcosUrlConfig.dcosApiUri+"/dcos-history-service/history/last?_timestamp="+Calendar.getInstance().getTimeInMillis(), HttpMethod.GET, null, null, null);
			//CallResult acallResult = CallRestUtil.doCall(dcosuri+"/system/health/v1/nodes?_timestamp="+Calendar.getInstance().getTimeInMillis(), HttpMethod.GET, null, null, null);
			System.out.println("==============="+CacheDcosUrlConfig.dcosApiUri+"/dcos-history-service/history/last?_timestamp="+Calendar.getInstance().getTimeInMillis());
			System.out.println("==============="+callResult);
			String body = callResult.getBody();
			JSONObject jSONObject = JSONObject.parseObject(body);
			JSONArray jSONArray = jSONObject.getJSONArray("slaves");
		
			Iterator<Object> it = jSONArray.iterator();
		    while (null != it && it.hasNext()) {
		    	NodeInfo Info = new NodeInfo();
		    	ResourceUsage resourceUsage = new ResourceUsage();		
		    	JSONObject ob = (JSONObject)it.next();
		    	JSONObject cob=(JSONObject)ob.get("resources");
		    	BigDecimal tempdisk=cob.getBigDecimal("disk");
		    	BigDecimal tempmem=cob.getBigDecimal("mem");
		    	BigDecimal tempcpus=cob.getBigDecimal("cpus");

		    	resourceUsage.setCpuAveliable(Double.valueOf(tempcpus.toString()));
		    	resourceUsage.setDiskAvelibale(Double.valueOf(tempdisk.toString())/(1024*1024));
		    	resourceUsage.setMemoryAvelibale(Double.valueOf(tempmem.toString())/1024);
		  
				Iterator<Object> usedit = jSONArray.iterator();
			    while (usedit.hasNext()) {
			    	JSONObject usedob = (JSONObject)usedit.next();
			    	JSONObject usedcob=(JSONObject)usedob.get("used_resources");
			    	BigDecimal usedisk=usedcob.getBigDecimal("disk");
			    	BigDecimal usedpmem=usedcob.getBigDecimal("mem");
			    	BigDecimal usedcpus=usedcob.getBigDecimal("cpus");
			    	
			    	resourceUsage.setCpuUsage(Double.valueOf(usedcpus.toString()));
					resourceUsage.setDiskUsage(Double.valueOf(usedisk.toString())/(1024*1024));
					
					resourceUsage.setMemoryUsage(Double.valueOf(usedpmem.toString())/1024);
					
			    }
			    
			   Info.setResourceUsage(resourceUsage);
		    	
			    
			   Info.setNodeIP(ob.get("hostname").toString());
			   Info.setHealth("0");
			   Info.setRole("slave");
				
				list.add(Info);
		    	
		    }
		  
		
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return list;
	}
	
	
	@RequestMapping(value = "/monitor/application")
	public List<AppAndPubSerInfo> getAppliactionInfo(ModelMap model) {
		User user = (User) model.get("user");
		return monitorService.getAppliactionInfo(user.getUserId());
	}
	
	@RequestMapping(value = "/monitor/publicService")
	public List<AppAndPubSerInfo> getPublicServiceInfo(ModelMap model) {
		User user = (User) model.get("user");
		return monitorService.getPublicServiceInfo(user.getUserId());
	}
	
	@RequestMapping(value = "/monitor/systemEvents")
	public GridData getSystemEventsInfo(ModelMap model) {
		GridData grid = new GridData();
		List<SystemEvent> systemEvents = monitorService.getSystemEventsInfo();
		grid.setData(systemEvents);
		return grid;
	}
	
	//service view
	@RequestMapping(value = "/monitor/service")
	public MonitorAjaxReponse getServiceNameList(ModelMap model) {
		User user = (User) model.get("user");
		return monitorService.getServiceNameList(user);
	}
	
	@RequestMapping(value = "/monitor/service/{pod_name}")//可能修改为/monitor/service/{pod_name}
	public MonitorAjaxReponse getServiceResourceUsagesByService(@PathVariable("pod_name") String pod_name, ModelMap model) {
		return monitorService.getServicePodResourceUsages(pod_name);
	}
	
	@RequestMapping(value = "/monitor/service/amount/{service_name}")
	public MonitorAjaxReponse getServicePodAmountByService(@PathVariable("service_name") String serviceName, ModelMap model) {
		return monitorService.getServicePodAmountByService(serviceName);
	}
	
	@RequestMapping(value = "/monitor/pod/{podName}")
	public List<HisttoryResourceUsage> getPodResourceUsagesByIp(@PathVariable("podName") String podName, ModelMap model) {
		return monitorService.getPodResourceUsages(podName);
	}
}
