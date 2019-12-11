package org.ku8eye.bean.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ku8eye.util.JSONUtil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@JsonIgnoreProperties(ignoreUnknown = true) 
public class Project
{
	private static final Logger log = Logger.getLogger(Project.class);

	private String projectName;
	private String version;
	private String author;
	private String notes;
	private String resPart;
	private List<Service> services = new ArrayList<Service>();
	
	public static Project getFromJSON(String json)
	{
		try
		{
			return JSONUtil.mapper.readValue(json, Project.class);
		}
		catch (JsonParseException e)
		{
			log.error("Couldn't parse Project from String, " + e);
		}
		catch (JsonMappingException e)
		{
			log.error("Json mapping error in Project, " + e);
		}
		catch (IOException e)
		{
			log.error("IO Error in Project, " + e);
		}
		return null;
	}
	
	public String toJSONString()
	{
		return JSONUtil.getJSONString(this);
	}

	public void addService(Service s)
	{
		services.add(s);
	}
	
	public String getProjectName()
	{
		return projectName;
	}

	public void setProjectName(String projectName)
	{
		this.projectName = projectName;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getAuthor()
	{
		return author;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public String getNotes()
	{
		return notes;
	}

	public void setNotes(String notes)
	{
		this.notes = notes;
	}

	public List<Service> getServices()
	{
		return services;
	}

	public void setServices(List<Service> services)
	{
		this.services = services;
	}

	public String getResPart()
	{
		return resPart;
	}

	public void setResPart(String resPart)
	{
		this.resPart = resPart;
	}
}
