package org.ku8eye.bean.respartion;

public class NodeView
{
	private String name;
	private int cpuLimit;
	private int memoryLimit;
	private int podLimit;
	private boolean checked;
	public boolean isChecked()
	{
		return checked;
	}
	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}
	public String getName()
	{
		return name;
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
	public void setName(String name)
	{
		this.name = name;
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
}
