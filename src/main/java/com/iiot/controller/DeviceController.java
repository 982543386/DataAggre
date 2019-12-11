/**
 * 
 */
package com.iiot.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.ku8eye.bean.GridData;
import org.ku8eye.domain.User;
import org.ku8eye.util.AjaxReponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iiot.entity.Device;
import com.iiot.service.DeviceService;

/**
 * 设备管理Controller
 * @author zhaich5
 *
 */
@RestController
@RequestMapping("/iiot/device")
public class DeviceController {
	
	private static final Logger log = Logger.getLogger(DeviceController.class);
	
	@Autowired
	private DeviceService service;
	
	/**
	 * 创建设备
	 * @param request
	 * @param device
	 * @return
	 */
	@RequestMapping(value = "/create")
	public AjaxReponse createDevice(HttpServletRequest request, @RequestBody Device device) {

		User user = null;
		Object obj = request.getSession()
				.getAttribute(org.ku8eye.Constants.USER_SESSION_KEY);
		if (obj != null) {
			user = (User) obj;
			if(log.isInfoEnabled())
				log.info("get login user:" + user.getUserId());
		}
		if (user == null) {
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(500, "PLEASE LOGIN");
		}
		
		device.setCreater(user.getUserId());
		device.setCreateTime(new Date());
		int i = service.saveDeviceInfo(device);
		if (i != 1) {
			log.error("创建设备[" + device.getDeviceName() + "]失败, 返回结果[" + i + "]");
			return new AjaxReponse(i, "CREATE FAILED");
		} else {
			if(log.isInfoEnabled())
				log.info("创建设备[" + device.getDeviceName() + "]成功");
			return new AjaxReponse(i, "CREATE SUCCEED");
		}
		
	}
	
	/**
	 * 修改设备
	 * @param request
	 * @param device
	 * @return
	 */
	@RequestMapping(value = "/modify")
	public AjaxReponse modifyDevice(HttpServletRequest request, @RequestBody Device device) {

		User user = null;
		Object obj = request.getSession()
				.getAttribute(org.ku8eye.Constants.USER_SESSION_KEY);
		if (obj != null) {
			user = (User) obj;
			if(log.isInfoEnabled())
				log.info("get login user:" + user.getUserId());
		}
		if (user == null) {
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(500, "PLEASE LOGIN");
		}
		
		device.setModifier(user.getUserId());
		device.setModifierTime(new Date());
		int i = service.modifyDeviceInfo(device);
		if (i != 1) {
			log.error("修改设备[" + device.getDeviceName() + "]失败, 返回结果[" + i + "]");
			return new AjaxReponse(i, "MODIFY FAILED");
		} else {
			if(log.isInfoEnabled())
				log.info("修改设备[" + device.getDeviceName() + "]成功");
			return new AjaxReponse(i, "MODIFY SUCCEED");
		}
		
	}
	
	/**
	 * 删除设备
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/remove")
	public AjaxReponse removeDevice(HttpServletRequest request, Integer id) {

		User user = null;
		Object obj = request.getSession()
				.getAttribute(org.ku8eye.Constants.USER_SESSION_KEY);
		if (obj != null) {
			user = (User) obj;
			if(log.isInfoEnabled())
				log.info("get login user:" + user.getUserId());
		}
		if (user == null) {
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(500, "PLEASE LOGIN");
		}
		
		int i = service.removeDeviceInfoByPk(id);
		if (i != 1) {
			log.error("删除设备[" + id + "]失败, 返回结果[" + i + "]");
			return new AjaxReponse(i, "REMOVE FAILED");
		} else {
			if(log.isInfoEnabled())
				log.info("删除设备[" + id + "]成功");
			return new AjaxReponse(i, "REMOVE SUCCEED");
		}
		
	}
	
	/**
	 * 查询设备信息
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/queryDevice")
	public GridData queryDevice(HttpServletRequest request, Integer id) {
		
		User user = null;
		Object obj = request.getSession().getAttribute(
				org.ku8eye.Constants.USER_SESSION_KEY);
		if (obj != null) {
			user = (User) obj;
			log.info("get login user:" + user.getUserId());
		}
		if (user == null) {
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		Device device = service.queryDeviceInfoByPk(id);
		if(log.isInfoEnabled())
			log.info("查询ID[" + device.getId() + "]设备信息");
		
		GridData grid = new GridData();
		grid.setData(device);
		
		return grid;
		
	}
	
	/**
	 * 查询全部设备信息列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryAllDevice")
	public GridData queryAllDevice(HttpServletRequest request) {
		
		User user = null;
		Object obj = request.getSession().getAttribute(
				org.ku8eye.Constants.USER_SESSION_KEY);
		if (obj != null) {
			user = (User) obj;
			log.info("get login user:" + user.getUserId());
		}
		if (user == null) {
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		List<Device> list = service.queryAllDeviceInfoList();
		if(log.isInfoEnabled())
			log.info("查询设备记录数[" + list.size() + "]");
		
		GridData grid = new GridData();
		grid.setData(list);
		
		return grid;
		
	}
	

}
