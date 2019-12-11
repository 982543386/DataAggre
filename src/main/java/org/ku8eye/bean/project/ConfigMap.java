package org.ku8eye.bean.project;

public class ConfigMap
{
	private String name;
	private String value;
	private String filename;
	
	public ConfigMap() {}
	
	public ConfigMap(String name, String value, String filename)
	{
		this.name = name;
		this.value = value;
		this.filename = filename;
	}

	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}
