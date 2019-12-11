package org.ku8eye.service;

import java.util.List;

import org.ku8eye.Constants;
import org.ku8eye.domain.Ku8Project;
import org.ku8eye.mapping.Ku8ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cudcos.util.CacheDcosUrlConfig;

/**
 * 
 * @author jackchen
 *
 */
@Service
public class ApplicationService {

	@Autowired
	private Ku8ProjectMapper projectDao;

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Ku8Project> getApplications() {
		
		
		return projectDao.selectAllZoneId(Integer.parseInt(CacheDcosUrlConfig.getZoneId()));
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Ku8Project> getApplications(String owner) {
		return projectDao.selectByOwner(owner);
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Ku8Project getApplication(int id) {
		return projectDao.selectByPrimaryKey(id);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int addApplication(int tenantId, String owner, String name, String version, String note, String json) {
		Ku8Project ku8p = new Ku8Project();
		ku8p.setTenantId(tenantId);
		ku8p.setOwner(owner);
		ku8p.setName(name);
		ku8p.setIconUrl("blank");
		ku8p.setVersion(version);
		ku8p.setZoneId(Integer.parseInt(CacheDcosUrlConfig.getZoneId()));
		ku8p.setClusterId(0);
		ku8p.setNote(note);
		ku8p.setJsonSpec(json);
		ku8p.setPrevJsonSpec(null);
		ku8p.setStatus(Constants.KU8_APP_INIT_STATUS);
		ku8p.setResPart(null);
		
		return projectDao.insert(ku8p);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int importApplication(int tenantId, String owner, String name, String version, String note, String json, String namespace) {
		Ku8Project ku8p = new Ku8Project();
		ku8p.setTenantId(tenantId);
		ku8p.setOwner(owner);
		ku8p.setName(name);
		ku8p.setIconUrl("blank");
		ku8p.setVersion(version);
		ku8p.setZoneId(Integer.parseInt(CacheDcosUrlConfig.getZoneId()));
		ku8p.setClusterId(0);
		ku8p.setNote(note);
		ku8p.setJsonSpec(json);
		ku8p.setPrevJsonSpec(null);
		ku8p.setStatus(Constants.KU8_APP_DEPLOYED_STATUS);
		ku8p.setResPart(namespace);
		
		return projectDao.insert(ku8p);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int updateApplication(Ku8Project ku8p) {
		return projectDao.updateByPrimaryKey(ku8p);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public int deleteApplication(int id) {
		return projectDao.deleteByPrimaryKey(id);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int updateApplicationJSON(int id, String jsonSpec, String prevJsonSpec) {
		return projectDao.updateJSON(id, jsonSpec, prevJsonSpec);
	}
}
