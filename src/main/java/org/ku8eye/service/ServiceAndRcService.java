package org.ku8eye.service;

import java.util.List;

import org.ku8eye.Constants;
import org.ku8eye.domain.Ku8Service;
import org.ku8eye.mapping.Ku8ServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cudcos.util.CacheDcosUrlConfig;

@Service
public class ServiceAndRcService
{
	@Autowired
	private Ku8ServiceMapper serviceDao;

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Ku8Service> getAllServiceAndRc(String owner)
	{
		return serviceDao.selectByOwner(owner);
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Ku8Service> getAllServiceAndRcByTenantId(int tenantId)
	{
		return serviceDao.selectByTenantId(tenantId);
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Ku8Service> getAllServiceAndRc()
	{
		//return serviceDao.selectAll();
		return serviceDao.selectByZoneId(Integer.parseInt(CacheDcosUrlConfig.getZoneId()));
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int deleteService(int id) {
		return serviceDao.deleteByPrimaryKey(id);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Ku8Service getServiceAndRc(int id)
	{
		return serviceDao.selectByPrimaryKey(id);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateServiceAndRc(Ku8Service ku8s)
	{
		serviceDao.updateByPrimaryKey(ku8s);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public int addServiceAndRc(int tenantId, String owner, org.ku8eye.bean.project.Service service)
	{
		Ku8Service ku8Service = new Ku8Service();
		ku8Service.setTenantId(tenantId);
		ku8Service.setOwner(owner);
		ku8Service.setName(service.getName());
		ku8Service.setReplica((byte) (service.getReplica()));
		ku8Service.setIconUrl("blank");
		ku8Service.setVersion(service.getVersion());
		ku8Service.setZoneId(Integer.parseInt(CacheDcosUrlConfig.getZoneId()));
		ku8Service.setClusterId(0);
		ku8Service.setJsonSpec(service.toJSONString());
		ku8Service.setStatus(Constants.KU8_APP_INIT_STATUS);
		ku8Service.setNote(service.getDescribe());
		return serviceDao.insert(ku8Service);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int updateServiceJSON(int id, String jsonSpec, String prevJsonSpec) {
		return serviceDao.updateJSON(id, jsonSpec, prevJsonSpec);
	}
	
	//SHARED METHOD
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<String> hasServiceName(String serviceName) {
		serviceName = "%\"name\":\"" + serviceName + "\"%";
		return serviceDao.hasInJSON(serviceName);
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<String> hasNodePort(String nodePort) {
		nodePort = "%\"nodePort\":" + nodePort + "%";
		return serviceDao.hasInJSON(nodePort);
	}
}
