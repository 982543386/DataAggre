package org.ku8eye.bean.project;

public class LivenessProbe
{
	private String type;
	private String path;
	private int port;
	private String command;
	private int initialDelaySeconds;
	private int timeoutSeconds;
	
	public String getType()
	{
		return type;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	public int getInitialDelaySeconds()
	{
		return initialDelaySeconds;
	}
	
	public void setInitialDelaySeconds(int initialDelaySeconds)
	{
		this.initialDelaySeconds = initialDelaySeconds;
	}
	
	public int getTimeoutSeconds()
	{
		return timeoutSeconds;
	}
	
	public void setTimeoutSeconds(int timeoutSeconds)
	{
		this.timeoutSeconds = timeoutSeconds;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public String getCommand()
	{
		return command;
	}

	public void setCommand(String command)
	{
		this.command = command;
	}
}
