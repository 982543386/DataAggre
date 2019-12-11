/**
 * 
 */
package com.iiot.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.ku8eye.Constants;
import org.ku8eye.bean.GridData;
import org.ku8eye.bean.project.Project;
import org.ku8eye.bean.project.Service;
import org.ku8eye.ctrl.ApplicationController;
import org.ku8eye.domain.Ku8Project;
import org.ku8eye.domain.User;
import org.ku8eye.util.AjaxReponse;
import org.ku8eye.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iiot.dao.IiotProjectMapper;
import com.iiot.dao.IiotAppListMapper;
import com.iiot.domain.IiotProject;
import com.iiot.edgenode.entity.EdgeNode;
import com.iiot.edgenode.service.EdgeNodeMonitorService;
import com.iiot.domain.IiotAppList;
import com.iiot.service.ProjectService;
import com.iiot.service.ProjectToK8Service;
import com.iiot.service.k8s.ProjectToK8APIService;
import com.iiot.service.k8s.K8APIService;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
/**
 * @author lenovo
 *
 */
@RestController
@RequestMapping("/iiot/project")
public class ProjectController {
	private static final Logger log = Logger.getLogger(ProjectController.class);
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private IiotAppListMapper iiotAppListMapper;
	
	@Autowired
	private ProjectToK8Service projectToK8Service;
	
	@Autowired
	private K8APIService k8APIService;
	
	@Autowired
	private ProjectToK8APIService _F8toK8APIService;
	
	@Autowired
	private EdgeNodeMonitorService edgeNodeMonitorService;
	
	
	
	@Value("${iiot.kubernetes.masterURL}")
	private String masterURL;
	
	@Value("${iiot.kubernetes.token}")
	private String OauthToken;
	
	//创建应用模板
	@RequestMapping(value = "/application/add")
	public AjaxReponse addApplication(HttpServletRequest request )
	{
		//验证登陆用户
		User user = null;
		Object obj = request.getSession().getAttribute(
				org.ku8eye.Constants.USER_SESSION_KEY);
		if (obj != null) {
			user = (User) obj;
			log.info("get login user:" + user.getUserId());
		}else {
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}
		//log.info("user detail is "+user.toString());
		//log.info("Alias"+user.getAlias()+"Note"+user.getNote()+"usertype"+user.getUserType()+"status"+user.getStatus());
		//解析表单
		String name =request.getParameter("name");
		String version =request.getParameter("version");
		String note =request.getParameter("note");
		String jsonStr =request.getParameter("jsonStr");
		String projectId = k8APIService.getIiotUUID();
		if(log.isInfoEnabled())
			log.info("Adding app name:" + request.getParameter("name"));
		if (name.isEmpty() || version.isEmpty() || jsonStr.isEmpty())
		{
			log.error("EMPTY FIELDS");
			return new AjaxReponse(-1, "EMPTY FIELDS");
		}

		byte status=0;//默认未启动状态码
		int res = projectService.addApplication(user.getTenantId(), user.getUserId(), name, version, note, jsonStr,status,projectId);

		if (res == -1)
		{
			log.error("App " + name + " failed to add, SQL returned :" + res);
			return new AjaxReponse(res, "FAILED");
		}
		else
		{
			if(log.isInfoEnabled())
				log.info("App " + name + " added");
			return new AjaxReponse(res, "SUCCESS");
		}
	}
	
	//创建应用模板
		@RequestMapping(value = "/application/delete")
		public AjaxReponse deleteApplication(HttpServletRequest request )
		{
			//验证登陆用户
			User user = null;
			Object obj = request.getSession().getAttribute(
					org.ku8eye.Constants.USER_SESSION_KEY);
			if (obj != null) {
				user = (User) obj;
				log.info("get login user:" + user.getUserId());
			}else {
				log.error("ERROR USER NOT LOGGED IN");
				return new AjaxReponse(-1, "USER NOT LOGGED");
			}
			//解析表单
			String templateID =request.getParameter("templateID");
			log.info("deleting template ID]"+templateID);
			//String jsonStr =request.getParameter("jsonStr");
			//String projectId = k8APIService.getIiotUUID();
			/*if(log.isInfoEnabled())
				log.info("Adding app name:" + request.getParameter("name"));
			if (name.isEmpty() || version.isEmpty() || jsonStr.isEmpty())
			{
				log.error("EMPTY FIELDS");
				return new AjaxReponse(-1, "EMPTY FIELDS");
			}

			byte status=0;//默认未启动状态码
			
*/
			int res = projectService.deleteApplication(templateID);
			if (res == -1)
			{
				log.error("App " + templateID + " failed to add, SQL returned :" + res);
				return new AjaxReponse(res, "FAILED");
			}
			else
			{
				if(log.isInfoEnabled())
					log.info("App " + templateID + " added");
				return new AjaxReponse(res, "SUCCESS");
			}
		}	
	
	

	
	
	
	



	@RequestMapping(value = "/application/list")
	public GridData getApplications(HttpServletRequest request)
	{
		User user = null;
		Object obj = request.getSession().getAttribute(
				org.ku8eye.Constants.USER_SESSION_KEY);
		if (obj != null) {
			user = (User) obj;
			log.info("get login user:" + user.getUserId());
		}

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		List<IiotProject> apps = null;
		
		if (user.getUserType().equals(Constants.USERTYPE_ADMIN))
		{
			 apps = projectService.getApplications();
		}
		/*else
		{
			 apps = projectService.getApplications(userService.getOwnerStrByUser(user));
		}*/
		
		GridData grid = new GridData();
		grid.setData(apps);
		return grid;
	}
	
	//获取模板名（name）和project uuid(projectId)
	@RequestMapping(value = "/names")
	public GridData getNames(HttpServletRequest request)
	{
		User user = null;
		Object obj = request.getSession().getAttribute(
				org.ku8eye.Constants.USER_SESSION_KEY);
		if (obj != null) {
			user = (User) obj;
			log.info("get login user:" + user.getUserId());
		}
		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		List<IiotProject> iiotProjects = projectService.getNamesAndProjectId(user.getUserId());
		
		/*if (user.getUserType().equals(Constants.USERTYPE_ADMIN))
		{
			 names = projectService.getNamesAndProjectId(user.getUserId());
		}*/
		/*else
		{
			 apps = projectService.getApplications(userService.getOwnerStrByUser(user));
		}*/
		
		GridData grid = new GridData();
		grid.setData(iiotProjects);
		return grid;
	}
	
	//将模板部署到指定节点
	@RequestMapping(value = "/addApp")
	public AjaxReponse addApp(HttpServletRequest request)
	{
		User user = null;
		Object obj = request.getSession().getAttribute(
				org.ku8eye.Constants.USER_SESSION_KEY);
		if (obj != null) {
			user = (User) obj;
			log.info("get login user:" + user.getUserId());
		}

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		String name =request.getParameter("name");
		String node =request.getParameter("node");
		String template =request.getParameter("template");
		System.out.println("request body name:"+name+node+template);
		
		IiotAppList list = new IiotAppList();
		list.setAppName(name);
		list.setNode(node);
		list.setTemplate(template);
		list.setOwner(user.getUserId());
		list.setStatus((byte) 0);
		int res = iiotAppListMapper.insert(list);
		
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
		/*else
		{
			 apps = projectService.getApplications(userService.getOwnerStrByUser(user));
		}*/
		
		//grid.setData(names);
	}
	
	//将模板部署到指定节点
	@RequestMapping(value = "/deployAppToNode")
	public AjaxReponse deployAppToNode(HttpServletRequest request)
	{
		User user = null;
		Object obj = request.getSession().getAttribute(
				org.ku8eye.Constants.USER_SESSION_KEY);
		if (obj != null) {
			user = (User) obj;
			log.info("get login user:" + user.getUserId());
		}

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		String name =request.getParameter("name");
		String node =request.getParameter("node");
		String template =request.getParameter("template");
		String note = request.getParameter("modalDeployNote");
		System.out.println("request body name:"+name+node+template);
		
		IiotAppList iiotAppList = new IiotAppList();
		iiotAppList.setAppName(name);
		iiotAppList.setNode(node);
		iiotAppList.setTemplate(template);
		iiotAppList.setOwner(user.getUserId());
		iiotAppList.setStatus((byte) 0);
		iiotAppList.setNote(note);
		//int res = iiotAppListMapper.insert(iiotAppList);
		iiotAppListMapper.insertAndGetId(iiotAppList);
		System.out.println("插入的resid为"+iiotAppList.getAppId());
		Integer returnListId = iiotAppList.getAppId();
		
		//获取 deplopyment app id
		/*Integer appId = Integer.parseInt(request.getParameter("appId"));
		log.info("deployment appID"+appId);
		IiotAppList iiotAppList = projectService.getIiotApp(appId);*/
		//获取模板ID
		
		String templateID = iiotAppList.getTemplate();
		//获取 模板信息
		IiotProject ku8p =  projectService.getApplicationByProjectId(templateID);
		//20190802  id=2
		//Integer ku8ID=2;
		//IiotProject ku8p = projectService.getApplication(ku8ID);
		//使用default namespace
		List<String> namespaceList = Arrays.asList("default");
		//IiotAppList含有模板和节点的对应信息
		
		String nodeName = iiotAppList.getNode();
		//判断节点是否在当前集群中且可用
		List<EdgeNode> nodeList = edgeNodeMonitorService.getNodesList();
		boolean flag = false;
		for(EdgeNode edgeNode : nodeList){ 
			if(edgeNode.getIp().equals(nodeName)){
				flag =true;
			}
		 } 
		if(!flag) {
			log.error("NODE NOT READY");
			return new AjaxReponse(-1, "NODE NOT READY");
		}
		//log.info("the flag is ]"+flag+"[");
		//List<String> namespaceList = JSONUtil.toObjectList(namespaces, String.class);
		if(namespaceList == null)
		{
			log.error("APP DATA CORRUPTED");
			return new AjaxReponse(-1, "APP DATA CORRUPTED");
		}

		/*if(ku8p == null)
		{
			log.error("APP NOT FOUND, id" + appId);
			return new AjaxReponse(-1, "APP NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8p.getOwner().equals(user.getUserId()))
		{
			log.error("Application id " + appId + " does not belong to user: " + user.getUserId());
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}*/
		else
		{
			// Start deploy, set status to 1, deploying
			ku8p.setProgress(0);
			ku8p.setStatus(Constants.KU8_APP_DEPLOYING_STATUS);
			projectService.updateApplication(ku8p);
			iiotAppList.setStatus(Constants.KU8_APP_DEPLOYING_STATUS);
			projectService.updateIiotAppList(iiotAppList);
			
			//模板ku8p
			//节点 nodeName
			return deployApplicationDeployment(iiotAppList, ku8p, namespaceList, user,nodeName, returnListId);
		}
	}
	
	//获取应用订单
	
	//@SuppressWarnings("null")
	@RequestMapping(value = "/app/list")
	public GridData getAppList(HttpServletRequest request)
	{
		User user = null;
		Object obj = request.getSession().getAttribute(
				org.ku8eye.Constants.USER_SESSION_KEY);
		if (obj != null) {
			user = (User) obj;
			log.info("get login user:" + user.getUserId());
		}

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		String name =request.getParameter("name");
		
		List<IiotAppList> res = iiotAppListMapper.selectByOwner(user.getUserId());
		
		
		//System.out.println("request body name:"+name+node+template);
		
		GridData grid = new GridData();
		grid.setData(res);
		return grid;
		
		
/*		if (res ==  null)
		{
			log.error("App " + name + " failed to add, SQL returned " + res);
			return new AjaxReponse(-1, "ADD FAILED");
		}
		else
		{
			if(log.isInfoEnabled())
				log.info("App " + name + " added");
			return new AjaxReponse(1, "ADDED");
		}*/
		/*else
		{
			 apps = projectService.getApplications(userService.getOwnerStrByUser(user));
		}*/
		
		//grid.setData(names);
	}
	
	@RequestMapping(value = "/app/getApplication")
	public IiotProject getApplication(int appID, HttpServletRequest request)
	{
		User user = null;
		Object obj = request.getSession().getAttribute(
				org.ku8eye.Constants.USER_SESSION_KEY);
		if (obj != null) {
			user = (User) obj;
			log.info("get login user:" + user.getUserId());
		}

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		
		IiotProject ku8p = projectService.getApplication(appID);
		
		if(ku8p == null)
		{
			log.error("APP NOT FOUND, id" + appID);
			return null;
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8p.getOwner().equals(user.getUserId()))
		{
			log.error("Application id " + appID + " does not belong to user: " + user.getUserId());
			return null;
		}
		else
		{
			return ku8p;
		}
	}
	
	
	@RequestMapping(value = "/app/getServices")
	public GridData getApplicationServices(int appID, HttpServletRequest request)
	{
		User user = null;
		Object obj = request.getSession().getAttribute(
				org.ku8eye.Constants.USER_SESSION_KEY);
		if (obj != null) {
			user = (User) obj;
			log.info("get login user:" + user.getUserId());
		}

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		

		IiotProject ku8p = projectService.getApplication(appID);

		if(ku8p == null)
		{
			log.error("APP NOT FOUND, id" + appID);
			return null;
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8p.getOwner().equals(user.getUserId()))
		{
			log.error("Application id " + appID + " does not belong to user: " + user.getUserId());
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
	
	@RequestMapping(value = "/application/deployApplication")
	public AjaxReponse deployApplication(Integer ku8ID, String namespaces, ModelMap model,HttpServletRequest request)
	{	
		User user = null;
		Object obj = request.getSession().getAttribute(
				org.ku8eye.Constants.USER_SESSION_KEY);
		if (obj != null) {
			user = (User) obj;
			log.info("get login user:" + user.getUserId());
		}
		
		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}
		//20190802  id=2
		ku8ID=2;
		IiotProject ku8p = projectService.getApplication(ku8ID);
		List<String> namespaceList = Arrays.asList("default");
		//List<String> namespaceList = JSONUtil.toObjectList(namespaces, String.class);
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
			projectService.updateApplication(ku8p);
			
			return deployApplication(ku8p, namespaceList, user);
		}
	}
	
	//部署應用到指定節點
	@RequestMapping(value = "/application/deployDeployment")
	public AjaxReponse deployDeployment(HttpServletRequest request)
	{	
		//Integer ku8ID, String namespaces, ModelMap model,
		User user = null;
		Object obj = request.getSession().getAttribute(
				org.ku8eye.Constants.USER_SESSION_KEY);
		if (obj != null) {
			user = (User) obj;
			log.info("get login user:" + user.getUserId());
		}
		
		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}
		//获取 deplopyment app id
		Integer appId = Integer.parseInt(request.getParameter("appId"));
		Integer returnListId = appId;
		log.info("deployment appID"+appId);
		IiotAppList iiotAppList = projectService.getIiotApp(appId);
		//获取模板ID
		
		String templateID = iiotAppList.getTemplate();
		//获取 模板信息
		IiotProject ku8p =  projectService.getApplicationByProjectId(templateID);
		//20190802  id=2
		//Integer ku8ID=2;
		//IiotProject ku8p = projectService.getApplication(ku8ID);
		//使用default namespace
		List<String> namespaceList = Arrays.asList("default");
		//IiotAppList含有模板和节点的对应信息
		
		String nodeName = iiotAppList.getNode();
		//判断节点是否在当前集群中且可用
		List<EdgeNode> nodeList = edgeNodeMonitorService.getNodesList();
		boolean flag = false;
		for(EdgeNode edgeNode : nodeList){ 
			if(edgeNode.getIp().equals(nodeName)){
				flag =true;
			}
		 } 
		if(!flag) {
			log.error("NODE NOT READY");
			return new AjaxReponse(-1, "NODE NOT READY");
		}
		//log.info("the flag is ]"+flag+"[");
		//List<String> namespaceList = JSONUtil.toObjectList(namespaces, String.class);
		if(namespaceList == null)
		{
			log.error("APP DATA CORRUPTED");
			return new AjaxReponse(-1, "APP DATA CORRUPTED");
		}

		if(ku8p == null)
		{
			log.error("APP NOT FOUND, id" + appId);
			return new AjaxReponse(-1, "APP NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8p.getOwner().equals(user.getUserId()))
		{
			log.error("Application id " + appId + " does not belong to user: " + user.getUserId());
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}
		else
		{
			// Start deploy, set status to 1, deploying
			ku8p.setProgress(0);
			ku8p.setStatus(Constants.KU8_APP_DEPLOYING_STATUS);
			projectService.updateApplication(ku8p);
			iiotAppList.setStatus(Constants.KU8_APP_DEPLOYING_STATUS);
			projectService.updateIiotAppList(iiotAppList);
			
			//模板ku8p
			//节点 nodeName
			return deployApplicationDeployment(iiotAppList, ku8p, namespaceList, user,nodeName, returnListId);
		}
	}
	//returnListId:插入iiotAppList的自增ID
	private AjaxReponse deployApplicationDeployment(IiotAppList iiotAppList, IiotProject ku8p, List<String> namespaces, User user, String nodeName, Integer returnListId)
	{
		if(log.isInfoEnabled())
		{
			log.info("Deploying app id:" + ku8p.getId() + ", with namespace: " + namespaces);
			log.info("Setting app to deploying, status:" + Constants.KU8_APP_DEPLOYING_STATUS);
		}
		
		// Validation done
		// Get JSON
		String json = ku8p.getJsonSpec();
		log.info("Getting json str: " + json);
		if(log.isDebugEnabled())
			log.debug("Getting json str: " + json);

		// Parse JSON from the application
		Project p = Project.getFromJSON(json);
		
		if(p == null)
		{
			ku8p.setStatus(Constants.KU8_APP_FAILED_STATUS);
			projectService.updateApplication(ku8p);
			iiotAppList.setStatus(Constants.KU8_APP_DEPLOYING_STATUS);
			projectService.updateIiotAppList(iiotAppList);
			
			log.error("APP DATA CORRUPTED, id" + ku8p.getId());
			return new AjaxReponse(-1, "APP DATA CORRUPTED");
		}
		
		if(log.isInfoEnabled())
			log.info("Project:" + p.getProjectName() + " found");

		boolean failed = false;
		String failedDetails = "";
		String namespace = null;
		int progress = 100 / (namespaces.size() * p.getServices().size());
		
		//Created Deployment/Service temp
		List<String> created = new ArrayList<String>(p.getServices().size());
		
		//Loop namespaces 20190808 only support ns 'default'
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
					
					String uuid32 = UUID.randomUUID().toString().trim().replaceAll("-", "");
					String serviceNameUUID = s.getName()+uuid32;
					s.setName(serviceNameUUID);
					iiotAppList.setAppName(serviceNameUUID);
					int resUpdate = projectService.updateIiotAppList(iiotAppList);
					System.out.println("更新serviceNameUUID"+serviceNameUUID+"结果是"+resUpdate);
					//build Service   @param false, p.getProjectName(), s, user
					
					projectToK8Service.buildService(ku8p.getClusterId(), namespace, false, p.getProjectName(), s, user);
					isSrvCreated = true;
					
					if(log.isInfoEnabled())
						log.info("Creating Deployment:" + s.getName());
					
					//projectToK8Service.buildReplicationController(ku8p.getClusterId(), namespace, false, p.getProjectName(), s, user);
					//ku8p.getClusterId()
					log.info("building Deployment  for Project Name : "+p.getProjectName());
					//build Deployment @param nodeName, s, p.getProjectName(), user
					projectToK8Service.buildDeployment(nodeName, namespace, false, p.getProjectName(), s, user);
					//Add to temp list
					created.add(s.getName());
					
					//Add progress
					int oldprogress = 0;
					if(ku8p.getProgress() != null)
						oldprogress = ku8p.getProgress();
					ku8p.setProgress(oldprogress + progress);
					//TODO  显示进度
					projectService.updateApplication(ku8p);
					
				}
				catch (KubernetesClientException e)
				{
					//Delete f8serv only if Deployment fails to deploy
					if(isSrvCreated)
					{
						if(log.isInfoEnabled())
							log.info("RC failed to create, Deleting Service: " + s.getName());
						k8APIService.deleteService(ku8p.getClusterId(), namespace, s.getName());
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
				k8APIService.deleteService(ku8p.getClusterId(), namespace, name);
				
				if(log.isInfoEnabled())
					log.info("Deleting RC: " + name);
				//TODO  出现问题时删除Deployment
				k8APIService.deleteReplicationController(ku8p.getClusterId(), namespace, name);
			}
			
			log.error("App: " + ku8p.getName() +" failed to deploy, " + failedDetails);
			
			// Failed, set status to FAILED
			ku8p.setStatus(Constants.KU8_APP_FAILED_STATUS);
			projectService.updateApplication(ku8p);
			iiotAppList.setStatus(Constants.KU8_APP_FAILED_STATUS);
			projectService.updateIiotAppList(iiotAppList);
			return new AjaxReponse(-1, "APP <strong>" + ku8p.getName() + "</strong> FAILED TO DEPLOY<br/>"+ failedDetails);
		}
		else
		{
			if(log.isInfoEnabled())
				log.info("App: " + ku8p.getName() +" successfully deployed.");
			
			// Success, set status to 2
			ku8p.setResPart(namespace);
			ku8p.setStatus(Constants.KU8_APP_DEPLOYED_STATUS);
			projectService.updateApplication(ku8p);
			iiotAppList.setStatus(Constants.KU8_APP_DEPLOYED_STATUS);
			projectService.updateIiotAppList(iiotAppList);
			return new AjaxReponse(1, "APP <strong>" + ku8p.getName() + "</strong> SUCCESSFULLY DEPLOYED");
		}
	}
	
	private AjaxReponse deployApplication(IiotProject ku8p, List<String> namespaces, User user)
	{
		if(log.isInfoEnabled())
		{
			log.info("Deploying app id:" + ku8p.getId() + ", with namespace: " + namespaces);
			log.info("Setting app to deploying, status:" + Constants.KU8_APP_DEPLOYING_STATUS);
		}
		
		// Validation done
		// Get JSON
		String json = ku8p.getJsonSpec();
		log.info("Getting json str: " + json);
		if(log.isDebugEnabled())
			log.debug("Getting json str: " + json);

		// Parse JSON from the application
		Project p = Project.getFromJSON(json);
		
		if(p == null)
		{
			ku8p.setStatus(Constants.KU8_APP_FAILED_STATUS);
			projectService.updateApplication(ku8p);
			
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
					
					projectToK8Service.buildService(ku8p.getClusterId(), namespace, false, p.getProjectName(), s, user);
					isSrvCreated = true;
					
					if(log.isInfoEnabled())
						log.info("Creating RC:" + s.getName());
					
					projectToK8Service.buildReplicationController(ku8p.getClusterId(), namespace, false, p.getProjectName(), s, user);
					
					//Add to temp list
					created.add(s.getName());
					
					//Add progress
					int oldprogress = 0;
					if(ku8p.getProgress() != null)
						oldprogress = ku8p.getProgress();
					ku8p.setProgress(oldprogress + progress);
					projectService.updateApplication(ku8p);
				}
				catch (KubernetesClientException e)
				{
					//Delete f8serv only if RC fails
					if(isSrvCreated)
					{
						if(log.isInfoEnabled())
							log.info("RC failed to create, Deleting Service: " + s.getName());
						k8APIService.deleteService(ku8p.getClusterId(), namespace, s.getName());
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
				k8APIService.deleteService(ku8p.getClusterId(), namespace, name);
				
				if(log.isInfoEnabled())
					log.info("Deleting RC: " + name);
				k8APIService.deleteReplicationController(ku8p.getClusterId(), namespace, name);
			}
			
			log.error("App: " + ku8p.getName() +" failed to deploy, " + failedDetails);
			
			// Failed, set status to FAILED
			ku8p.setStatus(Constants.KU8_APP_FAILED_STATUS);
			projectService.updateApplication(ku8p);
			return new AjaxReponse(-1, "APP <strong>" + ku8p.getName() + "</strong> FAILED TO DEPLOY<br/>"+ failedDetails);
		}
		else
		{
			if(log.isInfoEnabled())
				log.info("App: " + ku8p.getName() +" successfully deployed.");
			
			// Success, set status to 2
			ku8p.setResPart(namespace);
			ku8p.setStatus(Constants.KU8_APP_DEPLOYED_STATUS);
			projectService.updateApplication(ku8p);
			return new AjaxReponse(1, "APP <strong>" + ku8p.getName() + "</strong> SUCCESSFULLY DEPLOYED");
		}
	}


	

	/*private AjaxReponse deployApplicationDeployment(IiotProject ku8p, List<String> namespaces, User user)
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
	*/
		@RequestMapping(value = "/application/deploy")
	//public AjaxReponse addApplication(HttpServletRequest request,@RequestParam String name)
	public AjaxReponse deployApplication(HttpServletRequest request )
	{
		String appId =request.getParameter("id");
		log.info("Reading appId"+appId);
		KubernetesClient client = k8APIService.getClient(1);
		
		/*String filePath = this.getClass().getClassLoader().getResource("cert/98ca.crt").getPath();
		Config config = new ConfigBuilder().withCaCertFile(filePath).withMasterUrl(masterURL).withOauthToken(OauthToken).build();
	    KubernetesClient client = new DefaultKubernetesClient(config);*/
		//clusterId, namespace, isPublic, appName, s, user
		//验证登陆用户
		User user = null;
		Object obj = request.getSession().getAttribute(
				org.ku8eye.Constants.USER_SESSION_KEY);
		if (obj != null) {
			user = (User) obj;
			log.info("get login user:" + user.getUserId());
		}else {
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}
		/*String appName= "";
		Service s = 
		projectToK8Service.buildDeployment(1, "default", true, "newapp321", s, user)
		
		
		try {
	        // Create a namespace for all our stuff
	        //Namespace ns = new NamespaceBuilder().withNewMetadata().withName("thisisatest").addToLabels("this", "rocks").endMetadata().build();
	        //log("Created namespace", client.namespaces().createOrReplace(ns));

	        //ServiceAccount fabric8 = new ServiceAccountBuilder().withNewMetadata().withName("fabric8").endMetadata().build();

	        //client.serviceAccounts().inNamespace("thisisatest").createOrReplace(fabric8);
	        for (int i = 0; i < 2; i++) {
	          System.err.println("Iteration:" + (i+1));
	          Deployment deployment = new DeploymentBuilder()
	            .withNewMetadata()
	            .withName("nginx")
	            .endMetadata()
	            .withNewSpec()
	            .withReplicas(1)
	            .withNewTemplate()
	            .withNewMetadata()
	            .addToLabels("app", "nginx")
	            .endMetadata()
	            .withNewSpec()
	            .addNewContainer()
	            .withName("nginx")
	            .withImage("nginx")
	            .addNewPort()
	            .withContainerPort(80)
	            .endPort()
	            .endContainer()
	            .endSpec()
	            .endTemplate()
	            .withNewSelector()
	            .addToMatchLabels("app", "nginx")
	            .endSelector()
	            .endSpec()
	            .build();


	          deployment = client.apps().deployments().inNamespace("thisisatest").create(deployment);
	          log("Created deployment", deployment);

	          System.err.println("Scaling up:" + deployment.getMetadata().getName());
	          client.apps().deployments().inNamespace("thisisatest").withName("nginx").scale(2, true);
	          log("Created replica sets:", client.apps().replicaSets().inNamespace("thisisatest").list().getItems());
	          System.err.println("Deleting:" + deployment.getMetadata().getName());
	          client.resource(deployment).delete();
	        }
	        log("Done.");

	      }finally {
	        client.namespaces().withName("thisisatest").delete();
	        client.close();
	      }
	    }*/
		return null;
		    
		    
		    
		    
/*		
		System.out.println("request body:"+request.toString());
		if(log.isInfoEnabled())
			log.info("Adding app name:" + request.getParameter("name"));
		
		//User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);
		//String name, String version, String note, String jsonStr, ModelMap model
		User user = null;
		Object obj = request.getSession().getAttribute(
				org.ku8eye.Constants.USER_SESSION_KEY);
		if (obj != null) {
			user = (User) obj;
			log.info("get login user:" + user.getUserId());
		}
		log.info("user detail is "+user.toString());
		log.info("Alias"+user.getAlias()+"Note"+user.getNote()+"usertype"+user.getUserType()+"status"+user.getStatus());
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

		System.out.println("request tenant ID:"+user.getTenantId());
		System.out.println("request getUserId:"+user.getUserId());
		System.out.println("request name:"+name);
		System.out.println("request version:"+version);
		System.out.println("request json:"+jsonStr);
		byte status=0;
		int res = projectService.addApplication(user.getTenantId(), user.getUserId(), name, version, note, jsonStr,status);

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
		}*/
	}
}
