package org.ku8eye.mapping;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;
import org.ku8eye.domain.JenkinsOrderInfo;

public interface JenkinsOrderInfoMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table jenkins_info
	 * @mbg.generated  Mon Jun 26 10:43:13 CST 2017
	 */
	@Delete({ "delete from jenkins_info", "where id = #{id,jdbcType=INTEGER}" })
	int deleteByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table jenkins_info
	 * @mbg.generated  Mon Jun 26 10:43:13 CST 2017
	 */
	@Insert({ "insert into jenkins_info (id, jenkins_url, ", "jenkins_username, API_token, ", "job_name, job_status, ",
			"build_num, request_type, ", "LAST_UPDATED_TIME, password, ", "DCOS_USER_ID, DCOS_USER_ALIAS, ",
			"NOTE, order_id)", "values (#{id,jdbcType=INTEGER}, #{jenkinsUrl,jdbcType=VARCHAR}, ",
			"#{jenkinsUsername,jdbcType=VARCHAR}, #{apiToken,jdbcType=VARCHAR}, ",
			"#{jobName,jdbcType=VARCHAR}, #{jobStatus,jdbcType=TINYINT}, ",
			"#{buildNum,jdbcType=INTEGER}, #{requestType,jdbcType=TINYINT}, ",
			"#{lastUpdatedTime,jdbcType=TIMESTAMP}, #{password,jdbcType=VARCHAR}, ",
			"#{dcosUserId,jdbcType=VARCHAR}, #{dcosUserAlias,jdbcType=VARCHAR}, ",
			"#{note,jdbcType=VARCHAR}, #{orderId,jdbcType=INTEGER})" })
	int insert(JenkinsOrderInfo record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table jenkins_info
	 * @mbg.generated  Mon Jun 26 10:43:13 CST 2017
	 */
	@Select({ "select", "id, jenkins_url, jenkins_username, API_token, job_name, job_status, build_num, ",
			"request_type, LAST_UPDATED_TIME, password, DCOS_USER_ID, DCOS_USER_ALIAS, NOTE, ", "order_id",
			"from jenkins_info", "where id = #{id,jdbcType=INTEGER}" })
	@Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true),
			@Result(column = "jenkins_url", property = "jenkinsUrl", jdbcType = JdbcType.VARCHAR),
			@Result(column = "jenkins_username", property = "jenkinsUsername", jdbcType = JdbcType.VARCHAR),
			@Result(column = "API_token", property = "apiToken", jdbcType = JdbcType.VARCHAR),
			@Result(column = "job_name", property = "jobName", jdbcType = JdbcType.VARCHAR),
			@Result(column = "job_status", property = "jobStatus", jdbcType = JdbcType.TINYINT),
			@Result(column = "build_num", property = "buildNum", jdbcType = JdbcType.INTEGER),
			@Result(column = "request_type", property = "requestType", jdbcType = JdbcType.TINYINT),
			@Result(column = "LAST_UPDATED_TIME", property = "lastUpdatedTime", jdbcType = JdbcType.TIMESTAMP),
			@Result(column = "password", property = "password", jdbcType = JdbcType.VARCHAR),
			@Result(column = "DCOS_USER_ID", property = "dcosUserId", jdbcType = JdbcType.VARCHAR),
			@Result(column = "DCOS_USER_ALIAS", property = "dcosUserAlias", jdbcType = JdbcType.VARCHAR),
			@Result(column = "NOTE", property = "note", jdbcType = JdbcType.VARCHAR),
			@Result(column = "order_id", property = "orderId", jdbcType = JdbcType.INTEGER) })
	JenkinsOrderInfo selectByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table jenkins_info
	 * @mbg.generated  Mon Jun 26 10:43:13 CST 2017
	 */
	@Select({ "select", "id, jenkins_url, jenkins_username, API_token, job_name, job_status, build_num, ",
			"request_type, LAST_UPDATED_TIME, password, DCOS_USER_ID, DCOS_USER_ALIAS, NOTE, ", "order_id",
			"from jenkins_info" })
	@Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true),
			@Result(column = "jenkins_url", property = "jenkinsUrl", jdbcType = JdbcType.VARCHAR),
			@Result(column = "jenkins_username", property = "jenkinsUsername", jdbcType = JdbcType.VARCHAR),
			@Result(column = "API_token", property = "apiToken", jdbcType = JdbcType.VARCHAR),
			@Result(column = "job_name", property = "jobName", jdbcType = JdbcType.VARCHAR),
			@Result(column = "job_status", property = "jobStatus", jdbcType = JdbcType.TINYINT),
			@Result(column = "build_num", property = "buildNum", jdbcType = JdbcType.INTEGER),
			@Result(column = "request_type", property = "requestType", jdbcType = JdbcType.TINYINT),
			@Result(column = "LAST_UPDATED_TIME", property = "lastUpdatedTime", jdbcType = JdbcType.TIMESTAMP),
			@Result(column = "password", property = "password", jdbcType = JdbcType.VARCHAR),
			@Result(column = "DCOS_USER_ID", property = "dcosUserId", jdbcType = JdbcType.VARCHAR),
			@Result(column = "DCOS_USER_ALIAS", property = "dcosUserAlias", jdbcType = JdbcType.VARCHAR),
			@Result(column = "NOTE", property = "note", jdbcType = JdbcType.VARCHAR),
			@Result(column = "order_id", property = "orderId", jdbcType = JdbcType.INTEGER) })
	List<JenkinsOrderInfo> selectAll();

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table jenkins_info
	 * @mbg.generated  Mon Jun 26 10:43:13 CST 2017
	 */
	@Update({ "update jenkins_info", "set jenkins_url = #{jenkinsUrl,jdbcType=VARCHAR},",
			"jenkins_username = #{jenkinsUsername,jdbcType=VARCHAR},", "API_token = #{apiToken,jdbcType=VARCHAR},",
			"job_name = #{jobName,jdbcType=VARCHAR},", "job_status = #{jobStatus,jdbcType=TINYINT},",
			"build_num = #{buildNum,jdbcType=INTEGER},", "request_type = #{requestType,jdbcType=TINYINT},",
			"LAST_UPDATED_TIME = #{lastUpdatedTime,jdbcType=TIMESTAMP},", "password = #{password,jdbcType=VARCHAR},",
			"DCOS_USER_ID = #{dcosUserId,jdbcType=VARCHAR},", "DCOS_USER_ALIAS = #{dcosUserAlias,jdbcType=VARCHAR},",
			"NOTE = #{note,jdbcType=VARCHAR},", "order_id = #{orderId,jdbcType=INTEGER}",
			"where id = #{id,jdbcType=INTEGER}" })
	int updateByPrimaryKey(JenkinsOrderInfo record);
}