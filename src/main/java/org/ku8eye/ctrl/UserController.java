package org.ku8eye.ctrl;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.ku8eye.Constants;
import org.ku8eye.bean.GridData;
import org.ku8eye.domain.User;
import org.ku8eye.service.UserGroupService;
import org.ku8eye.service.UserService;
import org.ku8eye.util.AjaxReponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@EnableAutoConfiguration  
@RestController
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private UserGroupService userGroupService;
	private static final Logger log = Logger.getLogger(UserController.class);

	@RequestMapping("/user/{id}")
	public User view(@PathVariable("id") String id) {
		return userService.getUserByUserId(id);
	}

	/**
	 * get all user types
	 * 
	 * @return
	 */
	@RequestMapping("/user/types")
	public GridData getUserType() {
		GridData grid = new GridData();
		grid.setData(userService.getUserTypes());
		return grid;
	}

	@RequestMapping("/user/addNew")
	public AjaxReponse addNewUser(@RequestParam("id") String id,@RequestParam("alias") String alias,
			@RequestParam("pass") String pass, @RequestParam("type") String type,@RequestParam("groupId") String groupId,@RequestParam("desc") String desc) 
	{
		try{
			User user=new User();
			user.setUserId(id);
			user.setAlias(alias);
			user.setPassword(pass);
			user.setUserType(type);
			user.setUserGroup(Integer.parseInt(groupId));
			user.setNote(desc);
			user.setTenantId(Constants.TENANT_DEF_ID);
			userService.addUser(user);
			return new AjaxReponse(1, "Add User");
		}catch (Exception e){
			log.error(e.getMessage(), e);
			return new AjaxReponse(0, "Add User failed");
		}
	}

	@RequestMapping("/user/updateUser")
	public AjaxReponse updateUser(@RequestParam("id") String id,@RequestParam("alias") String alias,
			@RequestParam("pass") String pass, 
			@RequestParam("type") String type,@RequestParam("desc") String desc,@RequestParam("groupId") String groupId) 
	{
		try{
			User user=userService.getUserByUserId(id);
			user.setAlias(alias);
			user.setPassword(pass);
			user.setUserType(type);
			user.setNote(desc);
			user.setUserGroup(Integer.parseInt(groupId));
			userService.updateUser(user);
			return new AjaxReponse(1, "Update User");
		}catch (Exception e){
			log.error(e.getMessage(), e);
			return new AjaxReponse(0, "Update User failed");
		}
	}
	
	@RequestMapping("/user/list")
	public GridData getAllUser() 
	{
		return new GridData(userService.getAllUser());
	}
	
	@RequestMapping(value = "/user/{id}/del")
	public AjaxReponse delUser(@PathVariable("id") String userId)
	{
	
		try{
			userService.dealUser(userId);
			return new AjaxReponse(1, "Del User");
		}catch (Exception e){
			log.error(e.getMessage(), e);
			return new AjaxReponse(0, "Del User failed");
		}
	}

	@RequestMapping("/user/updateSelfInfo")
	public User updateSelfInfo(HttpServletRequest request,
			@RequestParam("id") String id, @RequestParam("pass") String pass,
			@RequestParam("desc") String desc) {
		log.info(">>" + id + "<<");

		User user = userService.getUserByUserId(id);
		user.setPassword(pass);
		user.setNote(desc);
		userService.updateUser(user);
		request.getSession().setAttribute(
				org.ku8eye.Constants.USER_SESSION_KEY, user);
		return user;
	}

	@RequestMapping("/user/Group/{id}")
	public GridData getUserByGroupId(@PathVariable("id") String id) {
		return new GridData(userService.getAllUserByGroupId(id));
	}

	@RequestMapping("/user/changeGroup")
	public void changeGroup(HttpServletRequest request,
			@RequestParam("id") String id,
			@RequestParam("groupId") String groupId) {
		User user = userService.getUserByUserId(id);
		user.setUserGroup(Integer.parseInt(groupId));
		userService.updateUser(user);
	}

	/**
	 * check login username and password
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@RequestMapping(value = "/checklogin")
	public User checkLogin(HttpServletRequest request,@RequestParam("username") String username,
			@RequestParam("password") String password) {
		User user = userService.getUserByUserId(username);
		if (user != null && password.equals(user.getPassword())) {
			// success put user into Session
			request.getSession().setAttribute(
					org.ku8eye.Constants.USER_SESSION_KEY, user);
			return user;
		} else {
			return null;
		}
	}

	@RequestMapping(value = "/loginuser")
	public User loginuser(HttpServletRequest request) {
		User user = null;
		Object obj = request.getSession().getAttribute(
				org.ku8eye.Constants.USER_SESSION_KEY);
		if (obj != null) {
			user = (User) obj;
			log.info("get login user:" + user.getUserId());
		}
		return user;
	}
}