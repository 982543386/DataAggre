package org.ku8eye.bean.monitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServiceInfo implements Serializable{

	private static final long serialVersionUID = 1L;

	private String service_name = null;
	private List<String> pod_names = new ArrayList<String>();
	private int pod_amount = 0;
	private double allCpuUsage = 0;
	private double allCpuLimit = 100;
	private double allMemoryUsage = 0;
	private double allMemoryLimit = 100;
	
	public String getService_name() {
		return service_name;
	}
	public void setService_name(String service_name) {
		this.service_name = service_name;
	}
	public List<String> getPod_names() {
		return pod_names;
	}
	public void setPod_names(List<String> pod_names) {
		this.pod_names = pod_names;
	}
	public double getAllCpuUsage() {
		return allCpuUsage;
	}
	public void setAllCpuUsage(double allCpuUsage) {
		this.allCpuUsage = allCpuUsage;
	}
	public double getAllCpuLimit() {
		return allCpuLimit;
	}
	public void setAllCpuLimit(double allCpuLimit) {
		this.allCpuLimit = allCpuLimit;
	}
	public double getAllMemoryUsage() {
		return allMemoryUsage;
	}
	public void setAllMemoryUsage(double allMemoryUsage) {
		this.allMemoryUsage = allMemoryUsage;
	}
	public double getAllMemoryLimit() {
		return allMemoryLimit;
	}
	public void setAllMemoryLimit(double allMemoryLimit) {
		this.allMemoryLimit = allMemoryLimit;
	}
	public int getPod_amount() {
		return pod_amount;
	}
	public void setPod_amount(int pod_amount) {
		this.pod_amount = pod_amount;
	}
	
	@Override
	public String toString(){
		return "ServiceInfo [service_name=" + service_name + ", pod_names=" + pod_names + ", allCpuUsage=" + allCpuUsage + ", allCpuLimit=" + allCpuLimit + ", allMemoryUsage=" + allMemoryUsage + ", allMemoryLimit=" + allMemoryLimit + ", pod_amount=" + pod_amount + "]";
	}
}
