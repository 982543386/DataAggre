package org.ku8eye.ctrl;

import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.client.KubernetesClientException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ku8eye.Constants;
import org.ku8eye.bean.GridData;
import org.ku8eye.bean.project.ClusterNamepace;
import org.ku8eye.bean.project.Container;
import org.ku8eye.bean.project.Pod;
import org.ku8eye.bean.project.Project;
import org.ku8eye.bean.project.Service;
import org.ku8eye.domain.Ku8Project;
import org.ku8eye.domain.User;
import org.ku8eye.service.ApplicationService;
import org.ku8eye.service.Ku8ResPartionService;
import org.ku8eye.service.UserService;
import org.ku8eye.service.k8s.F8toK8APIService;
import org.ku8eye.service.k8s.K8sAPIService;
import org.ku8eye.service.k8s.K8toF8APIService;
import org.ku8eye.util.AjaxReponse;
import org.ku8eye.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * @author Federico Gu 
 * Email: federico.gu@hpe.com
 * Date: 2016-05 
 * Description: Controller for Ku8 Applications
 */
@RestController
@SessionAttributes(org.ku8eye.Constants.USER_SESSION_KEY)
public class ApplicationController
{
	private static final Logger log = Logger.getLogger(ApplicationController.class);

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private K8toF8APIService _K8toF8APIService;
	
	@Autowired
	private F8toK8APIService _F8toK8APIService;
	
	@Autowired
	private K8sAPIService k8sAPIService;
	
	@Autowired
	private Ku8ResPartionService ku8ResPartionService;
	
	@Autowired
	private UserService userService;

	@RequestMapping(value = "/application/getApplications")
	public GridData getApplications(ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		List<Ku8Project> apps;
		
		if (user.getUserType().equals(Constants.USERTYPE_ADMIN))
		{
			 apps = applicationService.getApplications();
		}
		else
		{
			 apps = applicationService.getApplications(userService.getOwnerStrByUser(user));
		}
		
		GridData grid = new GridData();
		grid.setData(apps);
		return grid;
	}

	@RequestMapping(value = "/application/getApplication")
	public Ku8Project getApplication(int ku8ID, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		Ku8Project ku8p = applicationService.getApplication(ku8ID);
		
		if(ku8p == null)
		{
			log.error("APP NOT FOUND, id" + ku8ID);
			return null;
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8p.getOwner().equals(user.getUserId()))
		{
			log.error("Application id " + ku8ID + " does not belong to user: " + user.getUserId());
			return null;
		}
		else
		{
			return ku8p;
		}
	}
	
	@RequestMapping(value = "/application/deleteApplication")
	public AjaxReponse deleteApplication(int ku8ID, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Deleting app id:" + ku8ID);
		
		Ku8Project ku8p = applicationService.getApplication(ku8ID);
		
		if(ku8p == null)
		{
			log.error("APP NOT FOUND, id" + ku8ID);
			return new AjaxReponse(-1, "APP NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8p.getOwner().equals(user.getUserId()))
		{
			log.error("Application id " + ku8ID + " does not belong to user: " + user.getUserId());
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}
		
		String json = ku8p.getJsonSpec();
		Project p = Project.getFromJSON(json);
		
		if(p == null)
		{
			log.error("APP DATA CORRUPTED, id" + ku8ID);
			return new AjaxReponse(-1, "APP DATA CORUUPTED");
		}
		
		if(StringUtils.isAnyBlank(ku8p.getResPart()))
		{
			log.info("App id " + ku8ID + " has not been deployed, skipping K8 Delete");
		}
		else
		{
			for (Service s : p.getServices())
			{
				try
				{
					boolean deleted = false;
					if(log.isInfoEnabled())
						log.info("Deleting RC: " + s.getName());
					deleted = k8sAPIService.deleteReplicationController(ku8p.getClusterId(), ku8p.getResPart(), s.getName());
					if(!deleted)
						log.error("Failed to delete RC, " + s.getName());
					
					if(log.isInfoEnabled())
						log.info("Deleting Service: " + s.getName());
					deleted = k8sAPIService.deleteService(ku8p.getClusterId(), ku8p.getResPart(), s.getName());
					if(!deleted)
						log.error("Failed to delete RC, " + s.getName());
				}
				catch (KubernetesClientException e)
				{
					String msg;
					if(e.getStatus() != null)
						msg = e.getStatus().getMessage();
					else
						msg = e.getMessage();
					
					log.error("Delete RC/Service failed, " + msg);
					return new AjaxReponse(-1, "APP DELETE FAILED<br/>"+ msg);
				}
			}
		}
		
		int res = applicationService.deleteApplication(ku8ID);
		
		if (res == -1)
		{
			log.error("App id " + ku8ID + " failed to delete, SQL returned " + res);
			return new AjaxReponse(res, "APP DELETE FAILED");
		}
		else
		{
			if(log.isInfoEnabled())
				log.info("App id " + ku8ID + " deleted");
			return new AjaxReponse(res, "APP <strong>" + ku8p.getName() + "</strong> DELETED");
		}
	}

	@RequestMapping(value = "/application/addApplication")
	public AjaxReponse addApplication(String name, String version, String note, String jsonStr, ModelMap model)
	{
		if(log.isInfoEnabled())
			log.info("Adding app name:" + name);
		
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}

		if (name.isEmpty() || version.isEmpty() || jsonStr.isEmpty())
		{
			log.error("EMPTY FIELDS");
			return new AjaxReponse(-1, "EMPTY FIELDS");
		}

		int res = applicationService.addApplication(user.getTenantId(), user.getUserId(), name, version, note, jsonStr);

		if (res == -1)
		{
			log.error("App " + name + " failed to add, SQL returned " + res);
			return new AjaxReponse(res, "ADD FAILED");
		}
		else
		{
			if(log.isInfoEnabled())
				log.info("App " + name + " added");
			return new AjaxReponse(res, "ADDED");
		}
	}

	private AjaxReponse deployApplication(Ku8Project ku8p, List<String> namespaces, User user)
	{
		if(log.isInfoEnabled())
		{
			log.info("Deploying app id:" + ku8p.getId() + ", with namespace: " + namespaces);
			log.info("Setting app to deploying, status:" + Constants.KU8_APP_DEPLOYING_STATUS);
		}
		
		// Validation done
		// Get JSON
		String json = ku8p.getJsonSpec();
		
		if(log.isDebugEnabled())
			log.debug("Getting json str: " + json);

		// Parse JSON from the application
		Project p = Project.getFromJSON(json);
		
		if(p == null)
		{
			ku8p.setStatus(Constants.KU8_APP_FAILED_STATUS);
			applicationService.updateApplication(ku8p);
			
			log.error("APP DATA CORRUPTED, id" + ku8p.getId());
			return new AjaxReponse(-1, "APP DATA CORRUPTED");
		}
		
		if(log.isInfoEnabled())
			log.info("Project:" + p.getProjectName() + " found");

		boolean failed = false;
		String failedDetails = "";
		String namespace = null;
		int progress = 100 / (namespaces.size() * p.getServices().size());
		
		//Created RC/Service temp
		List<String> created = new ArrayList<String>(p.getServices().size());
		
		//Loop namespaces
		for (int i = 0; i < namespaces.size();)
		{	
			namespace = namespaces.get(i);
			
			for (Service s : p.getServices())
			{
				boolean isSrvCreated = false;
				
				try
				{
					if(log.isInfoEnabled())
						log.info("Creating service:" + s.getName());
					
					_K8toF8APIService.buildService(ku8p.getClusterId(), namespace, false, p.getProjectName(), s, user);
					isSrvCreated = true;
					
					if(log.isInfoEnabled())
						log.info("Creating RC:" + s.getName());
					
					_K8toF8APIService.buildReplicationController(ku8p.getClusterId(), namespace, false, p.getProjectName(), s, user);
					
					//Add to temp list
					created.add(s.getName());
					
					//Add progress
					int oldprogress = 0;
					if(ku8p.getProgress() != null)
						oldprogress = ku8p.getProgress();
					ku8p.setProgress(oldprogress + progress);
					applicationService.updateApplication(ku8p);
				}
				catch (KubernetesClientException e)
				{
					//Delete f8serv only if RC fails
					if(isSrvCreated)
					{
						if(log.isInfoEnabled())
							log.info("RC failed to create, Deleting Service: " + s.getName());
						k8sAPIService.deleteService(ku8p.getClusterId(), namespace, s.getName());
					}
					
					String msg;
					if(e.getStatus() != null)
						msg = e.getStatus().getMessage();
					else
						msg = e.getMessage();
					
					failedDetails += msg  + "<br/>";
					log.error("Deploy RC/Service failed, " + msg);
					failed = true;
				}
			}
			
			//Only 1 namespace allowed by the moment
			break;
		}

		if (failed)
		{
			//Clean orphaned RC/Service
			if(log.isInfoEnabled())
				log.info("Rollback, cleaning orphaned RC/Service, " + created);
			for(String name : created)
			{
				if(log.isInfoEnabled())
					log.info("Deleting Service: " + name);
				k8sAPIService.deleteService(ku8p.getClusterId(), namespace, name);
				
				if(log.isInfoEnabled())
					log.info("Deleting RC: " + name);
				k8sAPIService.deleteReplicationController(ku8p.getClusterId(), namespace, name);
			}
			
			log.error("App: " + ku8p.getName() +" failed to deploy, " + failedDetails);
			
			// Failed, set status to FAILED
			ku8p.setStatus(Constants.KU8_APP_FAILED_STATUS);
			applicationService.updateApplication(ku8p);
			return new AjaxReponse(-1, "APP <strong>" + ku8p.getName() + "</strong> FAILED TO DEPLOY<br/>"+ failedDetails);
		}
		else
		{
			if(log.isInfoEnabled())
				log.info("App: " + ku8p.getName() +" successfully deployed.");
			
			// Success, set status to 2
			ku8p.setResPart(namespace);
			ku8p.setStatus(Constants.KU8_APP_DEPLOYED_STATUS);
			applicationService.updateApplication(ku8p);
			return new AjaxReponse(1, "APP <strong>" + ku8p.getName() + "</strong> SUCCESSFULLY DEPLOYED");
		}
	}
	
	@RequestMapping(value = "/application/deployApplication")
	public AjaxReponse deployApplication(int ku8ID, String namespaces, ModelMap model)
	{	
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}

		Ku8Project ku8p = applicationService.getApplication(ku8ID);
		List<String> namespaceList = JSONUtil.toObjectList(namespaces, String.class);
		if(namespaceList == null)
		{
			log.error("APP DATA CORRUPTED");
			return new AjaxReponse(-1, "APP DATA CORRUPTED");
		}

		if(ku8p == null)
		{
			log.error("APP NOT FOUND, id" + ku8ID);
			return new AjaxReponse(-1, "APP NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8p.getOwner().equals(user.getUserId()))
		{
			log.error("Application id " + ku8ID + " does not belong to user: " + user.getUserId());
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}
		else
		{
			// Start deploy, set status to 1, deploying
			ku8p.setProgress(0);
			ku8p.setStatus(Constants.KU8_APP_DEPLOYING_STATUS);
			applicationService.updateApplication(ku8p);
			
			return deployApplication(ku8p, namespaceList, user);
		}
	}
	
	@RequestMapping(value = "/application/getServices")
	public GridData getApplicationServices(int ku8ID, ModelMap model)
	{
		if(log.isInfoEnabled())
			log.info("Getting services for app id:" + ku8ID);
		
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}

		Ku8Project ku8p = applicationService.getApplication(ku8ID);

		if(ku8p == null)
		{
			log.error("APP NOT FOUND, id" + ku8ID);
			return null;
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8p.getOwner().equals(user.getUserId()))
		{
			log.error("Application id " + ku8ID + " does not belong to user: " + user.getUserId());
			return null;
		}
		else
		{
			try
			{
				List<Service> beanServices = _F8toK8APIService.getServices(ku8p.getName(), false, ku8p.getResPart(), ku8p.getClusterId(), user);
				
				GridData grid = new GridData();
				grid.setData(beanServices);
				return grid;
			}
			catch (KubernetesClientException e)
			{
				log.error("Kubernetes error on getApplicationServices, " + e.getMessage());
				return null;
			}
		}
	}
	
	@RequestMapping(value = "/application/getPods")
	public GridData getApplicationPods(int ku8ID, String name, ModelMap model)
	{
		if(log.isInfoEnabled())
			log.info("Getting pods for service:" + name +", app id: " + ku8ID);

		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}

		Ku8Project ku8p = applicationService.getApplication(ku8ID);

		if(ku8p == null)
		{
			log.error("APP NOT FOUND, id" + ku8ID);
			return null;
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8p.getOwner().equals(user.getUserId()))
		{
			log.error("Application id " + ku8ID + " does not belong to user: " + user.getUserId());
			return null;
		}
		else
		{
			try
			{
				List<Pod> beanPods = _F8toK8APIService.getPods(ku8p.getClusterId(), ku8p.getResPart(), name, user);
	
				GridData grid = new GridData();
				grid.setData(beanPods);
				return grid;
			}
			catch (KubernetesClientException e)
			{
				log.error("Kubernetes error on getPods, " + e.getMessage());
				return null;
			}
		}
	}
	
	@RequestMapping(value = "/application/getPodLogs")
	public AjaxReponse getApplicationPodLogs(int ku8ID, String podName, String containerName, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Getting logs for pod: " + podName + ", in Container: " + containerName);

		Ku8Project ku8p = applicationService.getApplication(ku8ID);

		if (ku8p == null)
		{
			log.error("APPLICATION NOT FOUND, id" + ku8ID);
			return new AjaxReponse(-1, "APPLICATION NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8p.getOwner().equals(user.getUserId()))
		{
			log.error("Service id " + ku8ID + " does not belong to user: " + user.getUserId());
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}
		else
		{
			try
			{
				String log = k8sAPIService.getPodLogByName(ku8p.getClusterId(), ku8p.getResPart(), podName, containerName);
				return new AjaxReponse(1, log);
			}
			catch (KubernetesClientException e)
			{
				String msg;
				if(e.getStatus() != null)
					msg = e.getStatus().getMessage();
				else
					msg = e.getMessage();
				
				log.error("Kubernetes error on getPodLogs, " + msg);
				return new AjaxReponse(-1, "GET POD LOGS<br/>" + msg);
			}
		}
	}
	
	@RequestMapping(value = "/application/deletePod")
	public AjaxReponse deleteApplicationPod(int ku8ID, String name, ModelMap model)
	{
		if(log.isInfoEnabled())
			log.info("Deleting pod:" + name +", on app id: " + ku8ID);
		
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}

		Ku8Project ku8p = applicationService.getApplication(ku8ID);

		if(ku8p == null)
		{
			log.error("APP NOT FOUND, id" + ku8ID);
			return new AjaxReponse(-1, "APP NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8p.getOwner().equals(user.getUserId()))
		{
			log.error("Application id " + ku8ID + " does not belong to user: " + user.getUserId());
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}
		else
		{
			try
			{
				boolean res = k8sAPIService.deletePod(ku8p.getClusterId(), ku8p.getResPart(), name);
				
				if(res)
				{
					log.info("Pod delete, name: " + name +", app id: " + ku8ID);
					return new AjaxReponse(1, "SUCCESS");
				}
				else
				{
					log.error("Delete pod failed, name: " + name +", app id: " + ku8ID);
					return new AjaxReponse(-1, "STOP POD FAILED");
				}
			}
			catch (KubernetesClientException e)
			{
				String msg;
				if(e.getStatus() != null)
					msg = e.getStatus().getMessage();
				else
					msg = e.getMessage();
				
				log.error("Kubernetes error on deleteApplicationPod, " + msg);
				return new AjaxReponse(-1, "STOP POD FAILED<br/>" + msg);
			}
		}
	}
	
	@RequestMapping(value = "/application/setReplica")
	public AjaxReponse setApplicationReplica(int ku8ID, int replicas, String serviceName, ModelMap model)
	{
		if(log.isInfoEnabled())
			log.info("Scaling app id:" + ku8ID + ", replicas:" + replicas + ", for service:" + serviceName);
		
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}

		Ku8Project ku8p = applicationService.getApplication(ku8ID);

		if(ku8p == null)
		{
			log.error("APP NOT FOUND, id" + ku8ID);
			return new AjaxReponse(-1, "APP NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8p.getOwner().equals(user.getUserId()))
		{
			log.error("Application id " + ku8ID + " does not belong to user: " + user.getUserId());
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}
		else
		{
			try
			{
				if(log.isInfoEnabled())
					log.info("Scaling RC:" + serviceName + ", from cluster:" + ku8p.getClusterId() +", res_partition:" + ku8p.getResPart());
				
				ReplicationController response_f8ReplicationController = k8sAPIService.scaleReplicationController(ku8p.getClusterId(), ku8p.getResPart(), serviceName, replicas);
				
				if(response_f8ReplicationController != null)
				{
					if(log.isInfoEnabled())
						log.info("RC " + serviceName + " scaled");
					return new AjaxReponse(1, "SUCCESS");
				}
				else
				{	
					log.error("RC " + serviceName + " failed to updated");
					return new AjaxReponse(-1, "SET REPLICA FAILED");
				}
			}
			catch (KubernetesClientException e)
			{
				log.error("Kubernetes error on setReplica, " + e.getMessage());
				return new AjaxReponse(-1, "SERVER NOT FOUND");
			}
		}
	}
	
	@RequestMapping(value = "/application/getContainer")
	public GridData getApplicationContainer(int ku8ID, @RequestParam("name") String serviceName, int cIndex, ModelMap model)
	{
		if(log.isInfoEnabled())
			log.info("Getting container: " + cIndex + ", on service: " + serviceName + ", for app id:" + ku8ID);
		
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}

		Ku8Project ku8p = applicationService.getApplication(ku8ID);

		if(ku8p == null)
		{
			log.error("APP NOT FOUND, id" + ku8ID);
			return null;
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8p.getOwner().equals(user.getUserId()))
		{
			log.error("Application id " + ku8ID + " does not belong to user: " + user.getUserId());
			return null;
		}
		else
		{
			try
			{
				Container beanContainer = _F8toK8APIService.getContainerByService(ku8p.getName(), false, serviceName, cIndex, ku8p.getResPart(), ku8p.getClusterId(), user);
				
				GridData grid = new GridData();
				grid.setData(beanContainer);
				return grid;
			}
			catch (KubernetesClientException e)
			{
				log.error("Kubernetes error on getApplicationContainer, " + e.getMessage());
				return null;
			}
		}
	}
	
	@RequestMapping(value = "/application/updateContainer")
	public AjaxReponse updateApplicationContainer(int ku8ID, String serviceName, int containerIndex, String jsonStr, ModelMap model)
	{
		if(log.isInfoEnabled())
			log.info("Updating container index:" + containerIndex + ", app id:" + ku8ID +", service:" + serviceName);
		
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}
		
		if (jsonStr.isEmpty())
		{
			log.error("EMPTY FIELDS");
			return new AjaxReponse(-1, "EMPTY FIELDS");
		}

		Ku8Project ku8p = applicationService.getApplication(ku8ID);

		if(ku8p == null)
		{
			log.error("APP NOT FOUND, id" + ku8ID);
			return new AjaxReponse(-1, "APP NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8p.getOwner().equals(user.getUserId()))
		{
			log.error("Application id " + ku8ID + " does not belong to user: " + user.getUserId());
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}
		else
		{	
			// Validation done
			// Get JSON
			String project_json = ku8p.getJsonSpec();
			
			if(log.isDebugEnabled())
				log.debug("Getting json str: " + project_json);

			// Parse JSON from the application
			Project p = Project.getFromJSON(project_json);
			
			if(p == null)
			{
				log.error("APP DATA CORRUPTED, id" + ku8ID);
				return new AjaxReponse(-1, "APP DATA CORRUPTED");
			}
			
			if(log.isInfoEnabled())
				log.info("Application:" + p.getProjectName() + " found");

			boolean found = false;
			
			//Find the service
			for (Service s : p.getServices())
			{
				if(s.getName().equals(serviceName))
				{	
					if(log.isInfoEnabled())
						log.info("Service:" + s.getName() + " found");
				
					//Get the container
					if(containerIndex < s.getContainers().length)
					{
						found = true;
						Container beanContainer = s.getContainers()[containerIndex];
						
						//Create temporary container from JSON
						Container tempContainer = JSONUtil.toObject(jsonStr, Container.class);
						
						beanContainer.setEnvVariables(tempContainer.getEnvVariables());
						beanContainer.setVolumes(tempContainer.getVolumes());
						beanContainer.setLivenessProbe(tempContainer.getLivenessProbe());
						beanContainer.setImageId(tempContainer.getImageId());
						
						try
						{
							_K8toF8APIService.updateContainer(ku8p.getClusterId(), ku8p.getResPart(), serviceName, containerIndex, beanContainer, user);
							 
							//Update DB
							ku8p.setPrevJsonSpec(project_json);
							ku8p.setJsonSpec(p.toJSONString());
							applicationService.updateApplication(ku8p);
						}
						catch (KubernetesClientException e)
						{
							String msg;
							if(e.getStatus() != null)
								msg = e.getStatus().getMessage();
							else
								msg = e.getMessage();
							
							log.error("App id:" + ku8ID +", service:" + serviceName +", container index:" + containerIndex +", failed to update," + msg);
							return new AjaxReponse(-1, "CONTAINER FAILED TO UPDATE<br/>" + msg);
						}
					}
					break;
				}
			}

			if (!found)
			{
				log.error("App id:" + ku8ID +", service:" + serviceName +", container index:" + containerIndex +", not found");
				return new AjaxReponse(-1, "CONTAINER FAILED TO UPDATE");
			}
			else
			{
				if(log.isInfoEnabled())
					log.info("App id:" + ku8ID +", service:" + serviceName +", container index:" + containerIndex +", updated");
				return new AjaxReponse(1, "SUCCESS");
			}
		}
	}
	
	private List<String> getNamespacesByUser(int clusterID, String userID)
	{
		try
		{
			int i = 0;
			//New list to avoid UnsuportedOperationException
			List<String> usernamespaces = new ArrayList<String>(userService.getResourcePartitionByUserId(userID));
			//System.out.println("==================1:" + userID);
			//System.out.println("==================2:" + clusterID);
			for (String s : usernamespaces) {
				System.out.println("The "+i+"th item in ku8namespaces is" + s);
				i = i+1;
			}
			List<String> ku8namespaces = ku8ResPartionService.getAllNameSpaceName(clusterID);
			System.out.println("The available clusterID is ["+clusterID+"]");
			
			System.out.println("==================4:" + ku8namespaces);
			if(ku8namespaces == null || usernamespaces == null) {
				System.out.println("namespace为空");
				return null;
			};
			for (String s : ku8namespaces) {
				System.out.println("The "+i+"th item in ku8namespaces is" + s);
				i = i+1;
			}
			usernamespaces.removeIf(p -> !ku8namespaces.contains(p));
			System.out.println("==================6:" + usernamespaces.size());
			for (String s : usernamespaces) {
				System.out.println("==================5" + s);
			}
			return usernamespaces;
		}
		catch (KubernetesClientException e)
		{
			log.error("Error while retrieving partitions, " + e.getMessage());
			return null;
		}
		catch (Exception ex)
		{
			log.error("General exception, " + ex.getMessage());
			return null;
		}
	}
	
	@RequestMapping(value = "/application/getNamespacesByUser")
	public GridData getNamespacesPerCluster(ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		List<Integer> clusters = new ArrayList<Integer>();
		clusters.add(0);
		
		List<ClusterNamepace> clusterNamespaceList = new ArrayList<ClusterNamepace>();
		
		for (int clusterID : clusters)
		{
			if(log.isInfoEnabled())
				log.info("Looping cluster: " + clusterID);
			try
			{
				List<String> namespaces = getNamespacesByUser(clusterID, user.getUserId());
				
				if(namespaces == null)
				{
					log.info("No namespace found, continue");
					continue;
				}
				
				if(log.isInfoEnabled())
					log.info("Found namespaces: " + namespaces);
				
				ClusterNamepace clusterNamespace = new ClusterNamepace(namespaces);
				clusterNamespace.setClusterID(clusterID);
				clusterNamespace.setClusterName("Cluster " + clusterID);
					
				clusterNamespaceList.add(clusterNamespace);
			}
			catch (KubernetesClientException e)
			{
				log.error("Error while retrieving partitions, " + e.getMessage());
				return null;
			}
		}
		GridData grid = new GridData();
		grid.setData(clusterNamespaceList);
		return grid;
	}
	
	@RequestMapping(value = "/application/importApplication")
	public AjaxReponse importApplication(String namespace, String serviceName, ModelMap model)
	{
		if(serviceName.equals("") || namespace.equals(""))
			return null;
		
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		try
		{
			Service srv = _F8toK8APIService.getService(serviceName, false, namespace, 0, user);
			
			if(srv == null)
			{
				return new AjaxReponse(-1, "Service " + serviceName + ", namespace: " + namespace + "not found");
			}
			
			for(Container c : srv.getContainers())
				c.setImageId(-1);
			
			Project app = new Project();
			app.setAuthor(user.getUserId());
			app.setProjectName(serviceName);
			app.setNotes(serviceName);
			app.setVersion("1");
			app.setResPart(namespace);
			app.addService(srv);
			
			Map<String, String> labels = new HashMap<String, String>();
			labels.put(Constants.KU8_APPNAME, serviceName);
			labels.put(Constants.KU8_APPTYPE, Constants.KU8_PRIVATE);
			
			k8sAPIService.addLabelsReplicationController(0, Constants.KU8_DEFAULT_NAMESPACE, serviceName, labels);
			k8sAPIService.addLabelsService(0, Constants.KU8_DEFAULT_NAMESPACE, serviceName, labels);
			
			int res = applicationService.importApplication(user.getTenantId(), user.getUserId(), serviceName, "1", "", app.toJSONString(), namespace);
	
			if (res == -1)
			{
				log.error("[USER: " + user.getUserId() + "] App " + serviceName + " failed to add, SQL returned " + res);
				return new AjaxReponse(res, "IMPORT FAILED");
			}
			else
			{
				if(log.isInfoEnabled())
					log.info("[USER: " + user.getUserId() + "] App " + serviceName + " imported");
				return new AjaxReponse(1, "APP <strong>" + serviceName + "</strong> SUCCESSFULLY IMPORTED");
			}
		}
		catch (KubernetesClientException e)
		{
			String msg = "Error while getting Service " + serviceName + ", namespace: " + namespace + ", " + e.getMessage();
			log.error(msg);
			return new AjaxReponse(-1, "IMPORT FAILED<br/>" + msg);
		}
	}
	
	@RequestMapping(value = "/application/updateLabels")
	public AjaxReponse updateServiceLabels(int ku8ID, @RequestParam("name") String serviceName, String jsonStr, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Updating labels on Service:" + serviceName + ", id:" + ku8ID +", lbls:" + jsonStr);

		Ku8Project ku8p = applicationService.getApplication(ku8ID);
		
		if(ku8p == null)
		{
			log.error("[USER: " + user.getUserId() + "] APP NOT FOUND, id" + ku8ID);
			return new AjaxReponse(-1, "APP NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8p.getOwner().equals(user.getUserId()))
		{
			log.error("[USER: " + user.getUserId() + "] Application id " + ku8ID + " does not belong to this user");
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}
		else
		{
			
			@SuppressWarnings("unchecked")
			HashMap<String, String> lmap = JSONUtil.toObject(jsonStr, HashMap.class);
			lmap.put(Constants.KU8_APPNAME, ku8p.getName());
			lmap.put(Constants.KU8_APPTYPE, Constants.KU8_PRIVATE);
			
			_K8toF8APIService.updateRCServiceLabels(ku8p.getClusterId(), ku8p.getResPart(), serviceName, lmap, user);
			
			//Update DB
			Project p = Project.getFromJSON(ku8p.getJsonSpec());
			for(Service svc : p.getServices())
			{
				if(svc.getName().equals(serviceName))
				{
					svc.setLabels(lmap);
					break;
				}
			}
			applicationService.updateApplicationJSON(ku8p.getId(), p.toJSONString(), ku8p.getJsonSpec());
			
			return new AjaxReponse(1, "SERVICE <strong>" + serviceName + "</strong> LABELS SUCCESSFULLY UPDATED");
		}
	}
}
