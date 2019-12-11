package org.ku8eye.bean.monitor;

import java.io.Serializable;

public class SystemEvent implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String firstSeen;
	private String lastSeen;
	private String count;
	private String name;
	private String kind;
	private String subObject;
	private String reason;
	private String source;
	private String message;
	
	public String getFirstSeen() {
		return firstSeen;
	}
	public void setFirstSeen(String firstSeen) {
		this.firstSeen = firstSeen;
	}
	public String getLastSeen() {
		return lastSeen;
	}
	public void setLastSeen(String lastSeen) {
		this.lastSeen = lastSeen;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getSubObject() {
		return subObject;
	}
	public void setSubObject(String subObject) {
		this.subObject = subObject;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString(){
		return "SystemEvent [firstSeen=" + firstSeen + ", lastSeen=" + lastSeen + ", count=" + count + ", name=" + name + ", kind=" + kind + ", subObject=" + subObject + ", reason=" + reason + ", source=" + source + ", message=" + message + "]";
	}
}
