package com.iiot.mqtt.client;

public class MQTTPersist {

	private String topic;
	private String host;
	private String data;
	
	public MQTTPersist(String topic, String host) {
		this.topic = topic;
		this.host = host;
	}
	
	public MQTTPersist(String topic, String host, String data) {
		this.topic = topic;
		this.host = host;
		this.data = data;
	}
	
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
}
