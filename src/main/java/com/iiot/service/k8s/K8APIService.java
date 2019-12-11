package com.iiot.service.k8s;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.validation.Path;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.ku8eye.service.k8s.K8sAPIService;
import org.ku8eye.util.JSONUtil;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.ReplicationControllerList;
//import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.batch.JobList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * @author lenovo
 *
 */
@Service
public class K8APIService {
	
	@Autowired
	private SqlSessionFactoryBean sqlSessionFactory;
	
	@Value("${iiot.kubernetes.masterURL}")
	private String masterURL;
	@Value("${iiot.env}")
	private String env;
	@Value("${iiot.kubernetes.ca}")
	private String ca;
	@Value("${iiot.kubernetes.token}")
	private String OauthToken;
		//获取k8Client
	//TODO
		public KubernetesClient getClientOld(int clusterId) 
		{	
			String filePath = new String();
			System.out.println("Getting CA from:"+env);
			if (env.equals("dev")) {
				String str1 = this.getClass().getClassLoader().getResource("cert/"+ca).getPath();
				filePath = str1.substring(1, str1.length());
			}else if(env.equals("lab")) {
				filePath ="/cert/"+ca;
			}
			
			System.out.println("CA path is :"+filePath);
			Config config = new ConfigBuilder().withCaCertFile(filePath).withMasterUrl(masterURL).withOauthToken(OauthToken).build();
		    KubernetesClient client = new DefaultKubernetesClient(config);
			
			
			
			/*//String filePath = this.getClass().getClassLoader().getResource("cert/98ca.crt").getPath();
			String filePath = "c://Users/lenovo/git/iiot_admin/target/classes/cert/98ca.crt";
			//C:/Users/lenovo/git/iiot_admin/target/classes/cert/98ca.crt
			Config config = new ConfigBuilder().withCaCertFile(filePath).withMasterUrl(masterURL).withOauthToken(OauthToken).build();
		    KubernetesClient client = new DefaultKubernetesClient(config);*/
		    return client;
			/*KubernetesClient client = kubeClientMap.get(clusterId);
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
			}*/
		}
		
		public KubernetesClient getClient(int clusterId) {
			String filePath = new String();
			System.out.println("Getting CA from:"+env);
			if (env.equals("dev")) {
				String str1 = this.getClass().getClassLoader().getResource("cert/"+ca).getPath();
				filePath = str1.substring(1, str1.length());
			}else if(env.equals("lab")) {
				filePath ="/cert/"+ca;
			}
			
			System.out.println("CA path is :"+filePath);
			Config config = new ConfigBuilder().withCaCertFile(filePath).withMasterUrl(masterURL).withOauthToken(OauthToken).build();
		    KubernetesClient client = new DefaultKubernetesClient(config);
		    return client;
		}

	private ConcurrentHashMap<Integer, KubernetesClient> kubeClientMap = new ConcurrentHashMap<>();
	private Logger LOGGER = Logger.getLogger(K8sAPIService.class);
	
	public String getIiotUUID() {
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		return uuid;
	}
	
	public io.fabric8.kubernetes.api.model.Service createService(int clusterId, String namespace, io.fabric8.kubernetes.api.model.Service service) {
		return getClient(clusterId).services().inNamespace(namespace).create(service);
	}
	
	/**
	 * @param args
	 */
/*	Config config = new ConfigBuilder().withCaCertFile("c:/code/ca.crt").withMasterUrl("https://10.1.24.91:6443").withOauthToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImRlZmF1bHQtdG9rZW4tbnp3OTkiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiZGVmYXVsdCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjVmMDExY2EwLTgxYmQtMTFlOS1iNzRmLTAwNTA1NmE2MDc3YyIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmRlZmF1bHQifQ.oowZVz1euhCIrASteKFm43nTG0y0jGt3Zv-1pagh83ww4LHwENTHARxz10gb1JUpaYab6KUBIPdHQc1QXrSu1JXgPuTyzgGHGEXDTn9Y7OTgQ1QkEcQVl0JvzRvoFVbjggCZKngxTrCqS1Z3z0U6S7C67zoi7mUfwD7oAkHEKckiNtx-0pUkrNGLEnO-7LV2hSfhJ97OyabWsbts2J9FUd_qS592Mzm_doxrCIG22P63zgVZJdNZbjHLypXgLwyIemCsBGvS5w59lw7GM2xFFngVzrurjr68ZQqKSaK9tPEzRZ1gJDYuT6GF1b1fFAxuNvo47tNXfiiUt0ohAiSStA").build();
	KubernetesClient client = new DefaultKubernetesClient(config);
	NamespaceList myNs = client.namespaces().list();
	public static void test(NamespaceList myNs) {
		System.out.println("NameSpaceList is "+myNs.toString());
		// TODO Auto-generated method stub

	}*/

	//获取k8 client V1.14
		public KubernetesClient getClient14(int clusterId) {
			KubernetesClient client = kubeClientMap.get(clusterId);
				//	new DefaultKubernetesClient(config);
			if(client != null)
			{
				return client;
			}
			else
			{
				String masterURL = fetchMasterURLFromDB(clusterId);
				if (masterURL != null) 
				{
					Config config = new ConfigBuilder().withCaCertFile("c:/code/ca.crt").withMasterUrl("https://10.1.24.91:6443").withOauthToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImRlZmF1bHQtdG9rZW4tbnp3OTkiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiZGVmYXVsdCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjVmMDExY2EwLTgxYmQtMTFlOS1iNzRmLTAwNTA1NmE2MDc3YyIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmRlZmF1bHQifQ.oowZVz1euhCIrASteKFm43nTG0y0jGt3Zv-1pagh83ww4LHwENTHARxz10gb1JUpaYab6KUBIPdHQc1QXrSu1JXgPuTyzgGHGEXDTn9Y7OTgQ1QkEcQVl0JvzRvoFVbjggCZKngxTrCqS1Z3z0U6S7C67zoi7mUfwD7oAkHEKckiNtx-0pUkrNGLEnO-7LV2hSfhJ97OyabWsbts2J9FUd_qS592Mzm_doxrCIG22P63zgVZJdNZbjHLypXgLwyIemCsBGvS5w59lw7GM2xFFngVzrurjr68ZQqKSaK9tPEzRZ1gJDYuT6GF1b1fFAxuNvo47tNXfiiUt0ohAiSStA").build();
					
					//Config config = new ConfigBuilder().withMasterUrl(masterURL).build();
					
					/*if(USE_PROXY)
						config.setHttpProxy("http://10.1.128.200:9000");
					*/
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
		
/*		public void actions(int clusterId) {
		
			KubernetesClient client = getClient14(clusterId);
		//List resources
		NamespaceList myNs = client.namespaces().list();

		ServiceList myServices = client.services().list();

		ServiceList myNsServices = client.services().inNamespace("default").list();
		
		//get resource
		Namespace myns = client.namespaces().withName("myns").get();

		Service myservice = client.services().inNamespace("default").withName("myservice").get();
		
		
		//delete resource
		
		//Namespace myns1 = client.namespaces().withName("myns").delete();

		//Service myservice1 = client.services().inNamespace("default").withName("myservice").delete();
		
		//create resource
		
		Namespace myns1 = client.namespaces().createNew()
                .withNewMetadata()
                  .withName("myns")
                  .addToLabels("a", "label")
                .endMetadata()
                .done();

		Service myservice1 = client.services().inNamespace("default").createNew()
                  .withNewMetadata()
                    .withName("myservice")
                    .addToLabels("another", "label")
                  .endMetadata()
                  .done();
		}*/
		
		public ConfigMap getConfigMapByName(int clusterId, String namespace, String configMapName)
		{
			return getClient(clusterId).configMaps().inNamespace(namespace).withName(configMapName).get();
		}
		
		////RC
		
		public ReplicationController createReplicationController(int clusterId, String namespace, ReplicationController replicationController) {
			return getClient(clusterId).replicationControllers().inNamespace(namespace).create(replicationController);
		}
		
		public boolean deleteReplicationController(int clusterId, String namespace, String replicationControllerName) {
			return getClient(clusterId).replicationControllers().inNamespace(namespace).withName(replicationControllerName).cascading(true).delete();
		}
		
		public ReplicationController getReplicationControllerByName(int clusterId, String namespace, String replicationControllerName) {
			return getClient(clusterId).replicationControllers().inNamespace(namespace).withName(replicationControllerName).get();
		}
		
		public ReplicationControllerList getReplicationControllersByLabelsSelector(int clusterId, String namespace, Map<String, String> labels) {
			return getClient(clusterId).replicationControllers().inNamespace(namespace).withLabels(labels).list();
		}
		///Service
		public boolean deleteService(int clusterId, String namespace, String serviceName) {
			return getClient(clusterId).services().inNamespace(namespace).withName(serviceName).cascading(false).delete();
		}
		
		public io.fabric8.kubernetes.api.model.Service getServicesByName(int clusterId, String namespace, String serviceName) {
			return getClient(clusterId).services().inNamespace(namespace).withName(serviceName).get();
		}
		
		public ServiceList getServicesByLabelsSelector(int clusterId, String namespace, Map<String, String> labels) {
			return getClient(clusterId).services().inNamespace(namespace).withLabels(labels).list();
		}
		
		//Deployments
		public Deployment createDeployment(int clusterId, String namespace, Deployment deployment) {
			return getClient(clusterId).apps().deployments().inNamespace(namespace).create(deployment);
		}
		public boolean deleteDeployment(int clusterId, String namespace, String deploymentName) {
			return getClient(clusterId).apps().deployments().inNamespace(namespace).withName(deploymentName).cascading(true).delete();
		}
		
		//Pod
		
		public PodList getPodsByLabelsSelector(int clusterId, String namespace, Map<String, String> labels) {
			return getClient(clusterId).pods().inNamespace(namespace).withLabels(labels).list();
		}
		
		//Job
		
		public JobList getJobsByLabelsSelector(int clusterId, String namespace, Map<String, String> labels) {
			return getClient(clusterId).extensions().jobs().inNamespace(namespace).withLabels(labels).list();
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
				
/*				if(USE_PROXY)
					proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.1.128.200", 9000));
				else*/
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

//https://github.com/fabric8io/kubernetes-client

}
