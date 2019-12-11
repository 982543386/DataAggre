package org.ku8eye.service;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.batch.Job;
//import io.fabric8.kubernetes.api.model.extensions.Job;
import io.fabric8.kubernetes.client.KubernetesClientException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ku8eye.bean.respartion.NodeView;
import org.ku8eye.bean.respartion.ResPartion;
import org.ku8eye.domain.Ku8ResPartion;
import org.ku8eye.mapping.Ku8ResPartionMapper;
import org.ku8eye.service.k8s.K8sAPIService;
import org.ku8eye.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Ku8ResPartionService
{
	static Logger log = Logger.getLogger(Ku8ResPartionService.class);
	public static final String NODE_GROUP_NAME_PREFIX = "group_";

	@Autowired
	private Ku8ResPartionMapper resPartionDao;

	@Autowired
	private K8sAPIService k8sAPIService;

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Ku8ResPartion> getAllResPartions(int clusterId)
	{
		return resPartionDao.selectAll();
	}
	/**
	 * 得到每个Node组的 资源使用情况
	 * 
	 * @return
	 */
	public List<ResPartion> getAllResPartions()
	{
		Map<String, ResPartion> map = new HashMap<String, ResPartion>();
		List<Node> nodes = k8sAPIService.getAllNodes(0);// TODO clusterId 待补充来源 这个Service里边的都需要
		for (Node node : nodes)
		{
			Map<String, String> labels = node.getMetadata().getLabels();
			List<String> groupNames = new ArrayList<String>();
			for (String obj : labels.keySet())
			{
				if (obj.startsWith(Ku8ResPartionService.NODE_GROUP_NAME_PREFIX))
				{
					groupNames.add(obj.substring(Ku8ResPartionService.NODE_GROUP_NAME_PREFIX.length()));
				}
			}
			for (String name : groupNames)
			{
				int cpuLimit = StringUtil.getStringNum(node.getStatus().getCapacity().get("cpu").getAmount());
				String memoryLimitStr = node.getStatus().getCapacity().get("memory").getAmount();
				int memoryLimit = StringUtil.getStringNum(memoryLimitStr);
				int podLimit = StringUtil.getStringNum(node.getStatus().getCapacity().get("pods").getAmount());
				if (map.containsKey(name))
				{
					ResPartion limit = map.get(name);
					ResPartion res = new ResPartion();
					res.setCpuLimit(limit.getCpuLimit() + cpuLimit);
					res.setMemoryLimit(limit.getMemoryLimit() + memoryLimit);
					res.setPodLimit(limit.getPodLimit() + podLimit);
					res.setNodeAccount(limit.getNodeAccount() + 1);
					map.put(name, res);
				}
				else
				{
					ResPartion res = new ResPartion();
					res.setCpuLimit(cpuLimit);
					res.setMemoryLimit(memoryLimit);
					res.setPodLimit(podLimit);
					res.setNodeAccount(1);
					map.put(name, res);
				}
			}
		}
		List<ResPartion> result = new ArrayList<ResPartion>();
		for (String groupName : map.keySet())
		{
			ResPartion limit = map.get(groupName);
			ResPartion res = new ResPartion();
			res.setGroupName(groupName);
			res.setCpuLimit(limit.getCpuLimit());
			res.setMemoryLimit(limit.getMemoryLimit());
			res.setPodLimit(limit.getPodLimit());
			res.setNodeAccount(limit.getNodeAccount());
			result.add(res);
		}
		return result;
	}
	/**
	 * 得到所有的Node
	 * 
	 * @return
	 */
	public List<NodeView> getAllNodes()
	{
		List<NodeView> nodes = new ArrayList<NodeView>();
		List<Node> kuberNodes = k8sAPIService.getAllNodes(0);
		for (Node kuberNode : kuberNodes)
		{
			String name = kuberNode.getMetadata().getName();
			int cpuLimit = StringUtil.getStringNum(kuberNode.getStatus().getCapacity().get("cpu").getAmount());
			log.info(name+"==>");
			log.info(kuberNode.getStatus().getCapacity().get("memory").getAmount());
			String memoryLimitStr = kuberNode.getStatus().getCapacity().get("memory").getAmount();
			int memoryLimit = StringUtil.getStringNum(memoryLimitStr);
			int podLimit = StringUtil.getStringNum(kuberNode.getStatus().getCapacity().get("pods").getAmount());
			NodeView node = new NodeView();
			node.setName(name);
			node.setCpuLimit(cpuLimit);
			node.setMemoryLimit(memoryLimit);
			node.setPodLimit(podLimit);
			nodes.add(node);
		}
		return nodes;
	}
	/**
	 * 得到Node组名是指定值的所有Node
	 * 
	 * @param groupName
	 * @return
	 */
	private List<NodeView> getNodesByGroupName(String groupName)
	{
		List<NodeView> nodes = new ArrayList<NodeView>();
		List<Node> kuberNodes = k8sAPIService.getAllNodes(0);
		for (Node kuberNode : kuberNodes)
		{
			Map<String, String> labels = kuberNode.getMetadata().getLabels();
			List<String> groupNames = new ArrayList<String>();
			for (String obj : labels.keySet())
			{
				if (obj.startsWith(Ku8ResPartionService.NODE_GROUP_NAME_PREFIX))
				{
					groupNames.add(obj.substring(Ku8ResPartionService.NODE_GROUP_NAME_PREFIX.length()));
				}
			}
			for (String name : groupNames)
			{
				if (name.equals(groupName))
				{
					String nodeName = kuberNode.getMetadata().getName();
					int cpuLimit = StringUtil.getStringNum(kuberNode.getStatus().getCapacity().get("cpu").getAmount());
					String memoryLimitStr = kuberNode.getStatus().getCapacity().get("memory").getAmount();
					int memoryLimit = StringUtil.getStringNum(memoryLimitStr);
					int podLimit = StringUtil.getStringNum(kuberNode.getStatus().getCapacity().get("pods").getAmount());
					NodeView node = new NodeView();
					node.setName(nodeName);
					node.setCpuLimit(cpuLimit);
					node.setMemoryLimit(memoryLimit);
					node.setPodLimit(podLimit);
					nodes.add(node);
					break;
				}
			}
		}
		return nodes;
	}
	/**
	 * 通过给Node增加label，给其附着Node组名
	 * 
	 * @param groupName
	 * @param names
	 */
	public void addGroupNode(String groupName, String names)
	{
		this.createNameSpace(groupName);
		String[] nodeNames = getNodeNames(names);
		for (String nodeName : nodeNames)
		{
			Node node = k8sAPIService.getNodeByName(0, nodeName);
			Map<String, String> map = node.getMetadata().getLabels();
			map.put(Ku8ResPartionService.NODE_GROUP_NAME_PREFIX + groupName, groupName);
			// System.out.println(node);
			k8sAPIService.putNode(0, nodeName, node);
		}
	}
	/**
	 * 更改指定组名下拥有的Node
	 * 
	 * @param groupName
	 * @param names 组内部新的node名字名单
	 */
	public void updateGroupNode(String groupName, String names)
	{
		Map<String, String> tempMap = new HashMap<String, String>();
		String[] nodeNames = getNodeNames(names);
		for (String nodeName : nodeNames)
		{
			if (!("".equals(nodeName)))
			{
				tempMap.put(nodeName, "");
			}
		}
		// 删除操作
		List<NodeView> oldList = this.getNodesByGroupName(groupName);
		for (NodeView node : oldList)
		{
			// 旧的在新的里边没有 执行deal
			if (tempMap.get(node.getName()) == null)
			{
				Node delNode = k8sAPIService.getNodeByName(0, node.getName());
				Map<String, String> map = delNode.getMetadata().getLabels();
				map.remove(Ku8ResPartionService.NODE_GROUP_NAME_PREFIX + groupName);
				// System.out.println(delNode);
				k8sAPIService.putNode(0, node.getName(), delNode);
			}
			else
			{
				// 从名单中删除 新旧两份名单共有 不需要操作
				tempMap.remove(node.getName());
			}
		}
		// 新增名单增加
		for (String nodeName : tempMap.keySet())
		{
			Node node = k8sAPIService.getNodeByName(0, nodeName);
			Map<String, String> map = node.getMetadata().getLabels();
			map.put(Ku8ResPartionService.NODE_GROUP_NAME_PREFIX + groupName, groupName);
			// System.out.println(node);
			k8sAPIService.putNode(0, nodeName, node);
		}
	}
	/**
	 * 将"a,b,c"类似的String 转为 String[]
	 * 
	 * @param names
	 * @return
	 */
	private String[] getNodeNames(String names)
	{
		return names.split(",");
	}
	/**
	 * 根据 Node组的名字判断其是否存在
	 * 
	 * @param groupName
	 * @return
	 */
	public boolean exsitGroupName(String groupName)
	{
		boolean flag = true;
		if (this.getNodesByGroupName(groupName).size() == 0)
		{
			flag = false;
		}
		return flag;
	}
	/**
	 * 得到所有的Node，并将指定组名的中的Node标识出来  将所有属于组内的Node添加到List 头部
	 * 加到符合组名的Node的后边
	 * 
	 * @param groupName
	 * @return
	 */
	public List<NodeView> getAllNodesWithTag(String groupName)
	{
		List<NodeView> nodes = new ArrayList<NodeView>();
		List<Node> kuberNodes = k8sAPIService.getAllNodes(0);
		for (Node kuberNode : kuberNodes)
		{
			Map<String, String> labels = kuberNode.getMetadata().getLabels();
			List<String> groupNames = new ArrayList<String>();
			for (String obj : labels.keySet())
			{
				if (obj.startsWith(Ku8ResPartionService.NODE_GROUP_NAME_PREFIX))
				{
					groupNames.add(obj.substring(Ku8ResPartionService.NODE_GROUP_NAME_PREFIX.length()));
				}
			}
			NodeView node = new NodeView();
			boolean isInGroup = false;
			for (String name : groupNames)
			{
				if (name.equals(groupName))
				{
					isInGroup = true;
					break;
				}
			}
			String nodeName = kuberNode.getMetadata().getName();
			int cpuLimit = StringUtil.getStringNum(kuberNode.getStatus().getCapacity().get("cpu").getAmount());
			String memoryLimitStr = kuberNode.getStatus().getCapacity().get("memory").getAmount();
			int memoryLimit = StringUtil.getStringNum(memoryLimitStr);
			int podLimit = StringUtil.getStringNum(kuberNode.getStatus().getCapacity().get("pods").getAmount());
			node.setChecked(isInGroup);
			node.setName(nodeName);
			node.setCpuLimit(cpuLimit);
			node.setMemoryLimit(memoryLimit);
			node.setPodLimit(podLimit);
			if(isInGroup){
				nodes.add(0, node);
			}else{
				nodes.add(node);
			}
		}
		return nodes;
	}
	/**
	 * 删除指定组名的组
	 * @param groupName
	 */
	public void delGroup(String groupName)
	{
		this.delNameSpace(groupName);
		List<NodeView> nodeList = this.getNodesByGroupName(groupName);
		for (NodeView node : nodeList)
		{
			Node delNode = k8sAPIService.getNodeByName(0, node.getName());
			Map<String, String> map = delNode.getMetadata().getLabels();
			map.remove(Ku8ResPartionService.NODE_GROUP_NAME_PREFIX + groupName);
			k8sAPIService.putNode(0, node.getName(), delNode);
		}
	}
	/**
	 * 删除指定名字的namespace
	 * @param name
	 * @return
	 */
	private boolean delNameSpace(String name){
		return k8sAPIService.deleteNameSpace(0, name);
	}
	/**
	 * 创建指定名字的namespace
	 * @param name
	 * @return
	 */
	private Namespace createNameSpace(String name){
		Namespace namespace = new NamespaceBuilder().withKind("Namespace").withNewMetadata().withName(name).endMetadata().build();
		return k8sAPIService.createNameSpace(0, namespace);
	}
	
	/**
	 * 获取所有的namespace的名字 ，去除掉   ("name": "kube-system")
	 * @return
	 */
	public List<String> getAllNameSpaceName(int clusterID) throws KubernetesClientException
	{
		List<String> list = new ArrayList<String>();
		List<Namespace> temp = k8sAPIService.getAllNameSpace(clusterID);
		
		for(Namespace namespace : temp){
			String nsName = namespace.getMetadata().getName();
			if(!nsName.equalsIgnoreCase("kube-system")){
				list.add(namespace.getMetadata().getName());
			}
		}
		return list;
	}
	/**
	 * 获取Node 中 的所有label(选取起标注Node的分组作用的label)
	 * @return
	 */
	public Map<String,String> getAllNodeGroupName(){
		Map<String,String> groupNames = new HashMap<String, String>();
		List<Node> nodes = k8sAPIService.getAllNodes(0);
		for (Node node : nodes)
		{
			Map<String, String> labels = node.getMetadata().getLabels();
			for (String obj : labels.keySet())
			{
				if (obj.startsWith(Ku8ResPartionService.NODE_GROUP_NAME_PREFIX))
				{
					groupNames.put(obj,labels.get(obj));
				}
			}
		}
		return groupNames;
	}
	
	/**
	 * 获取指定namespace中的所有job
	 * @return
	 */
	public List<String> getAllJobName(int clusterId,String namespace) throws KubernetesClientException
	{
		List<String> list = new ArrayList<String>();
		List<Job> temp = k8sAPIService.getJobs(clusterId, namespace).getItems();
		
		for(Job job : temp){
			list.add(job.getMetadata().getName());
		}
		return list;
	}
}
