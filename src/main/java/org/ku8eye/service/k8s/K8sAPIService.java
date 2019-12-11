package org.ku8eye.service.k8s;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.ku8eye.util.JSONUtil;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.api.model.DoneableNode;
import io.fabric8.kubernetes.api.model.EventList;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.ReplicationControllerList;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.batch.Job;
import io.fabric8.kubernetes.api.model.batch.JobList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
/*import io.fabric8.kubernetes.client.dsl.ClientNonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.ClientResource;*/
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;

@Service
public class K8sAPIService {
	
	//CHANGE THIS TO FALSE FOR PRODUCTION
	private static final boolean USE_PROXY = false;
	
	private Logger LOGGER = Logger.getLogger(K8sAPIService.class);
	private ConcurrentHashMap<Integer, KubernetesClient> kubeClientMap = new ConcurrentHashMap<>();
	
	@Autowired
	private SqlSessionFactoryBean sqlSessionFactory;

	//获取k8Client
	public KubernetesClient getClient(int clusterId) 
	{
		KubernetesClient client = kubeClientMap.get(clusterId);
		if(client != null)
		{
			return client;
		}
		else
		{
			String masterURL = fetchMasterURLFromDB(clusterId);
			if (masterURL != null) 
			{
				Config config = new ConfigBuilder().withMasterUrl(masterURL).build();
				
				if(USE_PROXY)
					config.setHttpProxy("http://10.1.128.200:9000");
				
				client = new DefaultKubernetesClient(config);
				kubeClientMap.put(clusterId, client);
				return client;
			}
			else
			{
				throw new java.lang.RuntimeException("Can't create KubernetesClient for cluster: " + clusterId);
			}
		}
	}
	
	public String fetchMasterURLFromDB(int clusterId) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.getObject().openSession(true);
			Statement stmt = session.getConnection().createStatement();
			ResultSet rs = stmt
					.executeQuery("select SERVICE_URL from ku8s_srv_endpoint where CLUSTER_ID =" + clusterId);
			
			System.out.println("==========select SERVICE_URL from ku8s_srv_endpoint where CLUSTER_ID ="+ clusterId);
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			LOGGER.warn("sql exe err :" + e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		LOGGER.warn("cant' find master url for cluster " + clusterId);
		return null;
	}
	
	//SERVICES

	public ServiceList getServices(int clusterId, String namespace) {
		return getClient(clusterId).services().inNamespace(namespace).list();
	}
	
	public ServiceList getServicesByLabelsSelector(int clusterId, String namespace, Map<String, String> labels) {
		return getClient(clusterId).services().inNamespace(namespace).withLabels(labels).list();
	}
	
	public io.fabric8.kubernetes.api.model.Service getServicesByName(int clusterId, String namespace, String serviceName) {
		return getClient(clusterId).services().inNamespace(namespace).withName(serviceName).get();
	}
	
	public io.fabric8.kubernetes.api.model.Service createService(int clusterId, String namespace, io.fabric8.kubernetes.api.model.Service service) {
		return getClient(clusterId).services().inNamespace(namespace).create(service);
	}
	
	public boolean deleteService(int clusterId, String namespace, String serviceName) {
		return getClient(clusterId).services().inNamespace(namespace).withName(serviceName).cascading(false).delete();
	}
	
	public io.fabric8.kubernetes.api.model.Service replaceService(int clusterId, String namespace, String serviceName, io.fabric8.kubernetes.api.model.Service service) {
		return getClient(clusterId).services().inNamespace(namespace).withName(serviceName).replace(service);
	}
	
	public io.fabric8.kubernetes.api.model.Service addLabelsService(int clusterId, String namespace, String serviceName, Map<String, String> labels) {
		return getClient(clusterId).services().inNamespace(namespace).withName(serviceName).cascading(false).edit().editMetadata().addToLabels(labels).endMetadata().done();
	}
	
	public io.fabric8.kubernetes.api.model.Service replaceLabelsService(int clusterId, String namespace, String serviceName, Map<String, String> labels) {
		return getClient(clusterId).services().inNamespace(namespace).withName(serviceName).cascading(false).edit().editMetadata().withLabels(labels).endMetadata().done();
	}
	
	//PODS
	
	public PodList getPodsByLabelsSelector(int clusterId, String namespace, Map<String, String> labels) {
		return getClient(clusterId).pods().inNamespace(namespace).withLabels(labels).list();
	}
	
	public String getPodLogByName(int clusterId, String namespace, String podName, String containerName) {
		return getClient(clusterId).pods().inNamespace(namespace).withName(podName).inContainer(containerName).tailingLines(80).withPrettyOutput().getLog();
	}
	
	public boolean deletePod(int clusterId, String namespace, String podName) {
		return getClient(clusterId).pods().inNamespace(namespace).withName(podName).cascading(false).delete();
	}
	
	public ExecWatch execPod(int clusterId, String namespace, String podName, ExecListener listener) 
	{
		return getClient(clusterId).pods().inNamespace(namespace).withName(podName)
				.redirectingInput()
                .redirectingOutput()
                .usingListener(listener)
                .exec();
	}
	
	//NODES
	
	public List<Node> getAllNodes(int clusterId) {
		return getClient(clusterId).nodes().list().getItems();
	}
	public Node getNodeByName(int clusterId, String nodeName){
		NonNamespaceOperation<Node, NodeList, DoneableNode, Resource<Node, DoneableNode>> nodesClient = getClient(clusterId).nodes();
		Resource<Node, DoneableNode> nodesResource = nodesClient.withName(nodeName);
		return nodesResource.get();
	}
	
	public NodeList getNodeByLabelSelector(int clusterId, Map<String, String> label){
		NonNamespaceOperation<Node, NodeList, DoneableNode, Resource<Node, DoneableNode>> nodesClient = getClient(clusterId).nodes();
		return nodesClient.withLabels(label).list();
	}
	
	public Node putNode(int clusterId, String nodeName, Node node){
		NonNamespaceOperation<Node, NodeList, DoneableNode, Resource<Node, DoneableNode>> nodesClient = getClient(clusterId).nodes();
		Resource<Node, DoneableNode> nodesResource = nodesClient.withName(nodeName);
		return nodesResource.replace(node);
	}
	
	/**
	 * 获取所有Node
	 * @param clusterId
	 * @return
	 */
	public List<String> getK8sAliveNodes(int clusterId) {
		List<String> nodeIps = new LinkedList<String>();
		KubernetesClient client = getClient(clusterId);
		NodeList nodeList = client.nodes().list();
		for (Node node : nodeList.getItems()) {
			System.out.println(node.getMetadata().getName());
			nodeIps.add(node.getSpec().toString());
		}
		return nodeIps;
	}
	
	//NAMESPACE
	
	public Namespace createNameSpace(int clusterId,Namespace namespace){
		return getClient(clusterId).namespaces().create(namespace);
	}
	
	public boolean deleteNameSpace(int clusterId,String name){
		return getClient(clusterId).namespaces().withName(name).cascading(false).delete();
	}
	
	public Namespace getNameSpaceByName(int clusterId,String name){
		return getClient(clusterId).namespaces().withName(name).get();
	}
	
	public List<Namespace> getAllNameSpace(int clusterId){
		return getClient(clusterId).namespaces().list().getItems();
	}
	
	//TODO 找新函数
/*	public Namespace updateNameSpace(int clusterId,Namespace namespace){
		return getClient(clusterId).namespaces().withName(namespace.getMetadata().getName()).update(namespace);
	}*/
	
	public Namespace putNameSpace(int clusterId,Namespace namespace){
		return getClient(clusterId).namespaces().withName(namespace.getMetadata().getName()).replace(namespace);
	}
	
	//REPLICATION CONTROLLERS
	
	public ReplicationController createReplicationController(int clusterId, String namespace, ReplicationController replicationController) {
		return getClient(clusterId).replicationControllers().inNamespace(namespace).create(replicationController);
	}
	
	public ReplicationControllerList getReplicationControllers(int clusterId, String namespace) {
		return getClient(clusterId).replicationControllers().inNamespace(namespace).list();
	}
	
	public ReplicationController getReplicationControllerByName(int clusterId, String namespace, String replicationControllerName) {
		return getClient(clusterId).replicationControllers().inNamespace(namespace).withName(replicationControllerName).get();
	}
	
	public ReplicationController addLabelsReplicationController(int clusterId, String namespace, String replicationControllerName, Map<String, String> labels) {
		return getClient(clusterId).replicationControllers().inNamespace(namespace).withName(replicationControllerName).cascading(false).edit().editMetadata().addToLabels(labels).endMetadata().done();
	}
	
	public ReplicationController replaceLabelsReplicationController(int clusterId, String namespace, String replicationControllerName, Map<String, String> labels) {
		return getClient(clusterId).replicationControllers().inNamespace(namespace).withName(replicationControllerName).cascading(false).edit().editMetadata().withLabels(labels).endMetadata().done();
	}
	
	public ReplicationController scaleReplicationController(int clusterId, String namespace, String replicationControllerName, int replica) {
		return getClient(clusterId).replicationControllers().inNamespace(namespace).withName(replicationControllerName).scale(replica);
	}
	
	public ReplicationController replaceReplicationController(int clusterId, String namespace, String replicationControllerName, ReplicationController replicationController) {
		return getClient(clusterId).replicationControllers().inNamespace(namespace).withName(replicationControllerName).rolling().replace(replicationController);
	}
	
	public boolean deleteReplicationController(int clusterId, String namespace, String replicationControllerName) {
		return getClient(clusterId).replicationControllers().inNamespace(namespace).withName(replicationControllerName).cascading(true).delete();
	}
	
	public ReplicationControllerList getReplicationControllersByLabelsSelector(int clusterId, String namespace, Map<String, String> labels) {
		return getClient(clusterId).replicationControllers().inNamespace(namespace).withLabels(labels).list();
	}
	
	//JOBS 
	
	public Job createJob (int clusterId, String namespace, Job job) {
		return getClient(clusterId).batch().jobs().create(job);//.extensions().jobs().inNamespace(namespace).create(job);
	}
	
	public JobList getJobs(int clusterId, String namespace) {
		return getClient(clusterId).batch().jobs().inNamespace(namespace).list();//.extensions().jobs().inNamsespace(namespace).list();
	}
	
	public Job getJobByName(int clusterId, String namespace, String jobName) {
		return getClient(clusterId).extensions().jobs().inNamespace(namespace).withName(jobName).get();
	}
	
	public Job addLabelsJob(int clusterId, String namespace, String jobName, Map<String, String> labels) {
		return getClient(clusterId).extensions().jobs().inNamespace(namespace).withName(jobName).cascading(false).edit().editMetadata().addToLabels(labels).endMetadata().done();
	}
	
	public Job replaceLabelsJob(int clusterId, String namespace, String jobName, Map<String, String> labels) {
		return getClient(clusterId).extensions().jobs().inNamespace(namespace).withName(jobName).cascading(false).edit().editMetadata().withLabels(labels).endMetadata().done();
	}
	
	public Job replaceJob(int clusterId, String namespace, String jobName, Job job) {
		return getClient(clusterId).extensions().jobs().inNamespace(namespace).withName(jobName).replace(job);
	}
	
	public boolean deleteJob(int clusterId, String namespace, String jobName) {
		return getClient(clusterId).extensions().jobs().inNamespace(namespace).withName(jobName).cascading(true).delete();
	}
	
	public JobList getJobsByLabelsSelector(int clusterId, String namespace, Map<String, String> labels) {
		return getClient(clusterId).extensions().jobs().inNamespace(namespace).withLabels(labels).list();
	}
	
	//EVENTS

	public EventList getEvents(int clusterId){
		return getClient(clusterId).events().list();
	}
	
	//CONFIG MAPS
	
	public ConfigMapList getConfigMaps(int clusterId)
	{
		return getClient(clusterId).configMaps().inAnyNamespace().list();
	}
	
	public ConfigMap getConfigMapByName(int clusterId, String namespace, String configMapName)
	{
		return getClient(clusterId).configMaps().inNamespace(namespace).withName(configMapName).get();
	}
	
	public ConfigMap createConfigMap(int clusterId, String namespace, String configName, Map<String, String> definition, Map<String, String> map)
	{
		ConfigMapBuilder builder = new ConfigMapBuilder();
		ConfigMap confMap = builder.addToData(map).withNewMetadata().addToLabels(definition).withName(configName).endMetadata().build();
		return getClient(clusterId).configMaps().inNamespace(namespace).create(confMap);
	}
	
	public boolean deleteConfigMap(int clusterId, String namespace, String configMapName)
	{
		return getClient(clusterId).configMaps().inNamespace(namespace).withName(configMapName).cascading(false).delete();
	}
	
	public ConfigMap replaceConfigMap(int clusterId, String namespace, String configMapName, ConfigMap configMap) {
		return getClient(clusterId).configMaps().inNamespace(namespace).withName(configMapName).replace(configMap);
	}
	
	/**
	 * get ku8 version by cluster id
	 * @param clusterId
	 * @return
	 */
	public String getKubernetesVersion(int clusterId)
	{
		URL masterURL = getClient(clusterId).getMasterUrl();
		return getKubernetesVersion(masterURL);
	}
	
	/**
	 *  get ku8 version by Api master Url;
	 * @param masterURL
	 * @return
	 */
	public String getKubernetesVersion(URL masterURL)
	{
		try
		{
			URL versionURL = new URL(masterURL, "/version");
			Proxy proxy;
			
			if(USE_PROXY)
				proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.1.128.200", 9000));
			else
				proxy = Proxy.NO_PROXY;
			
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(versionURL.openConnection(proxy).getInputStream(), "UTF-8"));)
			{	
				String version = "";
				for(String line; (line = reader.readLine()) != null;)
					version += line;
				reader.close();
				
				Map<String, String> versionMap = JSONUtil.toObjectMap(version, String.class, String.class);
				
				return versionMap.get("gitVersion");
			}
			catch (UnsupportedEncodingException e)
			{
				LOGGER.error("Enconding error on getKubernetsVersion, " + e.getMessage());
				return null;
			}
			catch (IOException e)
			{
				LOGGER.error("IOException while on getKubernetsVersion, " + e.getMessage());
				return null;
			}
		}
		catch (MalformedURLException e)
		{
			LOGGER.error("MalformedURLException while on getKubernetsVersion, " + e.getMessage());
			return null;
		}
	}
}
