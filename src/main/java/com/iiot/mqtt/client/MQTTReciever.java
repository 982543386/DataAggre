package com.iiot.mqtt.client;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiot.dao.EdgeNodeDao;

@Service
public class MQTTReciever {

	@Autowired
	private EdgeNodeDao edgeNodeDao;
	
	private MqttClient client;  
	private MqttConnectOptions options; 
	private String clientId = "";
	
	public void start(String host, String topic, String username, String password){
		
		try {
			clientId = "client_"+topic;
			//连接客户端配置：host样例："tcp://127.0.0.1:1883"，client唯一确定
			client = new MqttClient(host, clientId);
			//MQTT的连接设置
			options = new MqttConnectOptions();
			// 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，设置为true表示每次连接到服务器都以新的身份连接  
	        options.setCleanSession(false);
	        
	        if(!password.equals("")) {
	        	// 设置连接的用户名  
		        options.setUserName(username);  
		        // 设置连接的密码  
		        options.setPassword(password.toCharArray());
	        }
	        
	        // 设置超时时间 单位为秒  
	        options.setConnectionTimeout(10);
	        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制  
	        options.setKeepAliveInterval(20);
	        // 设置连接断开遗愿
	        //options.setWill(topic, "last stop here".getBytes(), 0, true);
	        // 设置回调
	        client.setCallback(new MQTTRecieverCallback());
	        
	        client.connect(options);
	        
	        int[] Qos  = {1};  
	        String[] topic1 = {topic};
	        client.subscribe(topic1, Qos);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        
	}
	
	public void stop(String host, String topic, String username, String password) {
		try {
			//同id client尝试连接会断开原连接
			clientId = "client_"+topic;
			client = new MqttClient(host, clientId);
			client.connect();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void testStart(String host, String topic, String username, String password) {
		try {
			clientId = "client_"+topic;
			client = new MqttClient(host, clientId);
			options = new MqttConnectOptions();
			options.setCleanSession(false);
			if(!password.equals("")) {
		        options.setUserName(username);  
		        options.setPassword(password.toCharArray());
	        }
			client.setCallback(new MqttCallback() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					// TODO Auto-generated method stub
					String line = new String(message.getPayload());
					if(line.equals("close")) return;
					try {
						MQTTPersist mqttPersist = new MQTTPersist(topic, host, line);
						System.out.println(mqttPersist.getHost()+"||"+mqttPersist.getTopic()+"||"+mqttPersist.getData());
						System.out.println(edgeNodeDao);
						edgeNodeDao.insertMQTT(mqttPersist);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
				
				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {
					// TODO Auto-generated method stub
					System.out.println("deliveryComplete---------" + token.isComplete());
				}
				
				@Override
				public void connectionLost(Throwable cause) {
					// TODO Auto-generated method stub
					System.out.println("WARNING: "+cause.getMessage());
				}
			});
			client.connect(options);
	        int[] Qos  = {1};  
	        String[] topic1 = {topic};
	        client.subscribe(topic1, Qos);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
