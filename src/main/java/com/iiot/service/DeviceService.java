/**
 * 
 */
package com.iiot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.iiot.dao.IiotDeviceInfoMapper;
import com.iiot.domain.IiotDeviceInfo;
import com.iiot.entity.Device;

/**
 * 设备管理Service
 * @author zhaich5
 *
 */
@Service
public class DeviceService {

	@Autowired
	private IiotDeviceInfoMapper mapper;
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int saveDeviceInfo(Device device) {
		IiotDeviceInfo info = new IiotDeviceInfo();
		BeanUtils.copyProperties(device, info);
		return mapper.insert(info);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int modifyDeviceInfo(Device device) {
		IiotDeviceInfo info = mapper.selectByPrimaryKey(device.getId());
		info.setDeviceStatusCode(device.getDeviceStatusCode());
		info.setNodeIp(device.getNodeIp());
		info.setDescription(device.getDescription());
		info.setModifier(device.getModifier());
		info.setModifierTime(device.getModifierTime());
		return mapper.updateByPrimaryKey(info);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int removeDeviceInfoByPk(Integer id) {
		return mapper.deleteByPrimaryKey(id);
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Device> queryAllDeviceInfoList() {
		List<IiotDeviceInfo> resultList = mapper.selectAll();
		List<Device> deviceList = new ArrayList<Device>();
		for (IiotDeviceInfo info : resultList) {
			Device device = new Device();
			BeanUtils.copyProperties(info, device);
			deviceList.add(device);
		}
		return deviceList;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Device queryDeviceInfoByPk(Integer id) {
		IiotDeviceInfo result = mapper.selectByPrimaryKey(id);
		Device device = new Device();
		BeanUtils.copyProperties(result, device);
		return device;
	}
	
}
