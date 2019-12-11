package org.ku8eye.bean.task;

public class TaskResponse {

	private int status;//0没问题。1有问题
	private String message;
	private Object data;

	public TaskResponse() {
		this(0);
	}
	
	public TaskResponse(int status) {
		this(status, null);
	}

	public TaskResponse(int status, String message) {
		this(status, message, null);
	}

	public TaskResponse(int status, String message, Object data) {
		this.setStatus(status);
		this.setMessage(message);
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
