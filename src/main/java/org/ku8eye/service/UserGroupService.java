package org.ku8eye.service;

import java.util.List;

import org.ku8eye.domain.UserGroup;
import org.ku8eye.mapping.UserGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserGroupService {

	@Autowired
	private UserGroupMapper userGroupDao;

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public UserGroup getUserGroupByGroupId(Integer groupId) {
		return userGroupDao.selectByPrimaryKey(groupId);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<UserGroup> getAllUserGroup() {
		return userGroupDao.selectAll();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delUserGroup(Integer groupId) {
		userGroupDao.deleteByPrimaryKey(groupId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateUserGroup(String groupId,String groupName,String tenantId,String ku8ParttionIds){
		UserGroup userGroup = new UserGroup();
		userGroup.setGroupId(Integer.parseInt(groupId));
		userGroup.setGroupName(groupName);
		userGroup.setKu8ParttionIds(ku8ParttionIds);
		userGroup.setTenantId(Integer.parseInt(tenantId));
		userGroupDao.updateByPrimaryKey(userGroup);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void addUserGroup(String groupName,String ku8ParttionIds){
		UserGroup userGroup = new UserGroup();
		userGroup.setGroupName(groupName);
		userGroup.setTenantId(0);// TODO 补充来源
		userGroup.setKu8ParttionIds(ku8ParttionIds);
		userGroupDao.insert(userGroup);
	}

	/**
	 * 获取group对应的所有partion 数组
	 * 
	 * @param group_id
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public String[] getPartionsByGroupId(Integer id) {
		UserGroup ug = getUserGroupByGroupId(id);
		return ug.getKu8ParttionIds().split(",");
	}

	

}
