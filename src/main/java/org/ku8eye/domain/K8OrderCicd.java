package org.ku8eye.domain;

import java.util.Date;

public class K8OrderCicd {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.id
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private Integer id;
    
    /**
    *
    * This field was generated by MyBatis Generator.
    * This field corresponds to the database column k8order.status
    *
    * @mbg.generated Mon Jul 31 15:25:22 CST 2017
    */
   private Integer status;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.user_id
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private String userId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.war_name
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private String warName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.war_location
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private String warLocation;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.war_url
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private String warUrl;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.zone
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private Integer zone;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.repo_name
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private String repoName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.source_type
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private Integer sourceType;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.source_url
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private String sourceUrl;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.source_username
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private String sourceUsername;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.source_repo
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private String sourceRepo;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.source_password
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private String sourcePassword;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.source_token
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private String sourceToken;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.deliver_type
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private Integer deliverType;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.cluster_count
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private Integer clusterCount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.resource_type
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private Integer resourceType;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.storage_type
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private Integer storageType;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.disk_space
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private Integer diskSpace;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.database_name
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private String databaseName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.jenkins_url
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private String jenkinsUrl;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.jenkins_action
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private String jenkinsAction;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.remark
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private String remark;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.security
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private String security;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.k8order_id
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private String k8orderId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.remark_admin
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private String remarkAdmin;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_cicd.cd_type
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    private Integer cdType;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.id
     *
     * @return the value of k8order_cicd.id
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    
    private Date startTime;
    
    public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.id
     *
     * @param id the value for k8order_cicd.id
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.user_id
     *
     * @return the value of k8order_cicd.user_id
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public String getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.user_id
     *
     * @param userId the value for k8order_cicd.user_id
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.war_name
     *
     * @return the value of k8order_cicd.war_name
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public String getWarName() {
        return warName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.war_name
     *
     * @param warName the value for k8order_cicd.war_name
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setWarName(String warName) {
        this.warName = warName == null ? null : warName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.war_location
     *
     * @return the value of k8order_cicd.war_location
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public String getWarLocation() {
        return warLocation;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.war_location
     *
     * @param warLocation the value for k8order_cicd.war_location
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setWarLocation(String warLocation) {
        this.warLocation = warLocation == null ? null : warLocation.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.war_url
     *
     * @return the value of k8order_cicd.war_url
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public String getWarUrl() {
        return warUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.war_url
     *
     * @param warUrl the value for k8order_cicd.war_url
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setWarUrl(String warUrl) {
        this.warUrl = warUrl == null ? null : warUrl.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.zone
     *
     * @return the value of k8order_cicd.zone
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public Integer getZone() {
        return zone;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.zone
     *
     * @param zone the value for k8order_cicd.zone
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setZone(Integer zone) {
        this.zone = zone;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.repo_name
     *
     * @return the value of k8order_cicd.repo_name
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public String getRepoName() {
        return repoName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.repo_name
     *
     * @param repoName the value for k8order_cicd.repo_name
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setRepoName(String repoName) {
        this.repoName = repoName == null ? null : repoName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.source_type
     *
     * @return the value of k8order_cicd.source_type
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public Integer getSourceType() {
        return sourceType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.source_type
     *
     * @param sourceType the value for k8order_cicd.source_type
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.source_url
     *
     * @return the value of k8order_cicd.source_url
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public String getSourceUrl() {
        return sourceUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.source_url
     *
     * @param sourceUrl the value for k8order_cicd.source_url
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl == null ? null : sourceUrl.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.source_username
     *
     * @return the value of k8order_cicd.source_username
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public String getSourceUsername() {
        return sourceUsername;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.source_username
     *
     * @param sourceUsername the value for k8order_cicd.source_username
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setSourceUsername(String sourceUsername) {
        this.sourceUsername = sourceUsername == null ? null : sourceUsername.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.source_repo
     *
     * @return the value of k8order_cicd.source_repo
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public String getSourceRepo() {
        return sourceRepo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.source_repo
     *
     * @param sourceRepo the value for k8order_cicd.source_repo
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setSourceRepo(String sourceRepo) {
        this.sourceRepo = sourceRepo == null ? null : sourceRepo.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.source_password
     *
     * @return the value of k8order_cicd.source_password
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public String getSourcePassword() {
        return sourcePassword;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.source_password
     *
     * @param sourcePassword the value for k8order_cicd.source_password
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setSourcePassword(String sourcePassword) {
        this.sourcePassword = sourcePassword == null ? null : sourcePassword.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.source_token
     *
     * @return the value of k8order_cicd.source_token
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public String getSourceToken() {
        return sourceToken;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.source_token
     *
     * @param sourceToken the value for k8order_cicd.source_token
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setSourceToken(String sourceToken) {
        this.sourceToken = sourceToken == null ? null : sourceToken.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.deliver_type
     *
     * @return the value of k8order_cicd.deliver_type
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public Integer getDeliverType() {
        return deliverType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.deliver_type
     *
     * @param deliverType the value for k8order_cicd.deliver_type
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setDeliverType(Integer deliverType) {
        this.deliverType = deliverType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.cluster_count
     *
     * @return the value of k8order_cicd.cluster_count
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public Integer getClusterCount() {
        return clusterCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.cluster_count
     *
     * @param clusterCount the value for k8order_cicd.cluster_count
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setClusterCount(Integer clusterCount) {
        this.clusterCount = clusterCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.resource_type
     *
     * @return the value of k8order_cicd.resource_type
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public Integer getResourceType() {
        return resourceType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.resource_type
     *
     * @param resourceType the value for k8order_cicd.resource_type
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.storage_type
     *
     * @return the value of k8order_cicd.storage_type
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public Integer getStorageType() {
        return storageType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.storage_type
     *
     * @param storageType the value for k8order_cicd.storage_type
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setStorageType(Integer storageType) {
        this.storageType = storageType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.disk_space
     *
     * @return the value of k8order_cicd.disk_space
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public Integer getDiskSpace() {
        return diskSpace;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.disk_space
     *
     * @param diskSpace the value for k8order_cicd.disk_space
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setDiskSpace(Integer diskSpace) {
        this.diskSpace = diskSpace;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.database_name
     *
     * @return the value of k8order_cicd.database_name
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.database_name
     *
     * @param databaseName the value for k8order_cicd.database_name
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName == null ? null : databaseName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.jenkins_url
     *
     * @return the value of k8order_cicd.jenkins_url
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public String getJenkinsUrl() {
        return jenkinsUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.jenkins_url
     *
     * @param jenkinsUrl the value for k8order_cicd.jenkins_url
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setJenkinsUrl(String jenkinsUrl) {
        this.jenkinsUrl = jenkinsUrl == null ? null : jenkinsUrl.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.jenkins_action
     *
     * @return the value of k8order_cicd.jenkins_action
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public String getJenkinsAction() {
        return jenkinsAction;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.jenkins_action
     *
     * @param jenkinsAction the value for k8order_cicd.jenkins_action
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setJenkinsAction(String jenkinsAction) {
        this.jenkinsAction = jenkinsAction == null ? null : jenkinsAction.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.remark
     *
     * @return the value of k8order_cicd.remark
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public String getRemark() {
        return remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.remark
     *
     * @param remark the value for k8order_cicd.remark
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.security
     *
     * @return the value of k8order_cicd.security
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public String getSecurity() {
        return security;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.security
     *
     * @param security the value for k8order_cicd.security
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setSecurity(String security) {
        this.security = security == null ? null : security.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.k8order_id
     *
     * @return the value of k8order_cicd.k8order_id
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public String getK8orderId() {
        return k8orderId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.k8order_id
     *
     * @param k8orderId the value for k8order_cicd.k8order_id
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setK8orderId(String k8orderId) {
        this.k8orderId = k8orderId == null ? null : k8orderId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.remark_admin
     *
     * @return the value of k8order_cicd.remark_admin
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public String getRemarkAdmin() {
        return remarkAdmin;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.remark_admin
     *
     * @param remarkAdmin the value for k8order_cicd.remark_admin
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setRemarkAdmin(String remarkAdmin) {
        this.remarkAdmin = remarkAdmin == null ? null : remarkAdmin.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_cicd.cd_type
     *
     * @return the value of k8order_cicd.cd_type
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public Integer getCdType() {
        return cdType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_cicd.cd_type
     *
     * @param cdType the value for k8order_cicd.cd_type
     *
     * @mbg.generated Mon Jul 31 12:26:58 CST 2017
     */
    public void setCdType(Integer cdType) {
        this.cdType = cdType;
    }
    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order.status
     *
     * @return the value of k8order.status
     *
     * @mbg.generated Mon Jul 31 15:25:22 CST 2017
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order.status
     *
     * @param status the value for k8order.status
     *
     * @mbg.generated Mon Jul 31 15:25:22 CST 2017
     */
    public void setStatus(Integer status) {
        this.status = status;
    }
    
}