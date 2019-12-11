package org.ku8eye.bean.task;

import java.util.Date;

public class TaskInfo {

	private Byte status;//0-未执行，1-正在运行，2-正常结束，-1-异常结束
	private Date lastTime;//上次运行时间
	private Date nextTime;//下次运行时间
	
	public Byte getStatus() {
		return status;
	}
	public void setStatus(Byte status) {
		this.status = status;
	}
	public Date getLastTime() {
		return lastTime;
	}
	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}
	public Date getNextTime() {
		return nextTime;
	}
	public void setNextTime(Date nextTime) {
		this.nextTime = nextTime;
	}


}
