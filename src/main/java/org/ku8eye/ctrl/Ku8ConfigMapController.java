package org.ku8eye.ctrl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ku8eye.Constants;
import org.ku8eye.bean.GridData;
import org.ku8eye.domain.Ku8ConfigMap;
import org.ku8eye.domain.User;
import org.ku8eye.service.Ku8ConfigMapService;
import org.ku8eye.service.k8s.K8sAPIService;
import org.ku8eye.util.AjaxReponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClientException;

/**
 * @author Federico Gu 
 * Email: federico.gu@hpe.com
 * Date: 2016-05 
 * Description: Controller for Ku8 Config Maps
 */
@RestController
@SessionAttributes(org.ku8eye.Constants.USER_SESSION_KEY)
public class Ku8ConfigMapController
{
	
	private static final Logger log = Logger.getLogger(Ku8ConfigMapController.class);
	
	@Autowired
	private Ku8ConfigMapService configMapservice;
	
	@Autowired
	private K8sAPIService _k8APIService;

	/**
	 * 根据角色获取config-map 列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/configmaps")
	public GridData getConfigMaps(ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Getting all Config Maps");
		
		GridData grid = new GridData();
		if (Constants.USERTYPE_ADMIN.equals(user.getUserType()))
			grid.setData(configMapservice.listAll());
		else
			grid.setData(configMapservice.getConfigMapsByTenantId(user.getTenantId()));
		return grid;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/configmaps/distinct")
	public GridData getDistinctConfigMaps(ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Getting all Config Maps for deployment");
		
		List<Ku8ConfigMap> ku8configMap;
		Set<String> s = new LinkedHashSet<>();
		
		if (Constants.USERTYPE_ADMIN.equals(user.getUserType()))
		{
			ku8configMap = configMapservice.listAll();
		}
		else
		{
			ku8configMap =  configMapservice.getConfigMapsByTenantId(user.getTenantId());
		}
		
		ku8configMap.forEach(item->s.add(item.getName()));
		GridData grid = new GridData(s);
		return grid;
	}
	/**
	 * 
	 * @author zy
	 * @version 2016年3月29日  下午1:16:08
	 * @param name
	 * @param json
	 * @param clusterId
	 * @return
	 */
	@RequestMapping(value = "/configmaps/create", method = RequestMethod.POST)
	public AjaxReponse addConfigMap(@RequestParam("name") String name, @RequestParam("json") String json, @RequestParam("namespace") String namespace, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Adding Config Map name:" + name);
		
		try
		{
			Ku8ConfigMap config = new Ku8ConfigMap();
			config.setName(name);
			config.setClusterId(0);
			config.setNamespace(namespace);
			configMapservice.insertConfigMap(config, json, user);
			
			if(log.isInfoEnabled())
				log.info("[USER: " + user.getUserId() + "] Config Map " + name + " added");
			return new AjaxReponse(1, "ADDED");
		}
		catch (KubernetesClientException e)
		{
			log.error("[USER: " + user.getUserId() + "] Config Map " + name + " failed to add, " + e.getMessage());
			return new AjaxReponse(0, "ADD FAILED");
		}
	}
	
	@RequestMapping(value = "/configmaps/get", method = RequestMethod.GET)
	public GridData getConfigMap(int ku8ID, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Getting Config Map id:" + ku8ID);
		
		try
		{
			Ku8ConfigMap ku8cm = configMapservice.listByPk(ku8ID);
			ConfigMap f8cm = _k8APIService.getConfigMapByName(ku8cm.getClusterId(), ku8cm.getNamespace(), ku8cm.getName());
			
			if(f8cm == null)
			{
				log.error("[USER: " + user.getUserId() + "] ConfigMap " + ku8cm.getName() + " not found on cluster: " + ku8cm.getClusterId() + ", namespace: " + ku8cm.getNamespace());
				return null;
			}
			
			Map<String, String> definition = f8cm.getMetadata().getLabels();
			List<org.ku8eye.bean.project.ConfigMap> beanConfigMapList = new ArrayList<>();
			
			f8cm.getData().forEach((k, v) -> beanConfigMapList.add(new org.ku8eye.bean.project.ConfigMap(k, v, definition.get(k))));
			
			GridData grid = new GridData();
			grid.setData(beanConfigMapList);
			return grid;
		}
		catch (KubernetesClientException e)
		{
			log.error("[USER: " + user.getUserId() + "] Kubernetes error on getConfigMap, " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * 
	 * @author zy
	 * @version 2016年3月29日  下午1:16:03
	 * @param id
	 * @param json_map
	 * @return
	 */
	@RequestMapping(value = "/configmaps/update", method = RequestMethod.POST)
	public AjaxReponse updateConfigMap(@RequestParam("id") Integer id, @RequestParam("json") String json_map, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Updating Config Map id:" + id);
		
		try
		{
			configMapservice.updateConfigMap(json_map, id, user);
			log.info("[USER: " + user.getUserId() + "] Config Map id: " + id + " updated");
			return new AjaxReponse(1, "SUCCESS");
		}
		catch (Exception e)
		{	
			log.error("[USER: " + user.getUserId() + "] Failed to update Config Map id: " + id + ", " + e.getMessage());
			return new AjaxReponse(0, "Failed to update Config Map");
		}
	}

	/**
	 * 
	 * @author zy
	 * @version 2016年3月29日  下午1:15:55
	 * @param configMapId
	 * @return
	 */
	@RequestMapping(value = "/configmaps/del/{configMapId}")
	public AjaxReponse delConfigMap(@PathVariable("configMapId") Integer configMapId, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		try
		{
			configMapservice.del(configMapId, user);
			log.info("[USER: " + user.getUserId() + "] Config Map id: " + configMapId + " deleted");
			return new AjaxReponse(1, "SUCCESS");
		}
		catch (Exception e)
		{
			log.error("[USER: " + user.getUserId() + "] Failed to delete Config Map id: " + configMapId + ", " + e.getMessage());
			return new AjaxReponse(0, "Failed to delete Config Map");
		}
	} 
}
