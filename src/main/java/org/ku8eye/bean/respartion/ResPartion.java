package org.ku8eye.bean.respartion;

public class ResPartion
{
	private String groupName;
	private int nodeAccount;
	private int cpuLimit;
	private int memoryLimit;
	private int podLimit;
	public String getGroupName()
	{
		return groupName;
	}
	public int getCpuLimit()
	{
		return cpuLimit;
	}
	public int getMemoryLimit()
	{
		return memoryLimit;
	}
	public int getPodLimit()
	{
		return podLimit;
	}
	public void setGroupName(String groupName)
	{
		this.groupName = groupName;
	}
	public void setCpuLimit(int cpuLimit)
	{
		this.cpuLimit = cpuLimit;
	}
	public void setMemoryLimit(int memoryLimit)
	{
		this.memoryLimit = memoryLimit;
	}
	public void setPodLimit(int podLimit)
	{
		this.podLimit = podLimit;
	}
	public int getNodeAccount()
	{
		return nodeAccount;
	}
	public void setNodeAccount(int nodeAccount)
	{
		this.nodeAccount = nodeAccount;
	}
}
