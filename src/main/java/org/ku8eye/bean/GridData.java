package org.ku8eye.bean;


public class GridData {
	public GridData(){}
	
	public GridData(Object obj){
		this.data =obj;
	}
	private Object data;
	
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
}
