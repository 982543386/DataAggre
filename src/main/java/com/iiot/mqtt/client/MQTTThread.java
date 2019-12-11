package com.iiot.mqtt.client;

public class MQTTThread {

	private String thread_id;
	private String mqtt_topic;
	private String mqtt_host;
	
	public String getThread_id() {
		return thread_id;
	}
	public void setThread_id(String thread_id) {
		this.thread_id = thread_id;
	}
	public String getMqtt_topic() {
		return mqtt_topic;
	}
	public void setMqtt_topic(String mqtt_topic) {
		this.mqtt_topic = mqtt_topic;
	}
	public String getMqtt_host() {
		return mqtt_host;
	}
	public void setMqtt_host(String mqtt_host) {
		this.mqtt_host = mqtt_host;
	}
	
}
