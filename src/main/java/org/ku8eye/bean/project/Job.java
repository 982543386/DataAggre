package org.ku8eye.bean.project;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ku8eye.util.JSONUtil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@JsonIgnoreProperties(ignoreUnknown = true) 
public class Job {
	private String name;
	private String notes;
	private String ku8Version;
	private String resPart;
	private int parallelism;
	private Map<String, String> labels;
	private Container[] containers;
	
	private static final Logger log = Logger.getLogger(Job.class);
	
	public static Job getFromJSON(String json)
	{
		try
		{
			return JSONUtil.mapper.readValue(json, Job.class);
		}
		catch (JsonParseException e)
		{
			log.error("Couldn't parse Job from String, " + e);
		}
		catch (JsonMappingException e)
		{
			log.error("Json mapping error in Job, " + e);
		}
		catch (IOException e)
		{
			log.error("IO Error in Job, " + e);
		}
		return null;
	}
	
	public String toJSONString()
	{
		return JSONUtil.getJSONString(this);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getNotes()
	{
		return notes;
	}

	public void setNotes(String notes)
	{
		this.notes = notes;
	}

	public String getKu8Version()
	{
		return ku8Version;
	}

	public void setKu8Version(String ku8Version)
	{
		this.ku8Version = ku8Version;
	}

	public Map<String, String> getLabels()
	{
		return labels;
	}

	public void setLabels(Map<String, String> labels)
	{
		this.labels = labels;
	}

	public Container[] getContainers()
	{
		return containers;
	}

	public void setContainers(Container[] containers)
	{
		this.containers = containers;
	}

	public String getResPart()
	{
		return resPart;
	}

	public void setResPart(String resPart)
	{
		this.resPart = resPart;
	}

	public int getParallelism()
	{
		return parallelism;
	}

	public void setParallelism(int parallelism)
	{
		this.parallelism = parallelism;
	}
}

