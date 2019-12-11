package org.ku8eye.bean.monitor;

import java.io.Serializable;
import java.util.Arrays;

public class HisttoryResourceUsage implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String[] time;
	private double[] value;
	private double limit;
	private String name;
	
	public String[] getTime() {
		return time;
	}

	public void setTime(String[] time) {
		this.time = time;
	}

	public double[] getValue() {
		return value;
	}

	public void setValue(double[] value) {
		this.value = value;
	}

	public double getLimit() {
		return limit;
	}

	public void setLimit(double limit) {
		this.limit = limit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString(){
		return "HisttoryResourceUsage [time=" + Arrays.toString(time) + ", value=" + Arrays.toString(value) + ", limit=" + limit + ", name=" + name + "]";
	}
}
