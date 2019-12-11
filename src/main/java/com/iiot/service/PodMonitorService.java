package com.iiot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiot.service.k8s.K8APIService;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodCondition;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.PodStatus;

@Service
public class PodMonitorService {
	
	@Autowired
	private K8APIService k8apiService;
	
	public List<Pod> getPodListByServiceName(String serviceName) {
		String namespace = "default";
		io.fabric8.kubernetes.api.model.Service service = k8apiService.getServicesByName(0, namespace, serviceName);
		PodList podList = k8apiService.getPodsByLabelsSelector(0, namespace, service.getSpec().getSelector());
		return podList.getItems();
	}
}
