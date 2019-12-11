package org.ku8eye.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.ku8eye.Constants;
import org.ku8eye.domain.User;
import org.ku8eye.mapping.UserMapper;
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
public class UserService {
	
	@Autowired
	private UserMapper userDao;
	/**
	 * find User by userid
	 * @param pUserId
	 * @return User
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public User getUserByUserId(String userId){
		return userDao.selectByPrimaryKey(userId);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public List<User> getAllUserByGroupId(String id) {
		return userDao.selectByGroupId(id);
	}
	

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateUser(User user)
	{
		userDao.updateByPrimaryKey(user);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void addUser(User user)
	{
		userDao.insert(user);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void dealUser(String userId)
	{
		userDao.deleteByPrimaryKey(userId);
	}
	
	
	public String getOwnerStrByUser(User user)
	{
		String type=user.getUserType();
		
		if(type.equals(Constants.USERTYPE_ADMIN))
		{
			return Constants.USER_GROUP_ALL;
		}
		else if(type.equals(Constants.USERTYPE_GROUP_ADMIN))
		{
			return getOwnerStrByGroupId(user.getUserGroup()+"");
		}
		else if(type.equals(Constants.USERTYPE_TENANT))
		{
		
			return user.getUserId();
		}
		return null;
	}
	
	
	public String getOwnerStrByGroupId(String id)
	{
		 List<User> us=getAllUserByGroupId(id);
		 
		 String owners="";
		 for(User u:us)
		 {
			 owners= u.getUserId()+"','";
		 }
		 
		 return owners;
	}
	
	public HashMap<String,String> getUserTypes()
	{
		HashMap<String,String> map=new HashMap<String, String>();
		
		map.put(Constants.USERTYPE_ADMIN, "SUPER ADMIN");
		
		map.put(Constants.USERTYPE_TENANT, "USER");
		
		return map;
		
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<User> getAllUser() {
		return userDao.selectAll();
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<String> getResourcePartitionByUserId(String id) {
		String res_part = userDao.selectResourcePartitionByUserId(id);
		if(res_part != null)
			return  Arrays.asList(res_part.split("\\s*,\\s*"));
		return null;
	}
}

