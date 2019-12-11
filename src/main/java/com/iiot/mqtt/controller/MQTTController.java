package com.iiot.mqtt.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iiot.dao.EdgeNodeDao;
import com.iiot.edgenode.entity.Message;
import com.iiot.mqtt.client.MQTTPersist;
import com.iiot.mqtt.client.MQTTReciever;

@RestController
@RequestMapping("/mqtt")
public class MQTTController {
	
	@Autowired
	private EdgeNodeDao edgeNodeDao;
	@Autowired
	private MQTTReciever mqttReciever;
	
	@RequestMapping("/startRecv")
	public Message startRecvMQTT(HttpServletRequest request) {
		Message message = new Message();
		String host = "tcp://"+request.getParameter("ip")+":1883";
		String topic = request.getParameter("topic");
		
		try {
			//MQTTReciever mqttReciever = new MQTTReciever();
			
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mqttReciever.start(host, topic, "", "");
				}
			};
			Thread thread = new Thread(runnable);
	        thread.start();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return message;
	}
	
	@RequestMapping("/stopRecv")
	public Message stopRecvMQTT(HttpServletRequest request) {
		Message message = new Message();
		String host = "tcp://"+request.getParameter("ip")+":1883";
		String topic = request.getParameter("topic");
		try {
			//MQTTReciever mqttReciever = new MQTTReciever();
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mqttReciever.stop(host, topic, "", "");
				}
			};
			Thread thread = new Thread(runnable);
	        thread.start();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return message;
	}
	
	@RequestMapping("/testRecv")
	public Message testRecvMQTT(HttpServletRequest request) {
		Message message = new Message();
		String host = "tcp://"+request.getParameter("ip")+":1883";
		String topic = request.getParameter("topic");
		try {
			//MQTTReciever mqttReciever = new MQTTReciever();
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mqttReciever.testStart(host, topic, "", "");
				}
			};
			Thread thread = new Thread(runnable);
	        thread.start();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return message;
	}
	
	@RequestMapping("/stopTest")
	public Message stopRecvTest(HttpServletRequest request) {
		Message message = new Message();
		String host = "tcp://"+request.getParameter("ip")+":1883";
		String topic = request.getParameter("topic");
		try {
			//MQTTReciever mqttReciever = new MQTTReciever();
			mqttReciever.stop(host, topic, "", "");
			
			MQTTPersist mqttPersist = new MQTTPersist(topic, host);
			List<MQTTPersist> list = edgeNodeDao.selectMQTT(mqttPersist);
			for(MQTTPersist item: list) {
				System.out.println("data: "+item.getData());
			}
			message.setFlag(true);
			message.setResult(list);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			message.setFlag(false);
			message.setMsg("error");
		}
		return message;
	}
	
	@RequestMapping("/clearDB")
	public Message clearDB(HttpServletRequest request) {
		Message message = new Message();
		String host = "tcp://"+request.getParameter("ip")+":1883";
		String topic = request.getParameter("topic");
		try {
			MQTTPersist mqttPersist = new MQTTPersist(topic, host);
			edgeNodeDao.deleteMQTT(mqttPersist);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return message;
	}
	
}
