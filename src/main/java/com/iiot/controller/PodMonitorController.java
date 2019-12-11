package com.iiot.controller;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.alibaba.fastjson.JSONObject;
import com.iiot.edgenode.entity.Message;
import com.iiot.edgenode.entity.MonitorRes;
import com.iiot.service.PodMonitorService;
import com.iiot.utils.HttpClientUtils;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodCondition;
import io.ku8.docker.registry.HTTPCallResult;

@RestController
@RequestMapping("/podMonitor")
@SessionAttributes(org.ku8eye.Constants.USER_SESSION_KEY)
public class PodMonitorController {

	@Autowired
	private PodMonitorService podMonitorService;
	@Value("${iiot.prometheus.url}")
	private String promeURL;
	
	@RequestMapping("/getPodsList")
	public Message getPodsList(HttpServletRequest request) {
		Message message = new Message();
		String serviceName = request.getParameter("appName");
		List<Pod> podlist = null;
		try {
			podlist = podMonitorService.getPodListByServiceName(serviceName);
			if(podlist != null){
				List<String> pods = new ArrayList<String>();
				for(Pod pod: podlist) {
					List<PodCondition> podConditions = pod.getStatus().getConditions();
					for(PodCondition condition : podConditions) {
						if(condition.getType().equals("Ready")) {
							if(!condition.getStatus().equals("False")) {
								pods.add(pod.getMetadata().getName());
							}
						}
					}
				}
				if(pods.size()!=0) {
					message.setFlag(true);
				    message.setMsg("success");
				    message.setType("1");
				    message.setResult(pods);
				}else {
					message.setFlag(true);
				    message.setMsg("all pods' status are false");
				    message.setType("2");
				}
			}else {
				message.setFlag(true);
			    message.setMsg("pod list is null");
			    message.setType("3");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			message.setFlag(false);
			message.setMsg("出錯啦！");
		}
		return message;
	}
	
	@RequestMapping("/podCPUDetail")
	public Message podCPUDetail(HttpServletRequest request) {
		Message message = new Message();
		String curpod = request.getParameter("curpod");
		long timeGap = Long.valueOf(request.getParameter("timeGap"));
		try {
			long current = System.currentTimeMillis();
			String query = URLEncoder.encode("sum by (container_name)(rate(container_cpu_usage_seconds_total{image!=\"\",container_name!=\"POD\",pod_name=\""+curpod+"\"}[1m]))", "utf-8");
			String url = promeURL+"/api/v1/query_range?query="+query+"&start="+(current-timeGap)/1000+"&end="+current/1000+"&step=30";
			HTTPCallResult res = new HttpClientUtils().httpGet(url, null, 3000);
			JSONObject jsonObject = JSONObject.parseObject(res.getContent());
			int size = jsonObject.getJSONObject("data").getJSONArray("result").getJSONObject(0).getJSONArray("values").size();
			String tmp = "";
			List<MonitorRes> mrlist = new ArrayList<>();
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			for(int i=0; i<size; i++) {
				MonitorRes mRes = new MonitorRes();
				tmp = jsonObject.getJSONObject("data").getJSONArray("result").getJSONObject(0).getJSONArray("values").getJSONArray(i).toString();
				String[] ret = tmp.substring(1, tmp.length()-1).split(",");
				mRes.setTime(formatter.format(Long.valueOf(ret[0])*1000));
				mRes.setRes(String.format("%.4f", Double.valueOf(ret[1].substring(1, ret[1].length()-1))*100));
				mrlist.add(mRes);
			}
			message.setFlag(true);
		    message.setMsg("success");
		    message.setResult(mrlist);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			message.setFlag(false);
			message.setMsg("出錯啦！");
		}
		return message;
	}
	
	@RequestMapping("/podMemoryDetail")
	public Message podMemoryDetail(HttpServletRequest request) {
		Message message = new Message();
		String curpod = request.getParameter("curpod");
		long timeGap = Long.valueOf(request.getParameter("timeGap"));
		try {
			long current = System.currentTimeMillis();
			String query = URLEncoder.encode("sum by(container_name) (container_memory_usage_bytes{pod_name=\""+curpod+"\", container_name=\"\"})", "utf-8");
			String url = promeURL+"/api/v1/query_range?query="+query+"&start="+(current-timeGap)/1000+"&end="+current/1000+"&step=30";
			HTTPCallResult res = new HttpClientUtils().httpGet(url, null, 3000);
			JSONObject jsonObject = JSONObject.parseObject(res.getContent());
			int size = jsonObject.getJSONObject("data").getJSONArray("result").getJSONObject(0).getJSONArray("values").size();
			String tmp = "";
			List<MonitorRes> mrlist = new ArrayList<>();
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			for(int i=0; i<size; i++) {
				MonitorRes mRes = new MonitorRes();
				tmp = jsonObject.getJSONObject("data").getJSONArray("result").getJSONObject(0).getJSONArray("values").getJSONArray(i).toString();
				String[] ret = tmp.substring(1, tmp.length()-1).split(",");
				mRes.setTime(formatter.format(Long.valueOf(ret[0])*1000));
				mRes.setRes(String.format("%.2f", Double.valueOf(ret[1].substring(1, ret[1].length()-1))/1024/1024));
				mrlist.add(mRes);
			}
			message.setFlag(true);
		    message.setMsg("success");
		    message.setResult(mrlist);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			message.setFlag(false);
			message.setMsg("出錯啦！");
		}
		return message;
	}
	
	@RequestMapping("/podIODetail")
	public Message podIODetail(HttpServletRequest request) {
		Message message = new Message();
		String curpod = request.getParameter("curpod");
		long timeGap = Long.valueOf(request.getParameter("timeGap"));
		try {
			long current = System.currentTimeMillis();
			String query = URLEncoder.encode("sort_desc(sum by (pod_name) (rate(container_network_receive_bytes_total{pod_name=\""+curpod+"\"}[1m])))", "utf-8");
			String url = promeURL+"/api/v1/query_range?query="+query+"&start="+(current-timeGap)/1000+"&end="+current/1000+"&step=30";
			HTTPCallResult res = new HttpClientUtils().httpGet(url, null, 3000);
			JSONObject jsonObject = JSONObject.parseObject(res.getContent());
			int size = jsonObject.getJSONObject("data").getJSONArray("result").getJSONObject(0).getJSONArray("values").size();
			String tmp = "";
			List<MonitorRes> mrlist = new ArrayList<>();
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			for(int i=0; i<size; i++) {
				MonitorRes mRes = new MonitorRes();
				tmp = jsonObject.getJSONObject("data").getJSONArray("result").getJSONObject(0).getJSONArray("values").getJSONArray(i).toString();
				String[] ret = tmp.substring(1, tmp.length()-1).split(",");
				mRes.setTime(formatter.format(Long.valueOf(ret[0])*1000));
				mRes.setRes(String.format("%.2f", Double.valueOf(ret[1].substring(1, ret[1].length()-1))));
				mrlist.add(mRes);
			}
			message.setFlag(true);
		    message.setMsg("success");
		    message.setResult(mrlist);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			message.setFlag(false);
			message.setMsg("出錯啦！");
		}
		return message;
	}
	
}
