package org.ku8eye.ctrl;

import java.util.List;

import org.ku8eye.bean.GridData;
import org.ku8eye.domain.UserGroup;
import org.ku8eye.service.Ku8ResPartionService;
import org.ku8eye.service.UserGroupService;
import org.ku8eye.util.AjaxReponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserGroupController
{
	private static final Logger logger = LoggerFactory.getLogger(UserGroupController.class);

	@Autowired
	private UserGroupService service;
	
	@Autowired
	private Ku8ResPartionService resPartionService;

	@RequestMapping(value = "/usergroup/list")
	public GridData getAllUserGroup()
	{
		GridData grid = new GridData();
		List<UserGroup> res = service.getAllUserGroup();
		grid.setData(res);
		return grid;
	}
	
	@RequestMapping(value = "/usergroup/namespaces")
	public List<String> getAllNameSpaceName()
	{
		List<String> names = resPartionService.getAllNameSpaceName(0);
		return names;
	}
	
	@RequestMapping(value = "/usergroup/{groupId}/del",method=RequestMethod.POST)
	public AjaxReponse delUserGroup(@PathVariable("groupId") Integer groupId)
	{
		try{
			service.delUserGroup(groupId);
			return new AjaxReponse(1, "Del UserGroup");
		}catch (Exception e){
			logger.error(e.getMessage(), e);
			return new AjaxReponse(0, "Del UserGroup failed");
		}
	}
	
	@RequestMapping(value = "/usergroup/add",method=RequestMethod.POST)
	public AjaxReponse addUserGroup(@RequestParam("groupName") String groupName,@RequestParam("ku8ParttionIds") String ku8ParttionIds)
	{
		try{
			service.addUserGroup(groupName,ku8ParttionIds);
			return new AjaxReponse(1, "add UserGroup");
		}catch (Exception e){
			logger.error(e.getMessage(), e);
			return new AjaxReponse(0, "add UserGroup failed");
		}
	}
	
	@RequestMapping(value = "/usergroup/{groupId}/update",method=RequestMethod.POST)
	public AjaxReponse updateUserGroup(@PathVariable("groupId") String groupId,@RequestParam("groupName") String groupName,@RequestParam("tenantId") String tenantId,@RequestParam("ku8ParttionIds") String ku8ParttionIds)
	{
		try{
			service.updateUserGroup(groupId,groupName,tenantId,ku8ParttionIds);
			return new AjaxReponse(1, "update UserGroup");
		}catch (Exception e){
			logger.error(e.getMessage(), e);
			return new AjaxReponse(0, "update UserGroup failed");
		}
	}
}
