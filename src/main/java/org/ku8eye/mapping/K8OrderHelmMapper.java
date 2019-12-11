package org.ku8eye.mapping;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;
import org.ku8eye.domain.K8OrderHelm;

public interface K8OrderHelmMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table k8order_helm
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    @Delete({
        "delete from k8order_helm",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table k8order_helm
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    @Insert({
        "insert into k8order_helm (id, k8order_id, ",
        "helm_name, user_id, res1, ",
        "k8order_type, res3, source, ",
        "version, charts, ",
        "settings, time, ",
        "status_helm)",
        "values (#{id,jdbcType=INTEGER}, #{k8orderId,jdbcType=VARCHAR}, ",
        "#{helmName,jdbcType=VARCHAR}, #{userId,jdbcType=VARCHAR}, #{res1,jdbcType=INTEGER}, ",
        "#{k8OrderType,jdbcType=VARCHAR}, #{res3,jdbcType=BIT}, #{source,jdbcType=VARCHAR}, ",
        "#{version,jdbcType=VARCHAR}, #{charts,jdbcType=VARCHAR}, ",
        "#{settings,jdbcType=VARCHAR}, #{time,jdbcType=TIMESTAMP}, ",
        "#{statusHelm,jdbcType=INTEGER})"
    })
    int insert(K8OrderHelm record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table k8order_helm
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    @Select({
        "select",
        "id, k8order_id, helm_name, user_id, res1, k8order_type, res3, source, version, charts, settings, ",
        "time, status_helm",
        "from k8order_helm",
        "where id = #{id,jdbcType=INTEGER}",
        "order by time desc"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="k8order_id", property="k8orderId", jdbcType=JdbcType.VARCHAR),
        @Result(column="helm_name", property="helmName", jdbcType=JdbcType.VARCHAR),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="res1", property="res1", jdbcType=JdbcType.INTEGER),
        @Result(column="k8order_type", property="k8OrderType", jdbcType=JdbcType.VARCHAR),
        @Result(column="res3", property="res3", jdbcType=JdbcType.BIT),
        @Result(column="source", property="source", jdbcType=JdbcType.VARCHAR),
        @Result(column="version", property="version", jdbcType=JdbcType.VARCHAR),
        @Result(column="charts", property="charts", jdbcType=JdbcType.VARCHAR),
        @Result(column="settings", property="settings", jdbcType=JdbcType.VARCHAR),
        @Result(column="time", property="time", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="status_helm", property="statusHelm", jdbcType=JdbcType.INTEGER)
    })
    K8OrderHelm selectByPrimaryKey(Integer id);
    
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table k8order_helm
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
  
    @Select({
        "select",
        "id, k8order_id, helm_name, user_id, res1, k8order_type, res3, source, version, charts, settings, ",
        "time, status_helm",
        "from k8order_helm",
        "where user_id = #{user,jdbcType=VARCHAR}",
        "order by time desc"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="k8order_id", property="k8orderId", jdbcType=JdbcType.VARCHAR),
        @Result(column="helm_name", property="helmName", jdbcType=JdbcType.VARCHAR),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="res1", property="res1", jdbcType=JdbcType.INTEGER),
        @Result(column="k8order_type", property="k8OrderType", jdbcType=JdbcType.VARCHAR),
        @Result(column="res3", property="res3", jdbcType=JdbcType.BIT),
        @Result(column="source", property="source", jdbcType=JdbcType.VARCHAR),
        @Result(column="version", property="version", jdbcType=JdbcType.VARCHAR),
        @Result(column="charts", property="charts", jdbcType=JdbcType.VARCHAR),
        @Result(column="settings", property="settings", jdbcType=JdbcType.VARCHAR),
        @Result(column="time", property="time", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="status_helm", property="statusHelm", jdbcType=JdbcType.INTEGER)
    })
    List<K8OrderHelm> selectByUser(String user);
    

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table k8order_helm
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    @Select({
        "select",
        "id, k8order_id, helm_name, user_id, res1, k8order_type, res3, source, version, charts, settings, ",
        "time, status_helm",
        "from k8order_helm",
        "order by time desc"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="k8order_id", property="k8orderId", jdbcType=JdbcType.VARCHAR),
        @Result(column="helm_name", property="helmName", jdbcType=JdbcType.VARCHAR),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="res1", property="res1", jdbcType=JdbcType.INTEGER),
        @Result(column="k8order_type", property="k8OrderType", jdbcType=JdbcType.VARCHAR),
        @Result(column="res3", property="res3", jdbcType=JdbcType.BIT),
        @Result(column="source", property="source", jdbcType=JdbcType.VARCHAR),
        @Result(column="version", property="version", jdbcType=JdbcType.VARCHAR),
        @Result(column="charts", property="charts", jdbcType=JdbcType.VARCHAR),
        @Result(column="settings", property="settings", jdbcType=JdbcType.VARCHAR),
        @Result(column="time", property="time", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="status_helm", property="statusHelm", jdbcType=JdbcType.INTEGER)
    })
    List<K8OrderHelm> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table k8order_helm
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    @Update({
        "update k8order_helm",
        "set k8order_id = #{k8orderId,jdbcType=VARCHAR},",
          "helm_name = #{helmName,jdbcType=VARCHAR},",
          "user_id = #{userId,jdbcType=VARCHAR},",
          "res1 = #{res1,jdbcType=INTEGER},",
          "k8order_type = #{k8OrderType,jdbcType=VARCHAR},",
          "res3 = #{res3,jdbcType=BIT},",
          "source = #{source,jdbcType=VARCHAR},",
          "version = #{version,jdbcType=VARCHAR},",
          "charts = #{charts,jdbcType=VARCHAR},",
          "settings = #{settings,jdbcType=VARCHAR},",
          "time = #{time,jdbcType=TIMESTAMP},",
          "status_helm = #{statusHelm,jdbcType=INTEGER}",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(K8OrderHelm record);
    

    @Select({
        "select",
       "count(k8order_id)",
        "from k8order_helm",
        "where user_id = #{user,jdbcType=VARCHAR} and status_helm = 2"
    })
    int countOrder(String user);
    
    
   @Select({
        "select",
       "count(k8order_id)",
        "from k8order_helm",
        "where status_helm = 2"
    }) 
    int countAllOrder( );
}