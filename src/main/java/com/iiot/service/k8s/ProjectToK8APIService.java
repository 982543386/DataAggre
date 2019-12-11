package com.iiot.service.k8s;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.ExecAction;
import io.fabric8.kubernetes.api.model.HTTPGetAction;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodCondition;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.ReplicationControllerList;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.TCPSocketAction;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.batch.Job;
import io.fabric8.kubernetes.api.model.batch.JobList;
//import io.fabric8.kubernetes.api.model.extensions.Job;
//import io.fabric8.kubernetes.api.model.extensions.JobList;
import io.fabric8.kubernetes.client.KubernetesClientException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.ku8eye.Constants;
import org.ku8eye.bean.project.LivenessProbe;
import org.ku8eye.bean.project.Volume;
import org.ku8eye.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Federico Gu 
 * Email: federico.gu@hpe.com
 * Date: 2016-05 
 * Description: Service to translate from Kubernetes to Ku8
 */

/**
 * 
 */
/**
 * @author lenovo
 *
 */
@Service
public class ProjectToK8APIService
{
	private static final Logger log = Logger.getLogger(ProjectToK8APIService.class);
	
	@Autowired
	private K8APIService k8sAPIService;
	
	public org.ku8eye.bean.project.Service getService(String serviceName, boolean isPublic, String namespace, int clusterID, User user)
	{
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Getting Services for:" + serviceName);
		
		ReplicationController f8ReplicationController = k8sAPIService.getReplicationControllerByName(clusterID, namespace, serviceName);
		io.fabric8.kubernetes.api.model.Service f8Service = k8sAPIService.getServicesByName(clusterID, namespace, serviceName);
		String ku8Version = k8sAPIService.getKubernetesVersion(clusterID);
		
		if(f8ReplicationController == null || f8Service == null)
		{
			log.error("[USER: " + user.getUserId() + "] Service " + serviceName + " does not exist.");
			return null;
		}
		
		return F8toK8Service(f8ReplicationController, f8Service, ku8Version, user);
		
	}
	
	public List<org.ku8eye.bean.project.Service> getServices(String appName, boolean isPublic, String namespace, int clusterID, User user)
	{
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Getting Services for:" + appName);
		
		try
		{
			Map<String, String> l_map = new HashMap<String, String>();
			l_map.put(org.ku8eye.Constants.KU8_APPNAME, appName);
			if(isPublic)
				l_map.put(org.ku8eye.Constants.KU8_APPTYPE, org.ku8eye.Constants.KU8_PUBLIC);
			else
				l_map.put(org.ku8eye.Constants.KU8_APPTYPE, org.ku8eye.Constants.KU8_PRIVATE);
			
			ReplicationControllerList f8ReplicationControllerList = k8sAPIService.getReplicationControllersByLabelsSelector(clusterID, namespace, l_map);
			ServiceList f8ServiceList = k8sAPIService.getServicesByLabelsSelector(clusterID, namespace, l_map);
			String ku8Version = k8sAPIService.getKubernetesVersion(clusterID);
			
			if(f8ReplicationControllerList == null || f8ServiceList == null)
			{
				log.error("[USER: " + user.getUserId() + "] This app " + appName + " has no ReplicationControllers / Services");
				return null;
			}
			
			List<ReplicationController> f8ReplicationControllers = f8ReplicationControllerList.getItems();
			
			if(log.isInfoEnabled())
				log.info("[USER: " + user.getUserId() + "] Found " + f8ReplicationControllers.size() + " RCs.");
				
			List<io.fabric8.kubernetes.api.model.Service> f8Services = f8ServiceList.getItems();
			
			if(log.isInfoEnabled())
				log.info("[USER: " + user.getUserId() + "] Found " + f8Services.size() + " services.");
			
			List<org.ku8eye.bean.project.Service> beanServices = new ArrayList<org.ku8eye.bean.project.Service>(f8ReplicationControllers.size());
			
			if (f8ReplicationControllers.size() != f8Services.size())
			{
				log.error("[USER: " + user.getUserId() + "] Returned " + f8ReplicationControllers.size() + " RCs, "+ f8Services.size() +" services, RC and Service sizes are different");
				return null;
			}
			
			for(int i = 0; i < f8ReplicationControllers.size(); i++)
			{
				org.ku8eye.bean.project.Service beanService =  F8toK8Service(f8ReplicationControllers.get(i), f8Services.get(i), ku8Version, user);
				beanServices.add(beanService);
			}
			return beanServices;
		}
		catch(KubernetesClientException e)
		{
			log.error("[USER: " + user.getUserId() + "] Error in getAllServices, " + e.getMessage());
			return null;
		}
	}
	
	public List<org.ku8eye.bean.project.Pod> getPods(int clusterId, String namespace, String name, User user)
	{
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Getting Pods for Job/Service: " + name +", clusted: " + clusterId + ", namespace: " + namespace);
		
		try
		{
			Map<String, String> l_map = new HashMap<String, String>();
			l_map.put(Constants.KU8_PODSELECTOR, name);
			
			PodList f8PodList = k8sAPIService.getPodsByLabelsSelector(clusterId, namespace, l_map);
			
			if(f8PodList == null)
			{
				log.error("[USER: " + user.getUserId() + "] This job/service " + name + " has no Pods");
				return null;
			}
			
			List<Pod> f8Pods = f8PodList.getItems();
			List<org.ku8eye.bean.project.Pod> beanPods = new ArrayList<org.ku8eye.bean.project.Pod>(f8Pods.size());
			
			if(log.isInfoEnabled())
				log.info("[USER: " + user.getUserId() + "] Found " + f8Pods.size() + " pods on " + name);
			
			for(Pod f8Pod : f8Pods)
			{
				org.ku8eye.bean.project.Pod beanPod = new org.ku8eye.bean.project.Pod();
				beanPod.setName(f8Pod.getMetadata().getName());
				beanPod.setPod_ip(f8Pod.getStatus().getPodIP());
				beanPod.setHost_ip(f8Pod.getStatus().getHostIP());
				beanPod.setStatus(f8Pod.getStatus().getPhase());
				
				StringBuilder statusDetails = new StringBuilder();
				for(PodCondition pc : f8Pod.getStatus().getConditions())
				{
					statusDetails.append("<b>Status:</b> ").append(pc.getReason()).append(", ").append(pc.getMessage()).append("<br/>");
				}
				
				for(ContainerStatus cs : f8Pod.getStatus().getContainerStatuses())
				{
					statusDetails.append("<br/><b>[").append(cs.getName()).append("]</b>")
							.append("<br/><b>Image:</b> ").append(cs.getImage())
							.append("<br/><b>Status:</b> ").append(cs.getState()).append("<br/>");
				}
				beanPod.setStatusDetails(statusDetails.toString());
				
				//End
				beanPods.add(beanPod);
			}
			return beanPods;
		}
		catch(KubernetesClientException e)
		{
			log.error("[USER: " + user.getUserId() + "] Error in getPods " + e.getMessage());
			return null;
		}
	}
	
	public org.ku8eye.bean.project.Container getContainerByService(String appName, boolean isPublic, String sName, int cIndex, String namespace, int clusterID, User user)
	{
		List<org.ku8eye.bean.project.Service> beanServices = getServices(appName, isPublic, namespace, clusterID, user);
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Found " + beanServices.size() + " services.");
		
		for(org.ku8eye.bean.project.Service beanService : beanServices)
		{
			if(beanService.getName().equals(sName))
			{
				if(log.isInfoEnabled())
					log.info("[USER: " + user.getUserId() + "] Services " + sName + " found");
				
				if(cIndex < beanService.getContainers().length)
					return beanService.getContainers()[cIndex];
				else
				{
					log.error("[USER: " + user.getUserId() + "] Services " + sName + ", container: " + cIndex + " does not exist.");
					return null;
				}
			}
		}
		
		log.error("[USER: " + user.getUserId() + "] Services " + sName + " not found");
		return null;
	}
	
	private org.ku8eye.bean.project.Service F8toK8Service(ReplicationController f8ReplicationController, io.fabric8.kubernetes.api.model.Service f8Service, String ku8Version, User user)
	{
		org.ku8eye.bean.project.Service beanService = new org.ku8eye.bean.project.Service();
	
		//Start filling data
		beanService.setName(f8ReplicationController.getMetadata().getName());
		beanService.setResPart(f8ReplicationController.getMetadata().getNamespace());
		beanService.setKu8Version(ku8Version);
		beanService.setReplica(f8ReplicationController.getSpec().getReplicas());
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Getting volumes for RC: " + beanService.getName());
		
		//Get Labels, remove important labels
		beanService.setLabels(f8ReplicationController.getMetadata().getLabels());
		beanService.getLabels().remove(Constants.KU8_APPNAME);
		beanService.getLabels().remove(Constants.KU8_APPTYPE);
		
		// Get the Volumes and ConfigMaps
		List<io.fabric8.kubernetes.api.model.Volume> f8Volumes = f8ReplicationController.getSpec().getTemplate().getSpec().getVolumes();
		List<Volume> beanVolumes = new ArrayList<Volume>();
		List<Volume> beanConfigMaps = new ArrayList<Volume>();
		for (int i = 0; i < f8Volumes.size(); i++)
		{
			io.fabric8.kubernetes.api.model.Volume f8Volume = f8Volumes.get(i);
			Volume beanVolume = new Volume();
			beanVolume.setName(f8Volume.getName());
			
			// Volume
			if(f8Volume.getHostPath() != null)
			{
				beanVolume.setPath(f8Volume.getHostPath().getPath());
				beanVolumes.add(beanVolume);
				continue;
			}
			
			// ConfigMaps
			if(f8Volume.getConfigMap() != null)
			{
				beanConfigMaps.add(beanVolume);
				continue;
			}
			
			//Other
			log.warn("Unknown Volume found, " + f8Volume);
		}
		beanService.setVolumes(beanVolumes.toArray(new Volume[beanVolumes.size()]));
		beanService.setConfmaps(beanConfigMaps.toArray(new Volume[beanConfigMaps.size()]));
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Getting containers for RC: " + beanService.getName());
			
		// Get the Containers
		List<Container> f8Containers = f8ReplicationController.getSpec().getTemplate().getSpec().getContainers();
		List<org.ku8eye.bean.project.Container> beanContainers = new ArrayList<org.ku8eye.bean.project.Container>(f8Containers.size());
		
		// Get the Service Ports
		List<ServicePort> f8ServicePorts = f8Service.getSpec().getPorts();
		beanService.setServicePorts(f8ServicePorts.toArray(new ServicePort[f8Service.getSpec().getPorts().size()]));
		
		for(int j = 0; j < f8Containers.size(); j++)
		{
			Container f8Container = f8Containers.get(j);
			org.ku8eye.bean.project.Container beanContainer = new org.ku8eye.bean.project.Container();
			
			//Start filling data
			beanContainer.setName(f8Container.getName());
			beanContainer.setImageName(f8Container.getImage());
			
			//Set default values
			beanContainer.setQuotas_cpu(org.ku8eye.Constants.KU8_UNLIMITED);
			beanContainer.setQuotas_memory(org.ku8eye.Constants.KU8_UNLIMITED);
			
			//Check if Limits are set
			if(f8Container.getResources().getLimits() != null)
			{
				//Get CPU if exist
				Quantity cpu = f8Container.getResources().getLimits().get("cpu");
				if(cpu != null)
					beanContainer.setQuotas_cpu(cpu.getAmount());
			
				//Get Memory if exist
				Quantity memory = f8Container.getResources().getLimits().get("memory");
				if(memory != null)
					beanContainer.setQuotas_memory(memory.getAmount());
			}
			
			//Get Volume Mounts
			List<VolumeMount> f8VolumeMounts = f8Container.getVolumeMounts();
			List<Volume> beanVolumeMounts = new ArrayList<Volume>();
			for (int k = 0; k < f8VolumeMounts.size(); k++)
			{
				VolumeMount f8VolumeMount = f8VolumeMounts.get(k);
				
				//Check if this corresponds to a ConfigMap Volume
				Optional<Volume> vm_optional = beanConfigMaps.stream().filter(p -> p.getName().equals(f8VolumeMount.getName())).findFirst();
				
				if(vm_optional.isPresent())
				{
					Volume k8VolumeConfMap = vm_optional.get();
					k8VolumeConfMap.setPath(f8VolumeMount.getMountPath());
					continue;
				}
				
				// Then this is a real Volume Mount
				Volume beanVolumeMount = new Volume();
				beanVolumeMount.setName(f8VolumeMount.getName());
				beanVolumeMount.setPath(f8VolumeMount.getMountPath());
				beanVolumeMounts.add(beanVolumeMount);
			}
			beanContainer.setVolumes(beanVolumeMounts.toArray(new Volume[beanVolumeMounts.size()]));
			
			//Get EnVars
			beanContainer.setEnvVariables(f8Container.getEnv().toArray(new EnvVar[f8Container.getEnv().size()]));
			
			//Get Liveness Probe
			LivenessProbe livenessProbe = null;
			Probe f8Probe = f8Container.getLivenessProbe();
			if(f8Probe != null)
			{
				livenessProbe = new LivenessProbe();
				if(f8Probe.getExec() != null)
				{
					livenessProbe.setType("exec");
					ExecAction f8ExecAction = f8Probe.getExec();
					//Cleanse command
					String cmd = f8ExecAction.getCommand().get(f8ExecAction.getCommand().size() -1);
					String tok[] = cmd.split("\"");
					if(tok.length > 1)
						livenessProbe.setCommand(tok[1]);
				}
				else if (f8Probe.getHttpGet() != null)
				{
					livenessProbe.setType("httpGet");
					HTTPGetAction f8HttpGetAction = f8Probe.getHttpGet();
					livenessProbe.setPath(f8HttpGetAction.getPath());
					livenessProbe.setPort(f8HttpGetAction.getPort().getIntVal());
				}
				else if (f8Probe.getTcpSocket() != null)
				{
					livenessProbe.setType("tcpSocket");
					TCPSocketAction f8TcpSocketAction = f8Probe.getTcpSocket();
					livenessProbe.setPort(f8TcpSocketAction.getPort().getIntVal());
				}
				livenessProbe.setInitialDelaySeconds(f8Probe.getInitialDelaySeconds());
				livenessProbe.setTimeoutSeconds(f8Probe.getTimeoutSeconds());
			}
			beanContainer.setLivenessProbe(livenessProbe);
			
			//Get the Container Port
			List<ContainerPort> f8ContainerPorts = f8Container.getPorts();
			beanContainer.setContainerPorts(f8ContainerPorts.toArray(new ContainerPort[f8Container.getPorts().size()]));
			
			String port_str = "";
			for(int l = 0; l < f8ContainerPorts.size(); l++)
			{
				ContainerPort f8ContainerPort = f8ContainerPorts.get(l);
				port_str += "CP: " + f8ContainerPort.getContainerPort();
				
				//Get the Node port
				if(f8Service.getSpec().getType().equals("NodePort"))
				{
					int container_pos = j;
					int port_pos = l;
					Optional<ServicePort> sp_optional = f8ServicePorts.stream().filter(p -> {
						if(p.getName() == null)
						{
							String msg = "One of the NodePort has no Name, please set Name on Kubernetes before continuing.";
							log.error("[USER: " + user.getUserId() + "] " + msg);
							throw new KubernetesClientException(msg);
						}
						else
						{
							return p.getName().equals("c-" + container_pos + "-p-" + port_pos);
						}}).findFirst();
					
					if(sp_optional.isPresent())
					{
						ServicePort f8ServicePort = sp_optional.get();
						port_str += " NP: " + f8ServicePort.getNodePort();
					}
					
					if(l < f8ContainerPorts.size() -1)
						port_str += "<br/>";
				}
			}
			beanContainer.setPortString(port_str);
			
			beanContainers.add(beanContainer);
		}
		beanService.setContainers(beanContainers.toArray(new org.ku8eye.bean.project.Container[beanContainers.size()]));
	
		return beanService;
	}
	
	//JOBS
	
	public List<org.ku8eye.bean.project.Job> getJobs(String jobName, String namespace, int clusterID, User user)
	{
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Getting Jobs for JobList:" + jobName);
		
		try
		{
			Map<String, String> l_map = new HashMap<String, String>();
			l_map.put(org.ku8eye.Constants.KU8_JOBGROUP, jobName);
			
			JobList f8JobList = k8sAPIService.getJobsByLabelsSelector(clusterID, namespace, l_map);
			String ku8Version = k8sAPIService.getKubernetesVersion(clusterID);
			
			if(f8JobList == null)
			{
				log.error("[USER: " + user.getUserId() + "] This JobList " + jobName + " has no Jobs");
				return null;
			}
			
			List<Job> f8Jobs = f8JobList.getItems();
			
			if(log.isInfoEnabled())
				log.info("[USER: " + user.getUserId() + "] Found " + f8Jobs.size() + " Jobs.");
				
			List<org.ku8eye.bean.project.Job> beanJobs = new ArrayList<org.ku8eye.bean.project.Job>(f8Jobs.size());
			
			for(int i = 0; i < f8Jobs.size(); i++)
			{
				org.ku8eye.bean.project.Job beanJob =  F8toK8Job(f8Jobs.get(i), ku8Version, user);
				beanJobs.add(beanJob);
			}
			return beanJobs;
		}
		catch(KubernetesClientException e)
		{
			log.error("[USER: " + user.getUserId() + "] Error in getAllServices, " + e.getMessage());
			return null;
		}
	}
	
	public org.ku8eye.bean.project.Container getContainerByJob(String jobListName, String jobName, int cIndex, String namespace, int clusterID, User user)
	{
		List<org.ku8eye.bean.project.Job> beanJobs = getJobs(jobListName, namespace, clusterID, user);
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Found " + beanJobs.size() + " jobs.");
		
		for(org.ku8eye.bean.project.Job beanJob : beanJobs)
		{
			if(beanJob.getName().equals(jobName))
			{
				if(log.isInfoEnabled())
					log.info("[USER: " + user.getUserId() + "] Jobs " + jobName + " found");
				
				if(cIndex < beanJob.getContainers().length)
					return beanJob.getContainers()[cIndex];
				else
				{
					log.error("[USER: " + user.getUserId() + "] Jobs " + jobName + ", container: " + cIndex + " does not exist.");
					return null;
				}
			}
		}
		
		log.error("[USER: " + user.getUserId() + "] Jobs " + jobName + " not found");
		return null;
	}
	
	private org.ku8eye.bean.project.Job F8toK8Job(Job f8Job, String ku8Version, User user)
	{
		org.ku8eye.bean.project.Job beanJob = new org.ku8eye.bean.project.Job();
	
		//Start filling data
		beanJob.setName(f8Job.getMetadata().getName());
		beanJob.setResPart(f8Job.getMetadata().getNamespace());
		beanJob.setKu8Version(ku8Version);
		beanJob.setParallelism(f8Job.getSpec().getParallelism());
		
		//Get Labels, remove important labels
		beanJob.setLabels(f8Job.getMetadata().getLabels());
		beanJob.getLabels().remove(Constants.KU8_JOBGROUP);
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Getting containers for RC: " + beanJob.getName());
			
		// Get the Containers
		List<Container> f8Containers = f8Job.getSpec().getTemplate().getSpec().getContainers();
		List<org.ku8eye.bean.project.Container> beanContainers = new ArrayList<org.ku8eye.bean.project.Container>(f8Containers.size());
		
		for(int j = 0; j < f8Containers.size(); j++)
		{
			Container f8Container = f8Containers.get(j);
			org.ku8eye.bean.project.Container beanContainer = new org.ku8eye.bean.project.Container();
			
			//Start filling data
			beanContainer.setName(f8Container.getName());
			beanContainer.setImageName(f8Container.getImage());
			
			//Set default values
			beanContainer.setQuotas_cpu(org.ku8eye.Constants.KU8_UNLIMITED);
			beanContainer.setQuotas_memory(org.ku8eye.Constants.KU8_UNLIMITED);
			
			//Check if Limits are set
			if(f8Container.getResources().getLimits() != null)
			{
				//Get CPU if exist
				Quantity cpu = f8Container.getResources().getLimits().get("cpu");
				if(cpu != null)
					beanContainer.setQuotas_cpu(cpu.getAmount());
			
				//Get Memory if exist
				Quantity memory = f8Container.getResources().getLimits().get("memory");
				if(memory != null)
					beanContainer.setQuotas_memory(memory.getAmount());
			}
			
			//Get Commands
			beanContainer.setCommand(f8Container.getCommand().stream().collect(Collectors.joining(" ")));
			
			beanContainers.add(beanContainer);
		}
		beanJob.setContainers(beanContainers.toArray(new org.ku8eye.bean.project.Container[beanContainers.size()]));
	
		return beanJob;
	}
}
