package org.ku8eye.service.k8s;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ExecAction;
import io.fabric8.kubernetes.api.model.ExecActionBuilder;
import io.fabric8.kubernetes.api.model.HTTPGetAction;
import io.fabric8.kubernetes.api.model.HTTPGetActionBuilder;
import io.fabric8.kubernetes.api.model.HostPathVolumeSource;
import io.fabric8.kubernetes.api.model.KeyToPath;
import io.fabric8.kubernetes.api.model.KeyToPathBuilder;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.ReplicationControllerBuilder;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.TCPSocketAction;
import io.fabric8.kubernetes.api.model.TCPSocketActionBuilder;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.api.model.batch.Job;
import io.fabric8.kubernetes.api.model.batch.JobBuilder;
//k8 client 1.9
//import io.fabric8.kubernetes.api.model.extensions.Job;
//import io.fabric8.kubernetes.api.model.extensions.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ku8eye.Constants;
import org.ku8eye.bean.project.LivenessProbe;
import org.ku8eye.domain.User;
import org.ku8eye.service.Ku8ResPartionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiot.domain.DockerImage;
import com.iiot.service.ImageService;

/**
 * @author Federico Gu 
 * Email: federico.gu@hpe.com
 * Date: 2016-05 
 * Description: Service to translate from Ku8 to Kubernetes
 */
@Service
public class K8toF8APIService
{
	private static final Logger log = Logger.getLogger(K8toF8APIService.class);
	
	@Autowired
	private K8sAPIService k8sAPIService;

	@Autowired
	private ImageService imageService;
	
	private Container[] createContainers(String serviceName, org.ku8eye.bean.project.Container containerArray[], User user) throws KubernetesClientException
	{
		if(containerArray == null)
		{
			String msg = "Got a null Container Array";
			log.error("[USER: " + user.getUserId() + "] " + msg);
			throw new KubernetesClientException(msg);
		}
		
		Container f8Containers[] = new Container[containerArray.length];
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Looping " + containerArray.length + " containers from " + serviceName);
			
		for (int i = 0; i < containerArray.length; i++)
		{
			org.ku8eye.bean.project.Container beanContainer = containerArray[i];
			
			//Get name from DB
			DockerImage dImg = imageService.getImagesId(beanContainer.getImageId());
			
			if(dImg == null)
			{
				String msg = "Container \""+ beanContainer.getIndex() + "\" from Service \"" + serviceName + "\" failed, docker image could not be found, id: " + beanContainer.getImageId();
				log.error(msg);
				throw new KubernetesClientException(msg);
			}
			
			if(log.isInfoEnabled())
				log.info("[USER: " + user.getUserId() + "] Got Docker Image, " + dImg.getImageName() + ", url:" + dImg.getImageUrl());
			
			//Create Liveness Probe
			Probe f8LivenessProbe = null;
			if(beanContainer.getLivenessProbe() != null)
				f8LivenessProbe = createLivenessProbe(serviceName, beanContainer.getLivenessProbe(), user);
			
			//Create Volume Mounts
			List<VolumeMount> f8VolumeMounts = createVolumeMounts(serviceName, beanContainer.getVolumes(), user);
			
			//Create Resource
			Map<String, Quantity> r_map = new HashMap<String, Quantity>();
			
			if(StringUtils.isNotBlank(beanContainer.getQuotas_cpu()) && !beanContainer.getQuotas_cpu().equals(Constants.KU8_UNLIMITED))
				r_map.put("cpu", new Quantity(beanContainer.getQuotas_cpu()));
			if(StringUtils.isNotBlank(beanContainer.getQuotas_memory()) && !beanContainer.getQuotas_memory().equals(Constants.KU8_UNLIMITED))
				r_map.put("memory", new Quantity(beanContainer.getQuotas_memory() + "Mi"));
			ResourceRequirements f8Resources = new ResourceRequirementsBuilder().withLimits(r_map).build();
			
			//Create Command
			List<String> cmds = null;
			if(beanContainer.getCommand() != null)
			{
				cmds = Arrays.asList(beanContainer.getCommand().split(" "));
			}
			
			//Don't use 'container.getImageName()' is invalid! dImg.getImageName() must be free of special characters.
			String imageStr = dImg.getImageUrl() + "/" + dImg.getImageName() + ":" + dImg.getVersion();
			
			if(log.isInfoEnabled())
				log.info("[USER: " + user.getUserId() + "] Assigning Docker Image url: " + imageStr);
			
			try
			{
				Container f8Container = new ContainerBuilder()
						.withName("container-" + i)
						.withImage(imageStr)
						.withImagePullPolicy("Always")
						.withPorts(beanContainer.getContainerPorts())
						.withLivenessProbe(f8LivenessProbe)
						.withVolumeMounts(f8VolumeMounts)
						.withResources(f8Resources)
						.withEnv(beanContainer.getEnvVariables())
						.withCommand(cmds)
						.build();
				
				f8Containers[i] = f8Container;
			}
			catch(IllegalStateException ise)
			{
				String msg = "Container \""+ beanContainer.getIndex() + "\" from Service \"" + serviceName + "\" failed to build, " + ise.getMessage();
				log.error("[USER: " + user.getUserId() + "] " + msg);
				throw new KubernetesClientException(msg);
			}
		}
		return f8Containers;
	}
	
	private Probe createLivenessProbe(String serviceName, LivenessProbe livenessProbe, User user) throws KubernetesClientException
	{
		if(livenessProbe == null)
		{
			String msg = "Got a null Liveness Probe";
			log.error("[USER: " + user.getUserId() + "] " + msg);
			throw new KubernetesClientException(msg);
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Creating Liveness Probe of type: " + livenessProbe.getType() + ", for service, " + serviceName);
		
		Probe f8Probe = null;
		
		try
		{
			switch (livenessProbe.getType())
			{
			case "httpGet":
			{
				HTTPGetAction f8HttpGetAction = new HTTPGetActionBuilder()
					.withPath(livenessProbe.getPath())
					.withNewPort(livenessProbe.getPort())
					.build();
				f8Probe = new ProbeBuilder()
					.withHttpGet(f8HttpGetAction)
					.withInitialDelaySeconds(livenessProbe.getInitialDelaySeconds())
					.withTimeoutSeconds(livenessProbe.getTimeoutSeconds())
					.build();
				break;
			}
			case "tcpSocket":
			{
				TCPSocketAction f8TcpSocketAction = new TCPSocketActionBuilder()
					.withNewPort(livenessProbe.getPort())
					.build();
				f8Probe = new ProbeBuilder()
					.withTcpSocket(f8TcpSocketAction)
					.withInitialDelaySeconds(livenessProbe.getInitialDelaySeconds())
					.withTimeoutSeconds(livenessProbe.getTimeoutSeconds())
					.build();
				break;
			}
			case "exec":
			{
				List<String> commands = new ArrayList<String>();
				commands.add("- sh");
				commands.add("- -c");
				commands.add("- \"" + livenessProbe.getCommand() + "\"");
				
				ExecAction f8ExecAction = new ExecActionBuilder()
					.withCommand(commands)
					.build();
				
				f8Probe = new ProbeBuilder()
					.withExec(f8ExecAction)
					.withInitialDelaySeconds(livenessProbe.getInitialDelaySeconds())
					.withTimeoutSeconds(livenessProbe.getTimeoutSeconds())
					.build();
				break;
			}
			default:
			{
				String msg = "Unknown Liveness Probe type, " + livenessProbe.getType();
				log.error("[USER: " + user.getUserId() + "] " + msg);
				throw new KubernetesClientException(msg);
			}
			}
		}
		catch(IllegalStateException ise)
		{
			String msg = "Liveness Probe from Service \"" + serviceName + "\" failed to build, " + ise.getMessage();
			log.error("[USER: " + user.getUserId() + "] " + msg);
			throw new KubernetesClientException(msg);
		}
		return f8Probe;
	}

	private List<Volume> createVolumes(String serviceName, org.ku8eye.bean.project.Volume ku8VolumeArray[], User user) throws KubernetesClientException
	{
		if(ku8VolumeArray == null)
		{
			String msg = "Got a null Volume Array";
			log.warn("[USER: " + user.getUserId() + "] " + msg + ", skip Volume");
			return null;
		}
		
		List<Volume> volumeList = new ArrayList<Volume>();
		
		for (org.ku8eye.bean.project.Volume ku8Volume : ku8VolumeArray)
		{
			if(log.isInfoEnabled())
				log.info("[USER: " + user.getUserId() + "] Creating Volume: " + ku8Volume.getName() + ", for service, " + serviceName);
			
			try
			{
				Volume f8Volume = new VolumeBuilder()
					.withName(ku8Volume.getName())
					.withHostPath(new HostPathVolumeSource(ku8Volume.getPath(),"hello"))
					.build();
				volumeList.add(f8Volume);
			}
			catch(IllegalStateException ise)
			{
				String msg = "Volume \""+ ku8Volume.getName() + "\" from Service \"" + serviceName + "\" failed to build, " + ise.getMessage();
				log.error("[USER: " + user.getUserId() + "] " + msg);
				throw new KubernetesClientException(msg);
			}
		}
		return volumeList;
	}
	
	private void createConfigMaps(int clusterId, String namespace, String serviceName, org.ku8eye.bean.project.Volume ku8ConfMapArray[], Container f8Containers[], List<Volume> f8Volumes, User user) throws KubernetesClientException
	{
		if(ku8ConfMapArray == null)
		{
			String msg = "Got a null Config Map Array";
			log.warn("[USER: " + user.getUserId() + "] " + msg + ", skip Config Map");
			return;
		}
		
		for (org.ku8eye.bean.project.Volume confMap : ku8ConfMapArray)
		{
			if(log.isInfoEnabled())
				log.info("[USER: " + user.getUserId() + "] Checking Config Map: " + confMap.getName() + ", in Cluster " + clusterId + ", namespace: " + namespace);
			
			ConfigMap f8ConfigMap = k8sAPIService.getConfigMapByName(clusterId, namespace, confMap.getName());
			
			if(f8ConfigMap == null)
			{
				String msg = "Config Map \""+ confMap.getName() + "\" does not exist, please check the Config Maps";
				log.error("[USER: " + user.getUserId() + "] " + msg);
				throw new KubernetesClientException(msg);
			}
			
			try
			{
				for(Container f8Container : f8Containers)
				{
					List<VolumeMount> f8VolumeMounts = f8Container.getVolumeMounts();
					
					if(f8VolumeMounts == null)
						f8VolumeMounts = new ArrayList<VolumeMount>();
					
					VolumeMount f8VolumeMount = new VolumeMountBuilder()
						.withName(confMap.getName())
						.withMountPath(confMap.getPath())
						.build();
						
					f8VolumeMounts.add(f8VolumeMount);
					f8Container.setVolumeMounts(f8VolumeMounts);
				}
				
				//Build ConfigMap items
				List<KeyToPath> f8KeyToPathList = new ArrayList<KeyToPath>();
				f8ConfigMap.getMetadata().getLabels().forEach((k, v) -> f8KeyToPathList.add(new KeyToPathBuilder().withKey(k).withPath(v).build()));
				
				Volume f8Volume = new VolumeBuilder()
					.withName(confMap.getName())
					.withNewConfigMap()
					.withName(confMap.getName())
					.withItems(f8KeyToPathList)
					.endConfigMap()
					.build();
				f8Volumes.add(f8Volume);
			}
			catch(IllegalStateException ise)
			{
				String msg = "Config Map Volume \""+ confMap.getName() + "\" from Service \"" + serviceName + "\" failed to build, " + ise.getMessage();
				log.error("[USER: " + user.getUserId() + "] " + msg);
				throw new KubernetesClientException(msg);
			}
		}
	}
	
	private List<VolumeMount> createVolumeMounts(String serviceName, org.ku8eye.bean.project.Volume ku8VolumeArray[], User user) throws KubernetesClientException
	{
		if(ku8VolumeArray == null)
		{
			String msg = "Got a null Volume Array";
			log.warn("[USER: " + user.getUserId() + "] " + msg + ", skip Volume Mount");
			return null;
		}
		
		List<VolumeMount> volumeMountList = new ArrayList<VolumeMount>();
		
		for (org.ku8eye.bean.project.Volume ku8Volume : ku8VolumeArray)
		{
			if(log.isInfoEnabled())
				log.info("[USER: " + user.getUserId() + "] Creating VolumeMount: " + ku8Volume.getName() + ", for service, " + serviceName);
			try
			{
				VolumeMount f8VolumeMount = new VolumeMountBuilder()
					.withName(ku8Volume.getName())
					.withMountPath(ku8Volume.getPath())
					.build();
				volumeMountList.add(f8VolumeMount);
			}
			catch(IllegalStateException ise)
			{
				String msg = "Volume Mount \""+ ku8Volume.getName() + "\" from Service \"" + serviceName + "\" failed to build, " + ise.getMessage();
				log.error("[USER: " + user.getUserId() + "] " + msg);
				throw new KubernetesClientException(msg);
			}
		}
		return volumeMountList;
	}
	
	public ReplicationController buildReplicationController(int clusterId, String namespace, boolean isPublic, String appName, org.ku8eye.bean.project.Service s, User user) throws KubernetesClientException
	{
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Creating RC:" + s.getName());

		//Setup NodeSelector
		Map<String, String> ns_map = null;
		if(!namespace.equals(org.ku8eye.Constants.KU8_DEFAULT_NAMESPACE))
		{
			ns_map = new HashMap<String, String>();
			ns_map.put(Ku8ResPartionService.NODE_GROUP_NAME_PREFIX + namespace, namespace);
		}
		
		//Setup for Selector and Pod Label
		Map<String, String> sp_map = new HashMap<String, String>();
		sp_map.put(Constants.KU8_PODSELECTOR, s.getName());
		
		//Setup for RC Label, add APPTYPE and APPNAME
		Map<String, String> l_map = new HashMap<>(s.getLabels());
		l_map.put(org.ku8eye.Constants.KU8_APPNAME, appName);
		if(isPublic)
			l_map.put(org.ku8eye.Constants.KU8_APPTYPE, org.ku8eye.Constants.KU8_PUBLIC);
		else
			l_map.put(org.ku8eye.Constants.KU8_APPTYPE, org.ku8eye.Constants.KU8_PRIVATE);
		
		//Build all f8Volumes
		log.info("[USER: " + user.getUserId() + "] Creating volumes");
		List<Volume> f8Volumes = createVolumes(s.getName(), s.getVolumes(), user);

		//Build all f8Containers
		log.info("[USER: " + user.getUserId() + "] Creating containers");
		Container[] f8Containers = createContainers(s.getName(), s.getContainers(), user);
		
		//Build all f8ConfigMaps
		log.info("[USER: " + user.getUserId() + "] Creating config maps");
		createConfigMaps(clusterId, namespace, s.getName(), s.getConfmaps(), f8Containers, f8Volumes, user);
		
		try
		{
			ReplicationController replicationController = new ReplicationControllerBuilder().withKind("ReplicationController")
					.withNewMetadata()
					.withName(s.getName())
					.withNamespace(namespace)
					.withLabels(l_map)
					.endMetadata()
					.withNewSpec()
					.withReplicas(s.getReplica())
					.withSelector(sp_map)
					.withNewTemplate()
					.withNewMetadata()
					.withLabels(sp_map)
					.endMetadata()
					.withNewSpec()
					.withHostNetwork(s.isHostNetwork())
					.withVolumes(f8Volumes)
					.withContainers(f8Containers)
					.withNodeSelector(ns_map)
					.endSpec()
					.endTemplate()
					.endSpec().build();
			
			ReplicationController response_f8ReplicationController = k8sAPIService.createReplicationController(clusterId, namespace, replicationController);
			return response_f8ReplicationController;
		}
		catch(IllegalStateException ise)
		{
			String msg = "RC \"" + s.getName() + "\" failed to build, " + ise.getMessage();
			log.error("[USER: " + user.getUserId() + "] " + msg);
			throw new KubernetesClientException(msg);
		}
	}
	
	public io.fabric8.kubernetes.api.model.Service buildService(int clusterId, String namespace, boolean isPublic, String appName, org.ku8eye.bean.project.Service s, User user) throws KubernetesClientException
	{
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Creating Service:" + s.getName());
		
		//Setup for Selector
		Map<String, String> s_map = new HashMap<String, String>();
		s_map.put(Constants.KU8_PODSELECTOR, s.getName());
		
		//Setup for Service Label, add APPTYPE and APPNAME
		Map<String, String> l_map = new HashMap<>(s.getLabels());
		l_map.put(org.ku8eye.Constants.KU8_APPNAME, appName);
		if(isPublic)
			l_map.put(org.ku8eye.Constants.KU8_APPTYPE, org.ku8eye.Constants.KU8_PUBLIC);
		else
			l_map.put(org.ku8eye.Constants.KU8_APPTYPE, org.ku8eye.Constants.KU8_PRIVATE);
		
		//Set NodePort / ClusterIP
		String type = "ClusterIP";
		if(s.getServicePorts() != null)
		{
			for(ServicePort f8ServicePort : s.getServicePorts())
			{
				if(f8ServicePort.getNodePort() != null)
				{
					type = "NodePort";
					break;
				}
			}
		}
		
		try
		{
			io.fabric8.kubernetes.api.model.Service f8Service = new ServiceBuilder().withKind("Service")
					.withNewMetadata()
					.withName(s.getName())
					.withNamespace(namespace)
					.withLabels(l_map)
					.endMetadata()
					.withNewSpec()
					.withPorts(s.getServicePorts())
					.withType(type)
					.withSessionAffinity(s.getProxyMode())
					.withSelector(s_map)
					.endSpec().build();
			
			io.fabric8.kubernetes.api.model.Service response_f8Service = k8sAPIService.createService(clusterId, namespace, f8Service);
	
			return response_f8Service;
		}
		catch(IllegalStateException ise)
		{
			String msg = "Service \"" + s.getName() + "\" failed to build, " + ise.getMessage();
			log.error("[USER: " + user.getUserId() + "] " + msg);
			throw new KubernetesClientException(msg);
		}
	}
	
	public Job buildJob(int clusterId, String namespace, String jobName, org.ku8eye.bean.project.Job j, User user) throws KubernetesClientException
	{
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Creating Job:" + j.getName());
		
		//Setup for Selector
		Map<String, String> s_map = new HashMap<String, String>();
		s_map.put(Constants.KU8_PODSELECTOR, j.getName());
				
		//Setup for Label, JOBGROUP
		Map<String, String> l_map = new HashMap<>(j.getLabels());
		l_map.put(org.ku8eye.Constants.KU8_JOBGROUP, jobName);
		
		//Build all f8Containers
		log.info("[USER: " + user.getUserId() + "] Creating containers");
		Container[] f8Containers = createContainers(j.getName(), j.getContainers(), user);
		
		try
		{
			Job f8Job = new JobBuilder().withKind("Job")
					.withNewMetadata()
					.withName(j.getName())
					.withNamespace(namespace)
					.withLabels(l_map)
					.endMetadata()
					.withNewSpec()
					.withParallelism(j.getParallelism())
					.withNewTemplate()
					.withNewMetadata()
					.withLabels(s_map)
					.endMetadata()
					.withNewSpec()
					.withContainers(f8Containers)
					.withRestartPolicy("Never")
					.endSpec()
					.endTemplate()
					.endSpec()
					.build();
			Job response_f8Job = k8sAPIService.createJob(clusterId, namespace, f8Job);
			return response_f8Job;
		}
		catch(IllegalStateException ise)
		{
			String msg = "Job \"" + jobName + "\" failed to build, " + ise.getMessage();
			log.error("[USER: " + user.getUserId() + "] " + msg);
			throw new KubernetesClientException(msg);
		}
	}
	
	public io.fabric8.kubernetes.api.model.ReplicationController updateContainer(int clusterId, String namespace, String serviceName, int containerIndex, org.ku8eye.bean.project.Container container, User user) throws KubernetesClientException
	{
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Updating Container on Service: " + serviceName);
		
		ReplicationController f8rc = k8sAPIService.getReplicationControllerByName(clusterId, namespace, serviceName);
		
		if(f8rc == null)
		{
			String msg = "RC: " + serviceName + " could not be found";
			log.error("[USER: " + user.getUserId() + "] " + msg);
			throw new KubernetesClientException(msg);
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Updating values to RC:" + serviceName);
		
		boolean error = false;
		
		try
		{	
			if(containerIndex < f8rc.getSpec().getTemplate().getSpec().getContainers().size())
			{
				//Get old container
				io.fabric8.kubernetes.api.model.Container f8OldContainer = f8rc.getSpec().getTemplate().getSpec().getContainers().get(containerIndex);
				
				//Create new container
				io.fabric8.kubernetes.api.model.Container f8NewContainerList[] = createContainers(serviceName, new org.ku8eye.bean.project.Container[]{container}, user);

				if(f8NewContainerList.length > 0)
				{
					io.fabric8.kubernetes.api.model.Container f8NewContainer = f8NewContainerList[0];
					
					//Replace values
					f8OldContainer.setEnv(f8NewContainer.getEnv());
					f8OldContainer.setImage(f8NewContainer.getImage());
					f8OldContainer.setLivenessProbe(f8NewContainer.getLivenessProbe());
					
					f8rc.getSpec().getTemplate().getSpec().getContainers().set(containerIndex, f8OldContainer);
					return k8sAPIService.replaceReplicationController(clusterId, namespace, serviceName, f8rc);
				}
				else
					error = true;
			}
			else
				error = true;

			if(error)
			{
				String msg = "Fail to create the container";
				log.error("[USER: " + user.getUserId() + "] " + msg);
				throw new KubernetesClientException(msg);
			}
			else
			{
				//Cannot reach here
				return null;
			}
		}
		catch(IllegalStateException ise)
		{
			String msg = "RC \"" + serviceName + "\" failed to update, " + ise.getMessage();
			log.error("[USER: " + user.getUserId() + "] " + msg);
			throw new KubernetesClientException(msg);
		}
	}
	
	public boolean updateRCServiceLabels(int clusterId, String namespace, String serviceName, HashMap<String, String> labels, User user) throws KubernetesClientException
	{
		ReplicationController f8RC = k8sAPIService.replaceLabelsReplicationController(clusterId, namespace, serviceName, labels);
		io.fabric8.kubernetes.api.model.Service f8Service = k8sAPIService.replaceLabelsService(clusterId, namespace, serviceName, labels);
		
		if(f8RC != null && f8Service != null)
			return true;
		
		return false;
	}
	
	public boolean updateJobLabels(int clusterId, String namespace, String jobName, HashMap<String, String> labels, User user) throws KubernetesClientException
	{
		Job f8Job = k8sAPIService.replaceLabelsJob(clusterId, namespace, jobName, labels);
		return f8Job != null ? true : false;
	}
}
