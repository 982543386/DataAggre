package org.ku8eye.bean.project;

import java.util.List;

public class ClusterNamepace
{
	private int clusterID;
	private String clusterName;
	private List<String> namespace;

	public ClusterNamepace(List<String> namespace) {
		this.namespace = namespace;
	}

	public int getClusterID()
	{
		return clusterID;
	}

	public void setClusterID(int clusterID)
	{
		this.clusterID = clusterID;
	}

	public String getClusterName()
	{
		return clusterName;
	}

	public void setClusterName(String clusterName)
	{
		this.clusterName = clusterName;
	}

	public List<String> getNamespace()
	{
		return namespace;
	}

	public void setNamespace(List<String> namespace)
	{
		this.namespace = namespace;
	}
}
