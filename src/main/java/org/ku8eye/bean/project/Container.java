package org.ku8eye.bean.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.EnvVar;

@JsonIgnoreProperties(ignoreUnknown = true) 
public class Container {
	private int index;
	private String name;
	private int imageId;
	private String imageName;
	private String imagePullPolicy;
	private String quotas_cpu;
	private String quotas_memory;
	private String portString;
	private LivenessProbe livenessProbe;
	private ContainerPort[] containerPorts;
	private EnvVar[] envVariables;
	private Volume[] volumes;
	private String command;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getImagePullPolicy() {
		return imagePullPolicy;
	}

	public void setImagePullPolicy(String imagePullPolicy) {
		this.imagePullPolicy = imagePullPolicy;
	}

	public int getImageId()
	{
		return imageId;
	}

	public void setImageId(int imageId)
	{
		this.imageId = imageId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getQuotas_cpu() {
		return quotas_cpu;
	}

	public void setQuotas_cpu(String quotas_cpu) {
		this.quotas_cpu = quotas_cpu;
	}

	public String getQuotas_memory() {
		return quotas_memory;
	}

	public void setQuotas_memory(String quotas_memory) {
		this.quotas_memory = quotas_memory;
	}

	public String getPortString()
	{
		return portString;
	}

	public void setPortString(String portString)
	{
		this.portString = portString;
	}

	public EnvVar[] getEnvVariables()
	{
		return envVariables;
	}

	public ContainerPort[] getContainerPorts()
	{
		return containerPorts;
	}

	public void setContainerPorts(ContainerPort[] containerPorts)
	{
		this.containerPorts = containerPorts;
	}

	public void setEnvVariables(EnvVar[] envVariables)
	{
		this.envVariables = envVariables;
	}
	
	public Volume[] getVolumes()
	{
		return volumes;
	}

	public void setVolumes(Volume[] volumes)
	{
		this.volumes = volumes;
	}

	public String getCommand()
	{
		return command;
	}

	public void setCommand(String command)
	{
		this.command = command;
	}

	public LivenessProbe getLivenessProbe()
	{
		return livenessProbe;
	}

	public void setLivenessProbe(LivenessProbe livenessProbe)
	{
		this.livenessProbe = livenessProbe;
	}
}
