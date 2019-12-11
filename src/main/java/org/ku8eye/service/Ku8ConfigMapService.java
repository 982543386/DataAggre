package org.ku8eye.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ku8eye.domain.Ku8ConfigMap;
import org.ku8eye.domain.User;
import org.ku8eye.mapping.Ku8ConfigMapMapper;
import org.ku8eye.service.k8s.K8sAPIService;
import org.ku8eye.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.client.KubernetesClientException;


@Service
public class Ku8ConfigMapService
{
	private static final Logger log = Logger.getLogger(Ku8ConfigMapService.class);
			
	@Autowired
	private Ku8ConfigMapMapper dao;
	
	@Autowired
	private K8sAPIService k8sService;

	/**
	 * @update zy 2016年4月11日11:26:58
	 * @param map
	 */
	public int insertConfigMap(Ku8ConfigMap ku8configmap, String json, User user)
	{
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Creating Config Map: " + ku8configmap.getName() + ", cluster: " + ku8configmap.getClusterId() + ", namespace: " + ku8configmap.getNamespace());
		
		Map<String, String> definition = new HashMap<>();
		Map<String, String> map = new HashMap<>();
		List<org.ku8eye.bean.project.ConfigMap> configMapList = JSONUtil.toObjectList(json, org.ku8eye.bean.project.ConfigMap.class);

		for (org.ku8eye.bean.project.ConfigMap beanConfigMap : configMapList)
		{
			definition.put(beanConfigMap.getName(), beanConfigMap.getFilename());
			map.put(beanConfigMap.getName(), beanConfigMap.getValue());
		}

		k8sService.createConfigMap(ku8configmap.getClusterId(), ku8configmap.getNamespace(), ku8configmap.getName(), definition, map);
		return dao.insert(ku8configmap);
	}

	public void insertConfigMapDB(Ku8ConfigMap map)
	{
		dao.insert(map);
	}
	
	public boolean updateConfigMap(String json, Integer id, User user)
	{
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Updating Config Map id: " + id);
		
		try
		{
			Ku8ConfigMap ku8cm = listByPk(id);
			Map<String, String> definition = new HashMap<>();
			Map<String, String> map = new HashMap<>();
			List<org.ku8eye.bean.project.ConfigMap> configMapList = JSONUtil.toObjectList(json, org.ku8eye.bean.project.ConfigMap.class);
	
			for (org.ku8eye.bean.project.ConfigMap beanConfigMap : configMapList)
			{
				definition.put(beanConfigMap.getName(), beanConfigMap.getFilename());
				map.put(beanConfigMap.getName(), beanConfigMap.getValue());
			}
	
			ConfigMap f8cm = k8sService.getConfigMapByName(ku8cm.getClusterId(), ku8cm.getNamespace(), ku8cm.getName());
			f8cm.setData(map);
			f8cm.getMetadata().setLabels(definition);
			k8sService.replaceConfigMap(ku8cm.getClusterId(), ku8cm.getNamespace(), ku8cm.getName(), f8cm);
			
			return true;
		}
		catch (KubernetesClientException e)
		{
			log.error("[USER: " + user.getUserId() + "] Update Config Map id: " + id + " failed, " + e.getMessage());
			return false;
		}
	}

	public List<Ku8ConfigMap> listAll()
	{
		return dao.selectAll();
	}

	public List<Ku8ConfigMap> listByClusterId(Integer id)
	{
		return dao.selectClusterId(id);
	}

	public Ku8ConfigMap listByPk(Integer id)
	{
		return dao.selectByPrimaryKey(id);
	}

	/**
	 * 返回指定tenantId下的所有configMap
	 * 
	 * @author zy
	 * @version 2016年3月29日 下午12:09:43
	 * @param tenantId
	 * @return
	 */
	public List<Ku8ConfigMap> getConfigMapsByTenantId(Integer tenantId)
	{
		return dao.selectByTenantId(tenantId);
	}

	/**
	 * 删除指定id的configMap
	 * 
	 * @author zy
	 * @version 2016年3月29日 下午1:06:10
	 * @update 2016年4月11日11:24:46
	 * @param configMapId
	 */
	public void del(Integer configMapId, User user)
	{
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Deleting Config Map id: " + configMapId);
		
		delConfigMapInKu8(configMapId, user);
		dao.deleteByPrimaryKey(configMapId);
	}

	private boolean delConfigMapInKu8(Integer configMapId, User user)
	{
		Ku8ConfigMap configMap = listByPk(configMapId);
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Deleting Config Map in Kubernetes: " + configMap.getName() + ", cluster: " + configMap.getClusterId() + ", namespace: " + configMap.getNamespace());
		
		return k8sService.deleteConfigMap(configMap.getClusterId(), configMap.getNamespace(), configMap.getName());
	}

	/**
	 * 集群纳管时，将ku8中指定集群内的configmap同步到数据库中
	 * 
	 * @author zy
	 * @version 2016年4月11日 上午10:43:29
	 */
	public void putku8ClusterConfigMapIntoDB(int clusterId)
	{
		ConfigMapList configMapList = k8sService.getConfigMaps(clusterId);
		if(configMapList == null)
		{
			log.error("Got a null list of ConfigMaps");
			return;
		}
			
		List<ConfigMap> list = configMapList.getItems();
		dao.deleteByClusterId(clusterId);
		for (ConfigMap ku8configMap : list)
		{
			Ku8ConfigMap dbConfigMap = new Ku8ConfigMap();
			dbConfigMap.setClusterId(clusterId);
			dbConfigMap.setName(ku8configMap.getMetadata().getName());
			dbConfigMap.setNamespace(ku8configMap.getMetadata().getNamespace());
			insertConfigMapDB(dbConfigMap);
		}
	}
}
