package com.iiot.mqtt.client;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.iiot.influxdb.DataToDB;

public class MQTTRecieverCallback implements MqttCallback{

	// 连接丢失后，一般在这里面进行重连
	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		System.out.println("WARNING: "+arg0.getMessage());
	}

	// 数据传输接收是否完毕
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		System.out.println("deliveryComplete---------" + token.isComplete());
	}

	// 数据接收到在此处理
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// TODO Auto-generated method stub
		String line = new String(message.getPayload());
		if(line.equals("close")) return;
		DataToDB toDB = new DataToDB();
		//风机测试数据入库
		//toDB.windMachine(line, topic);
		//研华数据入库
		toDB.yanhuaData(line, topic);
		System.out.println("入库消息内容 : " + line);
	}

}
