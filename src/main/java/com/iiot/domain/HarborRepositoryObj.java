package com.iiot.domain;


public class HarborRepositoryObj {
	
	private String projectId;
	private String projectName;
	private String projectPublic;
	private String pullCount;
	private String repositoryName;
	private String tagsCount;
	private String[] tags;
	
	public String[] getTags() {
		return tags;
	}
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getProjectPublic() {
		return projectPublic;
	}
	public void setProjectPublic(String projectPublic) {
		this.projectPublic = projectPublic;
	}
	public String getPullCount() {
		return pullCount;
	}
	public void setPullCount(String pullCount) {
		this.pullCount = pullCount;
	}
	public String getRepositoryName() {
		return repositoryName;
	}
	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
	public String getTagsCount() {
		return tagsCount;
	}
	public void setTagsCount(String tagsCount) {
		this.tagsCount = tagsCount;
	}

	

}
