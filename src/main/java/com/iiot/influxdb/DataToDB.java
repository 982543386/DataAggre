package com.iiot.influxdb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.influxdb.InfluxDB;

import com.alibaba.fastjson.JSONObject;

public class DataToDB {
	
	//不同topic对应messurement，构建tag，执行各类数据入库函数
	private InfluxDBClient client;

	//cyfdata风机测试数据入库
	public void windMachine(String line, String topic) {
		
		Map<Integer, String> index = new HashMap<>();
		index.put(1, "wind_speed");
		index.put(2, "generator_speed");
		index.put(3, "power");
		index.put(4, "wind_direction");
		index.put(5, "wind_direction_mean");
		index.put(6, "yaw_position");
		index.put(7, "yaw_speed");
		index.put(8, "pitch1_angle");
		index.put(9, "pitch2_angle");
		index.put(10, "pitch3_angle");
		//三连0
		index.put(11, "pitch1_speed");
		index.put(12, "pitch2_speed");
		index.put(13, "pitch3_speed");
		//三连2位小数
		index.put(14, "pitch1_moto_tmp");
		index.put(15, "pitch2_moto_tmp");
		index.put(16, "pitch3_moto_tmp");
		index.put(17, "acc_x");
		index.put(18, "acc_y");
		index.put(19, "environment_tmp");
		index.put(20, "int_tmp");
		index.put(21, "pitch1_ng5_tmp");
		//2位小数
		index.put(22, "pitch2_ng5_tmp");
		index.put(23, "pitch3_ng5_tmp");
		//三连2位小数
		index.put(24, "pitch1_ng5_DC");
		index.put(25, "pitch2_ng5_DC");
		index.put(26, "pitch3_ng5_DC");
		//string tag
		index.put(27, "group");
		
		try {
			client = new InfluxDBClient();
			InfluxDB influxDB = client.getInfluxDB();
			
			String[] split = line.split(",");
			Map<String, String> tags = new HashMap<>();
		    Map<String, Object> fields = new HashMap<>();
		    for(int i=1;i<split.length;i++) {
		    	int itemLen = split[i].length();
		    	String item = "";
		    	if(i==split.length-1) {
		    		item = split[i].substring(1, itemLen-2);
		    		tags.put(index.get(i), item);
		    	}else {
		    		item = split[i].substring(1, itemLen-1);
		    		fields.put(index.get(i), Double.valueOf(item));
		    	}
		    }
		    client.insert(influxDB, tags, fields, topic, System.currentTimeMillis());
		    
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	//研华json数据入influxdb，字段自动化识别
	public void yanhuaData(String line, String topic) {
		Map<String, String> tags = new HashMap<>();
	    Map<String, Object> fields = new HashMap<>();
		JSONObject jsonObject = JSONObject.parseObject(line);
		//2019-08-13T03:18:33+0000
		String time = (String) jsonObject.get("ts");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
		long timestamp = 0L;
		try {
			Date result = df.parse(time);
			//ms
			timestamp = result.getTime();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		int dataSize = jsonObject.getJSONArray("d").size();
		for(int i=0;i<dataSize;i++) {
			JSONObject item = jsonObject.getJSONArray("d").getJSONObject(i);
			String tag = item.getString("tag");
			Object value = item.get("value");
			fields.put(tag, value);
		}
		try {
			client = new InfluxDBClient();
			InfluxDB influxDB = client.getInfluxDB();
			System.out.println(timestamp);
			client.insert(influxDB, tags, fields, topic, timestamp);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
