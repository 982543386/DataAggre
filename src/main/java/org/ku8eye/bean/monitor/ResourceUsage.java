package org.ku8eye.bean.monitor;

import java.io.Serializable;

public class ResourceUsage implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private double cpuUsage=0.00;
	private double cpuAveliable=1.00;
	private double memoryUsage=0.00;
	private double memoryAvelibale=100.00;
	
	
	//增加disk
	private double diskUsage=0.00;
	private double diskAvelibale=100.00;
	
	private String title="no title";
	private String partionName = null;
	
	public double getCpuUsage() {
		return cpuUsage;
	}
	public void setCpuUsage(double cpuUsage) {
		this.cpuUsage = cpuUsage;
	}
	public double getCpuAveliable() {
		return cpuAveliable;
	}
	public void setCpuAveliable(double cpuAveliable) {
		this.cpuAveliable = cpuAveliable;
	}
	public double getMemoryUsage() {
		return memoryUsage;
	}
	public void setMemoryUsage(double memoryUsage) {
		this.memoryUsage = memoryUsage;
	}
	public double getMemoryAvelibale() {
		return memoryAvelibale;
	}
	public void setMemoryAvelibale(double memoryAvelibale) {
		this.memoryAvelibale = memoryAvelibale;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getPartionName() {
		return partionName;
	}
	public void setPartionName(String partionName) {
		this.partionName = partionName;
	}
	
	//方理添加硬盘监控指标
	public double getDiskUsage() {
		return diskUsage;
	}
	public void setDiskUsage(double diskUsage) {
		this.diskUsage = diskUsage;
	}
	public double getDiskAvelibale() {
		return diskAvelibale;
	}
	public void setDiskAvelibale(double diskAvelibale) {
		this.diskAvelibale = diskAvelibale;
	}
	@Override
	public String toString(){
		return "ResourceUsage [cpuUsage=" + cpuUsage + ", cpuAveliable=" + cpuAveliable + ", memoryUsage=" + memoryUsage + ", memoryAveliable=" + memoryAvelibale + ", title=" + title + ", partionName=" + partionName + "]";
	}
}
