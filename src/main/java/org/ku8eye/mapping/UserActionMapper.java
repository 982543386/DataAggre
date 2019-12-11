package org.ku8eye.mapping;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;
import org.ku8eye.domain.UserAction;


public interface UserActionMapper {

	@Delete({ "delete from user_action", "where uri=#{0}" })
	int deleteByPrimaryKey( String uri);

	@Insert({
			"insert into user_action ( user_type ,",
			"uri, note,class)",
			"values ( #{userType,jdbcType=INTEGER}, ",
			"#{uri,jdbcType=VARCHAR}, #{desc,jdbcType=VARCHAR}, #{_class,jdbcType=VARCHAR})" })
	int insertUserAction(UserAction ua);

	@Select({ "select", "user_type,uri, note, class", "from user_action" })
	@Results({
			@Result(column = "user_type", property = "userType", jdbcType = JdbcType.VARCHAR),
			@Result(column = "uri", property = "uri", jdbcType = JdbcType.VARCHAR),
			@Result(column = "note", property = "desc", jdbcType = JdbcType.VARCHAR),
			@Result(column = "class", property = "_class", jdbcType = JdbcType.VARCHAR) })
	List<UserAction> selectALL();

	@Select({ "select user_type,uri, note, class from user_action where find_in_set(#{0},user_type)"})
	@Results({
			@Result(column = "user_type", property = "userType", jdbcType = JdbcType.VARCHAR),
			@Result(column = "uri", property = "uri", jdbcType = JdbcType.VARCHAR),
			@Result(column = "note", property = "desc", jdbcType = JdbcType.VARCHAR),
			@Result(column = "class", property = "_class", jdbcType = JdbcType.VARCHAR) })
	List<UserAction> selectByUserType(String userType);
	
	
	
	@Select({ "select user_type,uri, note, class from user_action where uri=#{0}"})
	@Results({
			@Result(column = "user_type", property = "userType", jdbcType = JdbcType.VARCHAR),
			@Result(column = "uri", property = "uri", jdbcType = JdbcType.VARCHAR),
			@Result(column = "note", property = "desc", jdbcType = JdbcType.VARCHAR),
			@Result(column = "class", property = "_class", jdbcType = JdbcType.VARCHAR) })
	UserAction selectByPk(String uri);
	
}
