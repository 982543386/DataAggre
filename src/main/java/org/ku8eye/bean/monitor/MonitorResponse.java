package org.ku8eye.bean.monitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MonitorResponse implements Serializable{

	private static final long serialVersionUID = 1L;
	private String service_name;
	private int podAmount = 0;
	private List<ResourceUsage> data = new ArrayList<ResourceUsage>();
	
	public int getPodAmount() {
		return podAmount;
	}
	public void setPodAmount(int podAmount) {
		this.podAmount = podAmount;
	}
	public List<ResourceUsage> getData() {
		return data;
	}
	public void setData(List<ResourceUsage> data) {
		this.data = data;
	}
	
	public String getService_name() {
		return service_name;
	}
	public void setService_name(String service_name) {
		this.service_name = service_name;
	}
	
	@Override
	public String toString(){
		return "MonitorResponse [podAmount=" + podAmount + ", data=" + data + ", service_name=" + service_name + "]";
	}
}
