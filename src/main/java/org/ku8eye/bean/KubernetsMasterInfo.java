package org.ku8eye.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;

public class KubernetsMasterInfo {

	public KubernetsMasterInfo(String v1, String v2, String v3, int v4) {
		this.loginName = v1;
		this.loginPass = v2;
		this.host = v3;
		this.port = v4;
	}

	private String loginName;
	private String loginPass;
	private String host;
	private int port;

	public String getLoginName() {
		return loginName;
	}

	public String getLoginPass() {
		return loginPass;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {

		return port;
	}

}
