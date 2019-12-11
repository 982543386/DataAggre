package org.ku8eye.ctrl;

import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.client.KubernetesClientException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.ku8eye.Constants;
import org.ku8eye.bean.GridData;
import org.ku8eye.bean.project.Container;
import org.ku8eye.bean.project.Pod;
import org.ku8eye.bean.project.Service;
import org.ku8eye.domain.Ku8Service;
import org.ku8eye.domain.User;
import org.ku8eye.service.ServiceAndRcService;
import org.ku8eye.service.UserService;
import org.ku8eye.service.k8s.F8toK8APIService;
import org.ku8eye.service.k8s.K8sAPIService;
import org.ku8eye.service.k8s.K8toF8APIService;
import org.ku8eye.util.AjaxReponse;
import org.ku8eye.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.cudcos.util.CacheDcosUrlConfig;
import com.cudcos.util.CallRestUtil;
import com.cudcos.util.CallResult;
import com.iiot.utils.SystemUtil;

/**
 * @author Federico Gu 
 * Email: federico.gu@hpe.com
 * Date: 2016-05 
 * Description: Controller for Ku8 Public Services
 */
@RestController
@SessionAttributes(org.ku8eye.Constants.USER_SESSION_KEY)
public class ServiceAndRcController
{
	private static final Logger log = Logger.getLogger(ServiceAndRcController.class);

	@Autowired
	private ServiceAndRcService serviceAndRcService;
	
	@Autowired
	private K8toF8APIService _K8toF8APIService;
	
	@Autowired
	private F8toK8APIService _F8toK8APIService;
	
	@Autowired
	private K8sAPIService k8sAPIService;
	
	@Autowired
	private UserService userService;

	@RequestMapping(value = "/publicservice/getServiceAndRCs", method = RequestMethod.GET)
	public GridData getServiceAndRCs(ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		List<Ku8Service> sr;
		
		if (user.getUserType().equals(Constants.USERTYPE_ADMIN))
		{
			 sr = serviceAndRcService.getAllServiceAndRc();
		}
		else
		{
			 sr = serviceAndRcService.getAllServiceAndRc(userService.getOwnerStrByUser(user));
		}
		
		GridData grid = new GridData();
		grid.setData(sr);
		return grid;
	}
	
	@RequestMapping(value = "/publicservice/getServiceAndRC")
	public Ku8Service getServiceAndRC(int ku8ID, ModelMap model)
	{
		if(log.isInfoEnabled())
			log.info("Getting public service id:" + ku8ID);
		
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		Ku8Service ku8s = serviceAndRcService.getServiceAndRc(ku8ID);
		
		if(ku8s == null)
		{
			log.error("PUBLIC SERVICE NOT FOUND, id" + ku8ID);
			return null;
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8s.getOwner().equals(user.getUserId()))
		{
			log.error("Service id " + ku8ID + " does not belong to user: " + user.getUserId());
			return null;
		}
		else
		{
			return ku8s;
		}
	}
	
	@RequestMapping(value = "/publicservice/deleteService")
	public AjaxReponse deleteService(int ku8ID, ModelMap model)
	{
		if(log.isInfoEnabled())
			log.info("Deleting service id:" + ku8ID);
		
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}
		
		Ku8Service ku8s = serviceAndRcService.getServiceAndRc(ku8ID);
		
		if(ku8s == null)
		{
			log.error("PUBLIC SERVICE NOT FOUND, id" + ku8ID);
			return new AjaxReponse(-1, "PUBLIC SERVICE NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8s.getOwner().equals(user.getUserId()))
		{
			log.error("Service id " + ku8ID + " does not belong to user: " + user.getUserId());
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}
		
		String json = ku8s.getJsonSpec();
		Service s = Service.getFromJSON(json);
		
		if(ku8s.getResPart() == null || ku8s.getResPart().isEmpty())
		{
			log.info("Public Service id " + ku8ID + " has not been deployed, skipping K8 Delete");
		}
		else
		{
			try
			{
				k8sAPIService.deleteReplicationController(ku8s.getClusterId(), ku8s.getResPart(), s.getName());
				k8sAPIService.deleteService(ku8s.getClusterId(), ku8s.getResPart(), s.getName());
			}
			catch (KubernetesClientException e)
			{
				String msg;
				if(e.getStatus() != null)
					msg = e.getStatus().getMessage();
				else
					msg = e.getMessage();
				
				log.error("Delete RC/Service failed, " + msg);
				return new AjaxReponse(-1, "SERVICE DELETE FAILED<br/>"+ msg);
			}
		}
			
		int res = serviceAndRcService.deleteService(ku8ID);
		
		if (res == -1)
		{
			log.error("Service id " + ku8ID + " failed to delete, SQL returned " + res);
			return new AjaxReponse(res, "SERVICE DELETE FAILED");
		}
		else
		{
			if(log.isInfoEnabled())
				log.info("Service id " + ku8ID + " deleted");
			return new AjaxReponse(res, "SERVICE <strong>" + ku8s.getName() + "</strong> DELETED");
		}
	}

	@RequestMapping(value = "/publicservice/addServiceAndRC", method = RequestMethod.POST)
	public AjaxReponse addServiceAndRc(String jsonStr, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}

		if (jsonStr.isEmpty())
		{
			log.error("JSON STRING IS EMPTY");
			return new AjaxReponse(-1, "JSON STRING IS EMPTY");
		}

		Service s = Service.getFromJSON(jsonStr);
		
		if(s == null)
		{
			log.error("PUBLIC SERVICE DATA CORRUPTED");
			return new AjaxReponse(-1, "PUBLIC SERVICE DATA CORRUPTED");
		}
		
		if(log.isInfoEnabled())
			log.info("Adding service name:" + s.getName());
		
		int res = serviceAndRcService.addServiceAndRc(user.getTenantId(), user.getUserId(), s);

		if (res == -1)
		{
			log.error("Service " + s.getName() + " failed to add, SQL returned " + res);
			return new AjaxReponse(res, "ADD FAILED");
		}
		else
		{
			if(log.isInfoEnabled())
				log.info("Service " + s.getName() + " added");
			return new AjaxReponse(res, "ADDED");
		}
	}
	
	@RequestMapping(value = "/publicservice/getK8ServiceAndRC")
	public GridData getK8ServiceAndRC(int ku8ID, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		Ku8Service ku8s = getServiceAndRC(ku8ID, model);
		
		if(ku8s != null)
		{
			try
			{
				List<Service> beanServices = _F8toK8APIService.getServices(ku8s.getName(), true, ku8s.getResPart(), ku8s.getClusterId(), user);
				
				if(beanServices.size() > 1)
				{
					log.error("Returned " + beanServices.size() + " beanServices, Public RC and Service should only have 1.");
					return null;
				}
				
				Service beanService = beanServices.get(0);
				beanService.setDescribe(ku8s.getNote());
				
				GridData grid = new GridData();
				grid.setData(beanService);
				return grid;
			}
			catch (KubernetesClientException e)
			{
				log.error("Kubernetes error on getServiceAndRC, " + e.getMessage());
				return null;
			}
		}
		return null;
	}
	
	private AjaxReponse deployServiceAndRCWithNamespace(Ku8Service ku8s, List<String> namespaces, User user)
	{
		if(log.isInfoEnabled())
		{
			log.info("Deploying service id:" + ku8s.getId() + ", with namespaces:" + namespaces);
			log.info("Setting service to deploying, status:" + Constants.KU8_APP_DEPLOYING_STATUS);
		}
		
		// Validation done
		// Get JSON
		String json = ku8s.getJsonSpec();
		
		if(log.isDebugEnabled())
			log.debug("Getting json str: " + json);

		// Parse JSON from the application
		Service s = Service.getFromJSON(json);
		
		if(s == null)
		{
			ku8s.setStatus(Constants.KU8_APP_FAILED_STATUS);
			serviceAndRcService.updateServiceAndRc(ku8s);
			
			log.error("PUBLIC SERVICE DATA CORRUPTED, id" + ku8s.getId());
			return new AjaxReponse(-1, "PUBLIC SERVICE DATA CORRUPTED");
		}
		
		if(log.isInfoEnabled())
			log.info("Service:" + s.getName() + " found");

		boolean failed = false;
		String failedDetails = "";
		String namespace = null;
		boolean isSrvCreated = false;
		int progress = 100 / namespaces.size();
		
		//Loop namespaces
		for (int i = 0; i < namespaces.size();)
		{
			namespace = namespaces.get(0);
			try
			{
				if(log.isInfoEnabled())
					log.info("Creating service:" + s.getName());
				
				_K8toF8APIService.buildService(ku8s.getClusterId(), namespace, true, s.getName(), s, user);
				isSrvCreated = true;
				
				if(log.isInfoEnabled())
					log.info("Creating RC:" + s.getName());
				
				_K8toF8APIService.buildReplicationController(ku8s.getClusterId(), namespace, true, s.getName(), s, user);
				
				//Add progress
				int oldprogress = 0;
				if(ku8s.getProgress() != null)
					oldprogress = ku8s.getProgress();
				ku8s.setProgress(oldprogress + progress);
				serviceAndRcService.updateServiceAndRc(ku8s);
				
			}
			catch (KubernetesClientException e)
			{
				//Delete f8serv only if RC fails
				if(isSrvCreated)
				{
					if(log.isInfoEnabled())
						log.info("RC failed to create, Deleting Service: " + s.getName());
					k8sAPIService.deleteService(ku8s.getClusterId(), namespace, s.getName());
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

			//Only 1 namespace allowed by the moment
			break;
		}

		if (failed)
		{
			log.error("Service:" + s.getName() +" failed to deploy, " + failedDetails);
			
			// Failed, set status to -1
			ku8s.setStatus(Constants.KU8_APP_FAILED_STATUS);
			serviceAndRcService.updateServiceAndRc(ku8s);
			return new AjaxReponse(-1, "SERVICE <strong>" + s.getName() + "</strong> FAILED TO DEPLOY<br/>"+ failedDetails);
		}
		else
		{
			if(log.isInfoEnabled())
				log.info("Service:" + s.getName() +" successfully deployed.");
			
			// Success, set status to 2
			ku8s.setResPart(namespace);
			ku8s.setStatus(Constants.KU8_APP_DEPLOYED_STATUS);
			serviceAndRcService.updateServiceAndRc(ku8s);
			return new AjaxReponse(1, "SERVICE <strong>" + s.getName() + "</strong> SUCCESSFULLY DEPLOYED");
		}
	}
	
	@RequestMapping(value = "/publicservice/deployServiceAndRC")
	public AjaxReponse deployServiceAndRC(int ku8ID, String namespaces, ModelMap model)
	{
		if(log.isInfoEnabled())
			log.info("Deploying service id:" + ku8ID);
		
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}

		Ku8Service ku8s = serviceAndRcService.getServiceAndRc(ku8ID);
		List<String> namespaceList = JSONUtil.toObjectList(namespaces, String.class);
		if(namespaceList == null)
		{
			log.error("SERVICE DATA CORRUPTED");
			return new AjaxReponse(-1, "SERVICE DATA CORRUPTED");
		}

		if (ku8s == null)
		{
			log.error("PUBLIC SERVICE NOT FOUND, id" + ku8ID);
			return new AjaxReponse(-1, "SERVICE NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8s.getOwner().equals(user.getUserId()))
		{
			log.error("Service id " + ku8ID + " does not belong to user: " + user.getUserId());
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}
		else
		{
			// Start deploy, set status to 1, deploying
			ku8s.setProgress(0);
			ku8s.setStatus(Constants.KU8_APP_DEPLOYING_STATUS);
			serviceAndRcService.updateServiceAndRc(ku8s);
			
			return deployServiceAndRCWithNamespace(ku8s, namespaceList, user);
		}
	}
	
	@RequestMapping(value = "/publicservice/setReplica")
	public AjaxReponse setServiceAndRCReplica(int ku8ID, int replicas, ModelMap model)
	{
		if(log.isInfoEnabled())
			log.info("Scaling service id:" + ku8ID + ", replicas:" + replicas);
		
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}

		Ku8Service ku8s = serviceAndRcService.getServiceAndRc(ku8ID);

		if (ku8s == null)
		{
			log.error("PUBLIC SERVICE NOT FOUND, id" + ku8ID);
			return new AjaxReponse(-1, "SERVICE NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8s.getOwner().equals(user.getUserId()))
		{
			log.error("Service id " + ku8ID + " does not belong to user: " + user.getUserId());
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}
		else
		{
			try
			{
				if(log.isInfoEnabled())
					log.info("Scaling RC:" + ku8s.getName() + ", from cluster:" + ku8s.getClusterId() +", res_partition:" + ku8s.getResPart());
				
				ReplicationController response_f8ReplicationController = k8sAPIService.scaleReplicationController(ku8s.getClusterId(), ku8s.getResPart(), ku8s.getName(), replicas);
				
				if(response_f8ReplicationController != null)
				{
					if(log.isInfoEnabled())
						log.info("RC " + ku8s.getName() + " updated");
					return new AjaxReponse(1, "SUCCESS");
				}
				else
				{	
					log.error("RC " + ku8s.getName() + " failed to updated");
					return new AjaxReponse(-1, "SET REPLICA FAILED");
				}
			}
			catch (KubernetesClientException e)
			{
				log.error("Kubernetes error on setServiceAndRCReplica, " + e.getMessage());
				return new AjaxReponse(-1, "SERVER NOT FOUND");
			}
		}
	}
	
	@RequestMapping(value = "/publicservice/getPods")
	public GridData getServiceAndRCPods(int ku8ID, String name, ModelMap model)
	{
		if(log.isInfoEnabled())
			log.info("Getting pods for service:" + name +", service id: " + ku8ID);
		
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}

		Ku8Service ku8s = serviceAndRcService.getServiceAndRc(ku8ID);

		if (ku8s == null)
		{
			log.error("PUBLIC SERVICE NOT FOUND, id" + ku8ID);
			return null;
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8s.getOwner().equals(user.getUserId()))
		{
			log.error("Service id " + ku8ID + " does not belong to user: " + user.getUserId());
			return null;
		}
		else
		{
			try
			{
				List<Pod> beanPods = _F8toK8APIService.getPods(ku8s.getClusterId(), ku8s.getResPart(), name, user);
	
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
	
	@RequestMapping(value = "/publicservice/getPodLogs")
	public AjaxReponse getServiceAndRCPodLogs(int ku8ID, String podName, String containerName, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Getting logs for pod: " + podName + ", in Container: " + containerName);

		Ku8Service ku8s = serviceAndRcService.getServiceAndRc(ku8ID);

		if (ku8s == null)
		{
			log.error("PUBLIC SERVICE NOT FOUND, id" + ku8ID);
			return new AjaxReponse(-1, "PUBLIC SERVICE NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8s.getOwner().equals(user.getUserId()))
		{
			log.error("Service id " + ku8ID + " does not belong to user: " + user.getUserId());
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}
		else
		{
			try
			{
				String log = k8sAPIService.getPodLogByName(ku8s.getClusterId(), ku8s.getResPart(), podName, containerName);
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
	
	@RequestMapping(value = "/publicservice/deletePod")
	public AjaxReponse deleteServiceAndRCPod(int ku8ID, String name, ModelMap model)
	{
		if(log.isInfoEnabled())
			log.info("Deleting pod:" + name +", on service id: " + ku8ID);
		
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}

		Ku8Service ku8s = serviceAndRcService.getServiceAndRc(ku8ID);

		if (ku8s == null)
		{
			log.error("PUBLIC SERVICE NOT FOUND, id" + ku8ID);
			return new AjaxReponse(-1, "PUBLIC SERVICE NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8s.getOwner().equals(user.getUserId()))
		{
			log.error("Service id " + ku8ID + " does not belong to user: " + user.getUserId());
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}
		else
		{
			try
			{
				boolean res = k8sAPIService.deletePod(ku8s.getClusterId(), ku8s.getResPart(), name);
				
				if(res)
				{
					log.info("Pod deleted, name: " + name +", service id: " + ku8ID);
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
				
				log.error("Kubernetes error on deleteServiceAndRCPod, " + msg);
				return new AjaxReponse(-1, "STOP POD FAILED<br/>" + msg);
			}
		}
	}
	
	@RequestMapping(value = "/publicservice/getContainer")
	public GridData getServiceAndRCContainer(int ku8ID, @RequestParam("name") String serviceName, int cIndex, ModelMap model)
	{
		if(log.isInfoEnabled())
			log.info("Getting container: " + cIndex + ", on service: " + serviceName + ", on service id: " + ku8ID);
		
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}

		Ku8Service ku8s = serviceAndRcService.getServiceAndRc(ku8ID);

		if(ku8s == null)
		{
			log.error("PUBLIC SERVICE NOT FOUND, id" + ku8ID);
			return null;
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8s.getOwner().equals(user.getUserId()))
		{
			log.error("Application id " + ku8ID + " does not belong to user: " + user.getUserId());
			return null;
		}
		else
		{
			try
			{
				Container beanContainer = _F8toK8APIService.getContainerByService(ku8s.getName(), true, serviceName, cIndex, ku8s.getResPart(), ku8s.getClusterId(), user);
				
				GridData grid = new GridData();
				grid.setData(beanContainer);
				return grid;
			}
			catch (KubernetesClientException e)
			{
				log.error("Kubernetes error on getServiceAndRCContainer, " + e.getMessage());
				return null;
			}
		}
	}
	
	@RequestMapping(value = "/publicservice/updateContainer")
	public AjaxReponse updateServiceAndRCContainer(int ku8ID, int containerIndex, String jsonStr, ModelMap model)
	{
		if(log.isInfoEnabled())
			log.info("Updating container index:" + containerIndex + " on service id:" + ku8ID);
		
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

		Ku8Service ku8s = serviceAndRcService.getServiceAndRc(ku8ID);

		if (ku8s == null)
		{
			log.error("PUBLIC SERVICE NOT FOUND, id" + ku8ID);
			return new AjaxReponse(-1, "SERVICE NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8s.getOwner().equals(user.getUserId()))
		{
			log.error("Service id " + ku8ID + " does not belong to user: " + user.getUserId());
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}
		else
		{	
			// Validation done
			// Get JSON
			String service_json = ku8s.getJsonSpec();
			
			if(log.isDebugEnabled())
				log.debug("Getting json str: " + service_json);

			// Parse JSON from the service
			Service s = Service.getFromJSON(service_json);
			
			if(s == null)
			{
				log.error("PUBLIC SERVICE DATA CORRUPTED, id" + ku8ID);
				return new AjaxReponse(-1, "PUBLIC SERVICE DATA CORRUPTED");
			}
			
			if(log.isInfoEnabled())
				log.info("Service:" + s.getName() + " found");

			boolean found = false;
				
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
					_K8toF8APIService.updateContainer(ku8s.getClusterId(), ku8s.getResPart(), ku8s.getName(), containerIndex, beanContainer, user);
					 
					//Update DB
					 ku8s.setPrevJsonSpec(service_json);
					 ku8s.setJsonSpec(s.toJSONString());
					 serviceAndRcService.updateServiceAndRc(ku8s);
				}
				catch (KubernetesClientException e)
				{
					String msg;
					if(e.getStatus() != null)
						msg = e.getStatus().getMessage();
					else
						msg = e.getMessage();
					
					log.error("Service id:" + ku8ID +", service:" + s.getName() +", container index:" + containerIndex +", failed to update," + msg);
					return new AjaxReponse(-1, "CONTAINER FAILED TO UPDATE<br/>" + msg);
					
				}
			}

			if (!found)
			{
				log.error("Service id:" + ku8ID +", service:" + s.getName() +", container index:" + containerIndex +", not found");
				return new AjaxReponse(-1, "CONTAINER FAILED TO UPDATE");
			}
			else
			{
				if(log.isInfoEnabled())
					log.info("Service id:" + ku8ID +", service:" + s.getName() +", container index:" + containerIndex +", updated");
				return new AjaxReponse(1, "SUCCESS");
			}
		}
	}
	
	@RequestMapping(value = "/publicservice/updateLabels")
	public AjaxReponse updateServiceAndRCLabels(int ku8ID, @RequestParam("name") String serviceName, String jsonStr, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Updating labels on Service:" + serviceName + ", id:" + ku8ID +", lbls:" + jsonStr);
		
		if (jsonStr.isEmpty())
		{
			log.error("[USER: " + user.getUserId() + "] EMPTY FIELDS ON JSON FILE");
			return new AjaxReponse(-1, "EMPTY FIELDS on JSON FILE");
		}

		Ku8Service ku8s = serviceAndRcService.getServiceAndRc(ku8ID);

		if (ku8s == null)
		{
			log.error("[USER: " + user.getUserId() + "] PUBLIC SERVICE NOT FOUND, id" + ku8ID);
			return new AjaxReponse(-1, "SERVICE NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8s.getOwner().equals(user.getUserId()))
		{
			log.error("[USER: " + user.getUserId() + "] Public Service id " + ku8ID + " does not belong to this user");
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}
		else
		{	
			@SuppressWarnings("unchecked")
			HashMap<String, String> lmap = JSONUtil.toObject(jsonStr, HashMap.class);
			lmap.put(Constants.KU8_APPNAME, ku8s.getName());
			lmap.put(Constants.KU8_APPTYPE, Constants.KU8_PUBLIC);
			
			_K8toF8APIService.updateRCServiceLabels(ku8s.getClusterId(), ku8s.getResPart(), serviceName, lmap, user);
			
			//Update DB
			Service srv = Service.getFromJSON(ku8s.getJsonSpec());
			if(srv.getName().equals(serviceName))
				srv.setLabels(lmap);
			
			serviceAndRcService.updateServiceJSON(ku8s.getId(), srv.toJSONString(), ku8s.getJsonSpec());
			
			return new AjaxReponse(1, "SERVICE <strong>" + serviceName + "</strong> LABELS SUCCESSFULLY UPDATED");
		}
	}
	
	//SHARED METHOD
	@RequestMapping(value = "/publicservice/hasServiceName")
	public GridData hasServiceName(String serviceName, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Verifying Service Name: " + serviceName + " in SQL");
		
		List<String> res_parts = serviceAndRcService.hasServiceName(serviceName);
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Service Name: " + serviceName + " exists in: " + res_parts);
		
		GridData grid = new GridData(res_parts);
		return grid;
	}
	
	@RequestMapping(value = "/publicservice/hasNodePort")
	public GridData hasNodePort(String nodePort, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Verifying Node Port: " + nodePort + " in SQL");
		
		List<String> res_ports = serviceAndRcService.hasNodePort(nodePort);
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Node Port: " + nodePort + " exists in: " + res_ports);
		
		GridData grid = new GridData(res_ports);
		
		return grid;
	}
	
	/**
	 * 
	 *	上传yaml文件调用调用k8s接口进行发布服务
	 * @throws Exception 
	 */
	@RequestMapping(value="/publicservice/issueServiceByYaml", method=RequestMethod.POST)
	public CallResult issueServiceByYaml(@RequestBody HashMap<String,String> map) throws Exception {
		Properties props = SystemUtil.getSpringAppProperties();
		String externalRes = props.getProperty("ku8.externalRes");
		String prex = "file:";
		externalRes = externalRes.substring(prex.length());
		String savePath = externalRes + File.separator
				+ props.getProperty("ku8.uploadedYamlPath");
		File file = new File(savePath + File.separator + map.get("name"));
		String sbody="{\"name\":\""+map.get("name")+"\",\"content\":\""+txt2String(file)+"\"}";
		//System.out.println("sbody=="+sbody);
		CallResult callResult = CallRestUtil.callSendJsonObject(CacheDcosUrlConfig.kubernetesDashboardUri, HttpMethod.POST, null, sbody,
				"application/json, text/plain, */*","application/json;charset=utf-8");
		return callResult;
	}
	
	
	/*
	 * 上传yaml文件
	 *  
	 * */
	@RequestMapping(value = "/publicservice/upload-yamlFile")
	public String uploadYamlFile(HttpServletRequest request,@RequestParam(value = "file") MultipartFile[] files) throws Exception {
		Properties props = SystemUtil.getSpringAppProperties();
		String externalRes = props.getProperty("ku8.externalRes");
		String prex = "file:";
		externalRes = externalRes.substring(prex.length());
		String savePath = externalRes + File.separator
				+ props.getProperty("ku8.uploadedYamlPath");
		File path = new File(savePath);
		if (!path.exists()) {
			path.mkdirs();
		}
		DiskFileItemFactory fac = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(fac);
		upload.setHeaderEncoding("utf-8");
		for (MultipartFile mf : files) {
			File file=null;
			if (!mf.isEmpty()) {
				file = new File(savePath + File.separator
						+ mf.getOriginalFilename());

				InputStream inputStream = mf.getInputStream();
				OutputStream outputStream = new FileOutputStream(file);

				// int bytesWritten = 0;
				int byteCount = 0;

				byte[] bytes = new byte[1024 * 1024];

				while ((byteCount = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, byteCount);
					// bytesWritten += byteCount;
				}
				inputStream.close();
				outputStream.close();
			}
		}
		return "ok";
	}
	
	/**
     * 读取txt文件的内容
     * @param file 想要读取的文件对象
     * @return 返回文件内容
     */
    public static String txt2String(File file){
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
            	s = s.replace("\"", "\\\"");
                result.append(s).append("\\n");
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }
}
