package org.ku8eye.bean.project;

public class Pod
{
	private String name;
	private String pod_ip;
	private String host_ip;
	private String status;
	private String statusDetails;
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getPod_ip()
	{
		return pod_ip;
	}
	
	public void setPod_ip(String pod_ip)
	{
		this.pod_ip = pod_ip;
	}
	
	public String getHost_ip()
	{
		return host_ip;
	}
	
	public void setHost_ip(String host_ip)
	{
		this.host_ip = host_ip;
	}
	
	public String getStatus()
	{
		return status;
	}
	
	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getStatusDetails()
	{
		return statusDetails;
	}

	public void setStatusDetails(String statusDetails)
	{
		this.statusDetails = statusDetails;
	}
}
