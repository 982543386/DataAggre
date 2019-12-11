package org.ku8eye.bean.project;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.ku8eye.util.JSONUtil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@JsonIgnoreProperties(ignoreUnknown = true) 
public class JobSet
{
	private static final Logger log = Logger.getLogger(JobSet.class);

	private String jobName;
	private String version;
	private String author;
	private String notes;
	private Job jobs[];
	
	public static JobSet getFromJSON(String json)
	{
		try
		{
			return JSONUtil.mapper.readValue(json, JobSet.class);
		}
		catch (JsonParseException e)
		{
			log.error("Couldn't parse JobSet from String, " + e);
		}
		catch (JsonMappingException e)
		{
			log.error("Json mapping error in JobSet, " + e);
		}
		catch (IOException e)
		{
			log.error("IO Error in JobSet, " + e);
		}
		return null;
	}
	
	public String toJSONString()
	{
		return JSONUtil.getJSONString(this);
	}

	public String getJobName()
	{
		return jobName;
	}

	public void setJobName(String jobName)
	{
		this.jobName = jobName;
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

	public Job[] getJobs()
	{
		return jobs;
	}

	public void setJobs(Job[] jobs)
	{
		this.jobs = jobs;
	}
}
