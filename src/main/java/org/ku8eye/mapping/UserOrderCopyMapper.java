package org.ku8eye.mapping;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;
import org.ku8eye.domain.UserOrderCopy;

public interface UserOrderCopyMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_order_copy
     *
     * @mbg.generated Tue Jun 06 15:42:36 CST 2017
     */
    @Delete({
        "delete from user_order_copy",
        "where order_id = #{orderId,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer orderId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_order_copy
     *
     * @mbg.generated Tue Jun 06 15:42:36 CST 2017
     */
    @Insert({
        "insert into user_order_copy (order_id, order_no, ",
        "tenant_id, user_id, ",
        "status, create_time, ",
        "update_time, remark, ",
        "start_time, end_time, ",
        "zone_id, order_type)",
        "values (#{orderId,jdbcType=INTEGER}, #{orderNo,jdbcType=VARCHAR}, ",
        "#{tenantId,jdbcType=INTEGER}, #{userId,jdbcType=VARCHAR}, ",
        "#{status,jdbcType=TINYINT}, #{createTime,jdbcType=TIMESTAMP}, ",
        "#{updateTime,jdbcType=TIMESTAMP}, #{remark,jdbcType=VARCHAR}, ",
        "#{startTime,jdbcType=TIMESTAMP}, #{endTime,jdbcType=TIMESTAMP}, ",
        "#{zoneId,jdbcType=INTEGER}, #{orderType,jdbcType=INTEGER})"
    })
    int insert(UserOrderCopy record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_order_copy
     *
     * @mbg.generated Tue Jun 06 15:42:36 CST 2017
     */
    @Select({
        "select",
        "order_id, order_no, tenant_id, user_id, status, create_time, update_time, remark, ",
        "start_time, end_time, zone_id, order_type",
        "from user_order_copy",
        "where order_id = #{orderId,jdbcType=INTEGER}"
    })
    @Results({
        @Result(column="order_id", property="orderId", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="order_no", property="orderNo", jdbcType=JdbcType.VARCHAR),
        @Result(column="tenant_id", property="tenantId", jdbcType=JdbcType.INTEGER),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="status", property="status", jdbcType=JdbcType.TINYINT),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="update_time", property="updateTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="remark", property="remark", jdbcType=JdbcType.VARCHAR),
        @Result(column="start_time", property="startTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="end_time", property="endTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="zone_id", property="zoneId", jdbcType=JdbcType.INTEGER),
        @Result(column="order_type", property="orderType", jdbcType=JdbcType.INTEGER)
    })
    UserOrderCopy selectByPrimaryKey(Integer orderId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_order_copy
     *
     * @mbg.generated Tue Jun 06 15:42:36 CST 2017
     */
    @Select({
        "select",
        "order_id, order_no, tenant_id, user_id, status, create_time, update_time, remark, ",
        "start_time, end_time, zone_id, order_type",
        "from user_order_copy"
    })
    @Results({
        @Result(column="order_id", property="orderId", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="order_no", property="orderNo", jdbcType=JdbcType.VARCHAR),
        @Result(column="tenant_id", property="tenantId", jdbcType=JdbcType.INTEGER),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="status", property="status", jdbcType=JdbcType.TINYINT),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="update_time", property="updateTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="remark", property="remark", jdbcType=JdbcType.VARCHAR),
        @Result(column="start_time", property="startTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="end_time", property="endTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="zone_id", property="zoneId", jdbcType=JdbcType.INTEGER),
        @Result(column="order_type", property="orderType", jdbcType=JdbcType.INTEGER)
    })
    List<UserOrderCopy> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_order_copy
     *
     * @mbg.generated Tue Jun 06 15:42:36 CST 2017
     */
    @Update({
        "update user_order_copy",
        "set order_no = #{orderNo,jdbcType=VARCHAR},",
          "tenant_id = #{tenantId,jdbcType=INTEGER},",
          "user_id = #{userId,jdbcType=VARCHAR},",
          "status = #{status,jdbcType=TINYINT},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP},",
          "remark = #{remark,jdbcType=VARCHAR},",
          "start_time = #{startTime,jdbcType=TIMESTAMP},",
          "end_time = #{endTime,jdbcType=TIMESTAMP},",
          "zone_id = #{zoneId,jdbcType=INTEGER},",
          "order_type = #{orderType,jdbcType=INTEGER}",
        "where order_id = #{orderId,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(UserOrderCopy record);
}