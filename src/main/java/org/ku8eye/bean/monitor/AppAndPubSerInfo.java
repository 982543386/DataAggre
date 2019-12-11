package org.ku8eye.bean.monitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppAndPubSerInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String name=null;
	private List<ServiceInfo> serviceInfo = new ArrayList<ServiceInfo>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ServiceInfo> getServiceInfo() {
		return serviceInfo;
	}
	public void setServiceInfo(List<ServiceInfo> serviceInfo) {
		this.serviceInfo = serviceInfo;
	}

	@Override
	public String toString(){
		return "AppAndPubSerInfo [name=" + name + ", serviceInfo=" + serviceInfo + "]";
	}
}
