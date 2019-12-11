package org.ku8eye.bean.monitor;

import org.ku8eye.util.AjaxReponse;

public class MonitorAjaxReponse extends AjaxReponse {
	
	private Object data;

	public MonitorAjaxReponse(int status, String message) {
		super(status, message);
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
