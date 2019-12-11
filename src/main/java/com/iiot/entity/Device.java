/**
 * 
 */
package com.iiot.entity;

import java.util.Date;

/**
 * 设备entity
 * @author zhaich5
 *
 */
public class Device {
	
	/** Id */
	private Integer id;

	/** 设备编码 */
	private String deviceEncode;

	/** 设备名称 */
	private String deviceName;

	/** Topic名称 */
	private String topicNanme;

	/** 设备状态代码，0：停止的；1：运行的 */
	private String deviceStatusCode;

	/** 节点ip */
	private String nodeIp;

	/** 节点名称 */
	private String hostname;

	/** 描述 */
	private String description;

	/** 创建者 */
	private String creater;

	/** 创建时间  */
	private Date createTime;

	/** 修改者 */
	private String modifier;

	/** 修改时间 */
	private Date modifierTime;

	/** 时间戳 */
	private Date timestamp;

	/** 版本号 */
	private Integer vno;

	/** 备注 */
	private String remark;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDeviceEncode() {
		return deviceEncode;
	}

	public void setDeviceEncode(String deviceEncode) {
		this.deviceEncode = deviceEncode;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getTopicNanme() {
		return topicNanme;
	}

	public void setTopicNanme(String topicNanme) {
		this.topicNanme = topicNanme;
	}

	public String getDeviceStatusCode() {
		return deviceStatusCode;
	}

	public void setDeviceStatusCode(String deviceStatusCode) {
		this.deviceStatusCode = deviceStatusCode;
	}

	public String getNodeIp() {
		return nodeIp;
	}

	public void setNodeIp(String nodeIp) {
		this.nodeIp = nodeIp;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public Date getModifierTime() {
		return modifierTime;
	}

	public void setModifierTime(Date modifierTime) {
		this.modifierTime = modifierTime;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getVno() {
		return vno;
	}

	public void setVno(Integer vno) {
		this.vno = vno;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
