package org.ku8eye.bean.project;

import io.fabric8.kubernetes.api.model.ServicePort;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ku8eye.util.JSONUtil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@JsonIgnoreProperties(ignoreUnknown = true) 
public class Service {
	private String name;
	private String describe;
	private int replica;
	private String version;
	private String ku8Version;
	private String icon;
	private String proxyMode;
	private boolean hostNetwork;
	private String resPart;
	private Map<String, String> labels;
	private Container[] containers;
	private ServicePort[] servicePorts;
	private Volume[] volumes;
	private Volume[] confmaps;
	
	private static final Logger log = Logger.getLogger(Service.class);
	
	public static Service getFromJSON(String json)
	{
		try
		{
			return JSONUtil.mapper.readValue(json, Service.class);
		}
		catch (JsonParseException e)
		{
			log.error("Couldn't parse Service from String, " + e);
		}
		catch (JsonMappingException e)
		{
			log.error("Json mapping error in Service, " + e);
		}
		catch (IOException e)
		{
			log.error("IO Error in Service, " + e);
		}
		return null;
	}
	
	public String toJSONString()
	{
		return JSONUtil.getJSONString(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public int getReplica() {
		return replica;
	}

	public void setReplica(int replica) {
		this.replica = replica;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getProxyMode()
	{
		return proxyMode;
	}

	public void setProxyMode(String proxyMode)
	{
		this.proxyMode = proxyMode;
	}

	public ServicePort[] getServicePorts()
	{
		return servicePorts;
	}

	public void setServicePorts(ServicePort[] servicePorts)
	{
		this.servicePorts = servicePorts;
	}

	public String getResPart()
	{
		return resPart;
	}

	public void setResPart(String resPart)
	{
		this.resPart = resPart;
	}
	
	public Volume[] getVolumes()
	{
		return volumes;
	}

	public void setVolumes(Volume[] volumes)
	{
		this.volumes = volumes;
	}

	public String getKu8Version()
	{
		return ku8Version;
	}

	public void setKu8Version(String ku8Version)
	{
		this.ku8Version = ku8Version;
	}

	public Volume[] getConfmaps() {
		return confmaps;
	}

	public void setConfmaps(Volume[] confmaps) {
		this.confmaps = confmaps;
	}

	public boolean isHostNetwork()
	{
		return hostNetwork;
	}

	public void setHostNetwork(boolean hostNetwork)
	{
		this.hostNetwork = hostNetwork;
	}
	
}

