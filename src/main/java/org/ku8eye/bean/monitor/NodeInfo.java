package org.ku8eye.bean.monitor;

public class NodeInfo {

	public String NodeIP="";
	public String Health="0";
	public String Role="0";
	public ResourceUsage resourceUsage= new ResourceUsage();
	public String getNodeIP() {
		return NodeIP;
	}
	public void setNodeIP(String nodeIP) {
		NodeIP = nodeIP;
	}
	public String getHealth() {
		return Health;
	}
	public void setHealth(String health) {
		Health = health;
	}
	public String getRole() {
		return Role;
	}
	public void setRole(String role) {
		Role = role;
	}
	public ResourceUsage getResourceUsage() {
		return resourceUsage;
	}
	public void setResourceUsage(ResourceUsage resourceUsage) {
		this.resourceUsage = resourceUsage;
	}
	
	
	
}
