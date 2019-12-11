package org.ku8eye.service;

import io.fabric8.kubernetes.api.model.Node;

import java.util.ArrayList;
import java.util.List;

import org.ku8eye.domain.Host;
import org.ku8eye.mapping.HostMapper;
import org.ku8eye.service.k8s.K8sAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author jackchen
 *
 */
@Service
public class HostService {

	@Autowired
	private HostMapper hostDao;
	@Autowired
	private K8sAPIService apiService;

	/**
	 * find host by zoneId
	 * 当数据库中有数据时，直接查询并返回
	 * 当数据库中无数据时，从master中reload数据到数据库，然后再查询返回
	 * 
	 * @param zoneId
	 * @return List<Host>
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Host> getHostsByZoneId(int zoneId) {
		//List<Host> dbHosts = hostDao.selectAll();
		List<Host> dbHosts = hostDao.selectByClusterId(zoneId);
		if(dbHosts != null && dbHosts.size() > 0){
			return dbHosts;
		} else {
			//从master中reload数据到数据库
			return reloadHost2Table(zoneId,zoneId);
		}
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Host getHostsByZoneString(int zoneId) {
		return hostDao.selectByPrimaryKey(zoneId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public int addHost(Host host) {
		return hostDao.insert(host);
	}
	
	/**
	 * 同步cluster 下node 信息到数据库
	 * @return ID 列表
	 * @param clusterId
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Host> reloadHost2Table(int clusterId, int zoneId) {
		
		System.out.println("========="+clusterId+"========"+zoneId);
		List<Node> nodes = apiService.getAllNodes(clusterId);
		System.out.println("=========="+nodes.size());
		return reloadHost2Table(clusterId,zoneId,nodes);
	}
	
	/**
	 * 同步指定 NODES 到数据库 
	 *  @return Host 列表
	 * @param clusterId
	 * @param nodes
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public  List<Host>  reloadHost2Table(int clusterId, int zoneId,List<Node> nodes) {
		List<Host> ids=null;
		if(nodes != null){
			ids=new ArrayList<Host>();
			for(Node node : nodes){				
			Host host=hostDao.selectByZoneIdAndIp(zoneId,node.getMetadata().getName());
				if(host==null)
				{
					host = new Host();
					host.setClusterId(clusterId);
					host.setZoneId(zoneId);
					host.setHostName(node.getMetadata().getName());
					host.setIp(node.getMetadata().getName());
					host.setMemory(Integer.parseInt(node.getStatus().getCapacity().get("memory").getAmount().replaceAll("\\D", "")));
					host.setCores(Short.parseShort(node.getStatus().getCapacity().get("cpu").getAmount().replaceAll("\\D", "")));
					hostDao.insert(host);	
				}
				else
				{
					host.setZoneId(zoneId);
					host.setMemory(Integer.parseInt(node.getStatus().getCapacity().get("memory").getAmount().replaceAll("\\D", "")));
					host.setCores(Short.parseShort(node.getStatus().getCapacity().get("cpu").getAmount().replaceAll("\\D", "")));
					hostDao.updateByPrimaryKey(host);	
				}
				ids.add(host);
			}
		}
		return ids;
	}
}
