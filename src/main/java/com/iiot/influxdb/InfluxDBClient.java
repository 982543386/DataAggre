package com.iiot.influxdb;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;

public class InfluxDBClient {

	private static final String INFLUXDB_HOST = "http://10.1.131.26:8086";
	private static final String INFLUXDB_USERNAME = "admin";
	private static final String INFLUXDB_PASSWORD = "admin";
	private static final String INFLUXDB_DB = "my_test";
	
	public InfluxDB getInfluxDB(){
		InfluxDB influxDB = null;
		try {
			influxDB = InfluxDBFactory.connect(INFLUXDB_HOST, INFLUXDB_USERNAME, INFLUXDB_PASSWORD);
			influxDB.setConnectTimeout(30000, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return influxDB;
	}
	
	public void insert(InfluxDB influxDB, Map<String, String> tags, Map<String, Object> fields, String measurement, long timestamp){
		Builder builder = Point.measurement(measurement);
		//builder.time(timestamp, TimeUnit.MICROSECONDS);
		if(!tags.isEmpty()) {
			builder.tag(tags);
		}
		if(!fields.isEmpty()) {
			builder.fields(fields);
		}
		try {
			influxDB.write(INFLUXDB_DB, "", builder.build());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
