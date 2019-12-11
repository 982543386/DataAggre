package org.ku8eye.ctrl;

import java.util.List;

import org.ku8eye.bean.GridData;
import org.ku8eye.bean.respartion.NodeView;
import org.ku8eye.bean.respartion.ResPartion;
import org.ku8eye.domain.Ku8ResPartion;
import org.ku8eye.service.Ku8ResPartionService;
import org.ku8eye.util.AjaxReponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

@RestController
@SessionAttributes("user")
public class Ku8ResPartionController
{
	@Autowired
	private Ku8ResPartionService resPartionService;

	@RequestMapping(value = "/respartions/{clusterId}")
	public GridData getRespartionsWithSql(@PathVariable("clusterId") int clusterId)
	{
		GridData grid = new GridData();
		List<Ku8ResPartion> pros = resPartionService.getAllResPartions(clusterId);
		grid.setData(pros);
		return grid;
	}

	@RequestMapping(value = "/respartions")
	public GridData getRespartions()
	{
		GridData grid = new GridData();
		List<ResPartion> res = resPartionService.getAllResPartions();
		grid.setData(res);
		return grid;
	}

	@RequestMapping(value = "/respartions/nodes")
	public GridData getAllNodes()
	{
		GridData grid = new GridData();
		List<NodeView> res = resPartionService.getAllNodes();
		grid.setData(res);
		return grid;
	}
	
	@RequestMapping(value = "/respartions/nodes/{groupName}/add",method=RequestMethod.POST)
	public AjaxReponse addGroup(@PathVariable("groupName") String groupName,@RequestParam("names") String names, ModelMap model)
	{
		try{
			if(resPartionService.exsitGroupName(groupName)){
				return new AjaxReponse(-1, "Group Name Exsit,You Need Change Group Name.");
			}else{
				resPartionService.addGroupNode(groupName,names);
				return new AjaxReponse(1, "add NodeGroup");
			}
		}catch (Exception e){
			e.printStackTrace();
			return new AjaxReponse(0, "add NodeGroup failed");
		}
	}

	@RequestMapping(value = "/respartions/nodes/{groupName}")
	public GridData getAllNodesWithTag(@PathVariable("groupName") String groupName)
	{
		GridData grid = new GridData();
		List<NodeView> res = resPartionService.getAllNodesWithTag(groupName);
		grid.setData(res);
		return grid;
	}
	
	@RequestMapping(value = "/respartions/nodes/{groupName}/del",method=RequestMethod.POST)
	public AjaxReponse delGroup(@PathVariable("groupName") String groupName)
	{
		try{
			resPartionService.delGroup(groupName);
			return new AjaxReponse(1, "Del NodeGroup");
		}catch (Exception e){
			e.printStackTrace();
			return new AjaxReponse(0, "Del NodeGroup failed");
		}
	}
	
	@RequestMapping(value = "/respartions/nodes/{groupName}/update",method=RequestMethod.POST)
	public AjaxReponse updateGroup(@PathVariable("groupName") String groupName,@RequestParam("names") String names)
	{
		try{
			resPartionService.updateGroupNode(groupName,names);
			return new AjaxReponse(1, "update NodeGroup");
		}catch (Exception e){
			e.printStackTrace();
			return new AjaxReponse(0, "update NodeGroup failed");
		}
	}
}
