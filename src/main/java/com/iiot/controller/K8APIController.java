/**
 * 
 */
package com.iiot.controller;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.io.*;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.ku8eye.bean.GridData;
import org.ku8eye.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cudcos.controller.OrderController;

import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * @author lenovo
 *
 */
@RestController
@RequestMapping("/k814")
@SessionAttributes(org.ku8eye.Constants.USER_SESSION_KEY)
//@SessionAttributes("ku8template")
public class K8APIController {
	public static final Logger log = Logger.getLogger(OrderController.class);
	//KubernetesClient client = new DefaultKubernetesClient();
	
	
	/*Config config = new ConfigBuilder().withCaCertFile("c:/code/ca.crt").withMasterUrl("https://10.1.24.91:6443").withOauthToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImRlZmF1bHQtdG9rZW4tbnp3OTkiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiZGVmYXVsdCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjVmMDExY2EwLTgxYmQtMTFlOS1iNzRmLTAwNTA1NmE2MDc3YyIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmRlZmF1bHQifQ.oowZVz1euhCIrASteKFm43nTG0y0jGt3Zv-1pagh83ww4LHwENTHARxz10gb1JUpaYab6KUBIPdHQc1QXrSu1JXgPuTyzgGHGEXDTn9Y7OTgQ1QkEcQVl0JvzRvoFVbjggCZKngxTrCqS1Z3z0U6S7C67zoi7mUfwD7oAkHEKckiNtx-0pUkrNGLEnO-7LV2hSfhJ97OyabWsbts2J9FUd_qS592Mzm_doxrCIG22P63zgVZJdNZbjHLypXgLwyIemCsBGvS5w59lw7GM2xFFngVzrurjr68ZQqKSaK9tPEzRZ1gJDYuT6GF1b1fFAxuNvo47tNXfiiUt0ohAiSStA").build();
	KubernetesClient client = new DefaultKubernetesClient(config);
	NamespaceList myNs = client.namespaces().list();*/
	 //System.out.println(); 
/*	public void  main(NamespaceList myNs) {
        System.out.println("NameSpaceList is "+myNs.toString());
	}*/
	@RequestMapping(value = "/count")
	public int grid(ModelMap model)  {
		/*int orderCount = 0;
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);
		String userId = user.getUserId();
		
		Config config = new ConfigBuilder().withCaCertFile("c:/code/ca.crt").withMasterUrl("https://10.1.24.91:6443").withOauthToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImRlZmF1bHQtdG9rZW4tbnp3OTkiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiZGVmYXVsdCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjVmMDExY2EwLTgxYmQtMTFlOS1iNzRmLTAwNTA1NmE2MDc3YyIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmRlZmF1bHQifQ.oowZVz1euhCIrASteKFm43nTG0y0jGt3Zv-1pagh83ww4LHwENTHARxz10gb1JUpaYab6KUBIPdHQc1QXrSu1JXgPuTyzgGHGEXDTn9Y7OTgQ1QkEcQVl0JvzRvoFVbjggCZKngxTrCqS1Z3z0U6S7C67zoi7mUfwD7oAkHEKckiNtx-0pUkrNGLEnO-7LV2hSfhJ97OyabWsbts2J9FUd_qS592Mzm_doxrCIG22P63zgVZJdNZbjHLypXgLwyIemCsBGvS5w59lw7GM2xFFngVzrurjr68ZQqKSaK9tPEzRZ1gJDYuT6GF1b1fFAxuNvo47tNXfiiUt0ohAiSStA").build();
		KubernetesClient client = new DefaultKubernetesClient(config);
		NamespaceList myNs = client.namespaces().list();
		NodeList nodeList = client.nodes().list();
		System.out.println("NodeList is "+nodeList.toString());
		System.out.println("NameSpaceList is "+myNs.toString());
		
		client.close();
		String countNum = "string";
		GridData grid = new GridData();
		grid.setData(countNum);*/
		int number = 6677;
		return number;
	}
}
/*import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;
public class K8APIService {
	public static void main(String[] args) throws IOException, ApiException{
		Config.
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
        for (V1Pod item : list.getItems()) {
            System.out.println(item.getMetadata().getName());
        }
    }
}*/
