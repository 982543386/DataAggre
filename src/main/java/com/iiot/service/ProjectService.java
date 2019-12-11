package com.iiot.service;

import java.util.List;

import org.ku8eye.Constants;
import org.ku8eye.domain.Ku8Project;
import org.ku8eye.mapping.Ku8ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cudcos.util.CacheDcosUrlConfig;
import com.iiot.dao.IiotAppListMapper;
import com.iiot.dao.IiotProjectMapper;
import com.iiot.domain.IiotAppList;
import com.iiot.domain.IiotProject;

/**
 * 
 * @author jackchen
 *
 */
@Service
public class ProjectService {

	@Autowired
	private IiotProjectMapper projectMapper;
	
	@Autowired
	private IiotAppListMapper iiotAppListMapper;
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<IiotProject> getApplications() {
		//return projectMapper.selectAllZoneId(Integer.parseInt(CacheDcosUrlConfig.getZoneId()));
		return projectMapper.selectAllZoneId(1);
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<IiotProject> getApplications(String owner) {
		return projectMapper.selectByOwner(owner);
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<IiotProject> getNamesAndProjectId(String owner) {
		return projectMapper.selectNamesAndProjectIdByOwner(owner);
	}
	
	
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public IiotProject getApplication(Integer id) {
		return projectMapper.selectByPrimaryKey(id);
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public IiotProject getApplicationByProjectId(String projectId) {
		return projectMapper.selectByProjectId(projectId);
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int addApplication(int tenantId, String owner, String name, String version, String note, String json,Byte status,String projectId ) {
		IiotProject spec = new IiotProject();
		spec.setTenantId(tenantId);
		spec.setOwner(owner);
		spec.setName(name);
		spec.setIconUrl("blank");
		spec.setVersion(version);
		//spec.setZoneId(Integer.parseInt(CacheDcosUrlConfig.getZoneId()));
		spec.setZoneId(1);
		spec.setClusterId(0);
		spec.setNote(note);
		spec.setStatus((byte) 0);
		spec.setJsonSpec(json);
		spec.setPrevJsonSpec(null);
		spec.setStatus(Constants.KU8_APP_INIT_STATUS);
		spec.setResPart(null);
		spec.setProjectId(projectId);
		System.out.println("==================spec string"+spec.toString());
		
		return projectMapper.insert(spec);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int deleteApplication(String projectId ) {
		
		return projectMapper.deleteByProjectId(projectId);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int importApplication(int tenantId, String owner, String name, String version, String note, String json, String namespace) {
		IiotProject spec = new IiotProject();
		spec.setTenantId(tenantId);
		spec.setOwner(owner);
		spec.setName(name);
		spec.setIconUrl("blank");
		spec.setVersion(version);
		spec.setZoneId(Integer.parseInt(CacheDcosUrlConfig.getZoneId()));
		spec.setClusterId(0);
		spec.setNote(note);
		spec.setJsonSpec(json);
		spec.setPrevJsonSpec(null);
		spec.setStatus(Constants.KU8_APP_DEPLOYED_STATUS);
		spec.setResPart(namespace);
		
		return projectMapper.insert(spec);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int updateApplication(IiotProject spec) {
		return projectMapper.updateByPrimaryKey(spec);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public int deleteApplication(int id) {
		return projectMapper.deleteByPrimaryKey(id);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int updateApplicationJSON(int id, String jsonSpec, String prevJsonSpec) {
		return projectMapper.updateJSON(id, jsonSpec, prevJsonSpec);
	}
	
	//部署应用到node 表 IiotAppList
	@Transactional(propagation = Propagation.REQUIRED)
	public IiotAppList getIiotApp(int appId) {
		return iiotAppListMapper.selectByPrimaryKey(appId);
	}
	
	//update 表 IiotAppList
	@Transactional(propagation = Propagation.REQUIRED)
	public int updateIiotAppList(IiotAppList record) {
		return iiotAppListMapper.updateByPrimaryKey(record);
	}
}
