package org.ku8eye.domain;

public class UserGroup {
	
	private int creat_user;
	public int getCreat_user() {
		return creat_user;
	}

	public void setCreat_user(int creat_user) {
		this.creat_user = creat_user;
	}
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_group.group_id
     *
     * @mbggenerated
     */
    private Integer groupId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_group.group_name
     *
     * @mbggenerated
     */
    private String groupName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_group.tenant_id
     *
     * @mbggenerated
     */
    private Integer tenantId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_group.ku8_parttion_ids
     *
     * @mbggenerated
     */
    private String ku8ParttionIds;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_group.group_id
     *
     * @return the value of user_group.group_id
     *
     * @mbggenerated
     */
    public Integer getGroupId() {
        return groupId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_group.group_id
     *
     * @param groupId the value for user_group.group_id
     *
     * @mbggenerated
     */
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_group.group_name
     *
     * @return the value of user_group.group_name
     *
     * @mbggenerated
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_group.group_name
     *
     * @param groupName the value for user_group.group_name
     *
     * @mbggenerated
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_group.tenant_id
     *
     * @return the value of user_group.tenant_id
     *
     * @mbggenerated
     */
    public Integer getTenantId() {
        return tenantId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_group.tenant_id
     *
     * @param tenantId the value for user_group.tenant_id
     *
     * @mbggenerated
     */
    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_group.ku8_parttion_ids
     *
     * @return the value of user_group.ku8_parttion_ids
     *
     * @mbggenerated
     */
    public String getKu8ParttionIds() {
        return ku8ParttionIds;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_group.ku8_parttion_ids
     *
     * @param ku8ParttionIds the value for user_group.ku8_parttion_ids
     *
     * @mbggenerated
     */
    public void setKu8ParttionIds(String ku8ParttionIds) {
        this.ku8ParttionIds = ku8ParttionIds;
    }
}