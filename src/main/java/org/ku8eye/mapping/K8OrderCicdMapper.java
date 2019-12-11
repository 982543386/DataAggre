package org.ku8eye.mapping;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;
import org.ku8eye.domain.K8Order;
import org.ku8eye.domain.K8OrderBigdata;
import org.ku8eye.domain.K8OrderCicd;

public interface K8OrderCicdMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table k8order_cicd
     *
     * @mbg.generated Wed Jul 26 16:12:19 CST 2017
     */
    @Delete({
        "delete from k8order_cicd",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table k8order_cicd
     *
     * @mbg.generated Wed Jul 26 16:12:19 CST 2017
     */
    @Insert({
        "insert into k8order_cicd (id, war_name, ",
        "war_location, war_url, ",
        "zone, repo_name, ",
        "source_type, source_url, ",
        "source_username, source_repo, ",
        "source_password, source_token, ",
        "deliver_type, cluster_count, ",
        "resource_type, storage_type, ",
        "disk_space, database_name, ",
        "jenkins_url, jenkins_action, ",
        "remark, security, ",
        "k8order_id, user_id, cd_type, ",
        "remark_admin)",
        "values (#{id,jdbcType=INTEGER}, #{warName,jdbcType=VARCHAR}, ",
        "#{warLocation,jdbcType=VARCHAR}, #{warUrl,jdbcType=VARCHAR}, ",
        "#{zone,jdbcType=INTEGER}, #{repoName,jdbcType=VARCHAR}, ",
        "#{sourceType,jdbcType=INTEGER}, #{sourceUrl,jdbcType=VARCHAR}, ",
        "#{sourceUsername,jdbcType=VARCHAR}, #{sourceRepo,jdbcType=VARCHAR}, ",
        "#{sourcePassword,jdbcType=VARCHAR}, #{sourceToken,jdbcType=VARCHAR}, ",
        "#{deliverType,jdbcType=INTEGER}, #{clusterCount,jdbcType=INTEGER}, ",
        "#{resourceType,jdbcType=INTEGER}, #{storageType,jdbcType=INTEGER}, ",
        "#{cdType,jdbcType=INTEGER},  ",
        "#{diskSpace,jdbcType=INTEGER}, #{databaseName,jdbcType=VARCHAR}, ",
        "#{jenkinsUrl,jdbcType=VARCHAR}, #{jenkinsAction,jdbcType=VARCHAR}, ",
        "#{remark,jdbcType=VARCHAR}, #{security,jdbcType=VARCHAR}, ",
        "#{k8orderId,jdbcType=VARCHAR}, #{userId,jdbcType=VARCHAR}, ",
        "#{remarkAdmin,jdbcType=VARCHAR})"
    })
    int insert(K8OrderCicd record);
    /**=================================添加自定义web服务=====================
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table k8order_cicd
     *
     * @mbg.generated Wed Jul 26 15:09:53 CST 2017
     */
    @Insert({
        "insert into k8order_cicd (id,war_name,",
        "war_location,repo_name, source_repo,",
        "source_type, source_url, ",
        "source_username, source_password, ",
        "source_token, deliver_type, ",
        "resource_type, ",
        "storage_type, disk_space, ",
        "remark, cd_type,",
        "k8order_id, ",
        "user_id)",
        "values (#{id,jdbcType=INTEGER}, #{warName,jdbcType=VARCHAR}, #{warLocation,jdbcType=VARCHAR},",
        "#{repoName,jdbcType=VARCHAR}, #{sourceRepo,jdbcType=VARCHAR},",
        "#{sourceType,jdbcType=INTEGER}, #{sourceUrl,jdbcType=VARCHAR}, ",
        "#{sourceUsername,jdbcType=VARCHAR}, #{sourcePassword,jdbcType=VARCHAR}, ",
        "#{sourceToken,jdbcType=VARCHAR}, #{deliverType,jdbcType=INTEGER}, ",
        "#{resourceType,jdbcType=INTEGER}, ",
        "#{storageType,jdbcType=INTEGER}, #{diskSpace,jdbcType=INTEGER}, ",
        "#{remark,jdbcType=VARCHAR}, #{cdType,jdbcType=INTEGER},",
        "#{k8orderId,jdbcType=VARCHAR}, ",
        "#{userId,jdbcType=VARCHAR})"
    })
    int insertByDiy(K8OrderCicd record);
    
    
    /**=================================添加WAR服务=====================
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table k8order_cicd
     *
     * @mbg.generated Wed Jul 26 15:09:53 CST 2017
     */
    @Insert({
        "insert into k8order_cicd (id,war_name, ",
        "war_location, war_url, ",
        "zone, repo_name, ",
        "source_type,  ",
        "deliver_type, ",
        "resource_type, ",
        "storage_type, disk_space, ",
        "remark, cd_type,",
        "k8order_id, ",
        "user_id)",
        "values (#{id,jdbcType=INTEGER}, #{warName,jdbcType=VARCHAR},  ",
        "#{warLocation,jdbcType=VARCHAR}, #{warUrl,jdbcType=VARCHAR}, ",
        "#{zone,jdbcType=INTEGER}, #{repoName,jdbcType=VARCHAR}, ",
        "#{sourceType,jdbcType=INTEGER}, ",
        "#{deliverType,jdbcType=INTEGER}, ",
        "#{resourceType,jdbcType=INTEGER}, ",
        "#{storageType,jdbcType=INTEGER}, #{diskSpace,jdbcType=INTEGER}, ",
        "#{cdType,jdbcType=INTEGER},  ",
        "#{remark,jdbcType=VARCHAR}, ",
        "#{k8orderId,jdbcType=VARCHAR}, ",
        "#{userId,jdbcType=VARCHAR})"
    })
    int insertByWar(K8OrderCicd record);
    
    
    
    
    
    
    
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table k8order_cicd
     *
     * @mbg.generated Wed Jul 26 16:12:19 CST 2017
     */
    @Select({
        "select",
        "id, war_name, war_location, war_url, zone, repo_name, source_type, source_url, ",
        "source_username, source_repo, source_password, source_token, deliver_type, cluster_count, ",
        "resource_type, storage_type, disk_space, database_name, jenkins_url, jenkins_action, cd_type,",
        "remark, security, k8order_id, user_id, remark_admin",
        "from k8order_cicd",
        "where id = #{id,jdbcType=INTEGER}",
        "order by id desc"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="war_name", property="warName", jdbcType=JdbcType.VARCHAR),
        @Result(column="war_location", property="warLocation", jdbcType=JdbcType.VARCHAR),
        @Result(column="war_url", property="warUrl", jdbcType=JdbcType.VARCHAR),
        @Result(column="zone", property="zone", jdbcType=JdbcType.INTEGER),
        @Result(column="repo_name", property="repoName", jdbcType=JdbcType.VARCHAR),
        @Result(column="source_type", property="sourceType", jdbcType=JdbcType.INTEGER),
        @Result(column="source_url", property="sourceUrl", jdbcType=JdbcType.VARCHAR),
        @Result(column="cd_type",property="cdType", jdbcType=JdbcType.INTEGER),
        @Result(column="source_username", property="sourceUsername", jdbcType=JdbcType.VARCHAR),
        @Result(column="source_repo", property="sourceRepo", jdbcType=JdbcType.VARCHAR),
        @Result(column="source_password", property="sourcePassword", jdbcType=JdbcType.VARCHAR),
        @Result(column="source_token", property="sourceToken", jdbcType=JdbcType.VARCHAR),
        @Result(column="deliver_type", property="deliverType", jdbcType=JdbcType.INTEGER),
        @Result(column="cluster_count", property="clusterCount", jdbcType=JdbcType.INTEGER),
        @Result(column="resource_type", property="resourceType", jdbcType=JdbcType.INTEGER),
        @Result(column="storage_type", property="storageType", jdbcType=JdbcType.INTEGER),
        @Result(column="disk_space", property="diskSpace", jdbcType=JdbcType.INTEGER),
        @Result(column="database_name", property="databaseName", jdbcType=JdbcType.VARCHAR),
        @Result(column="jenkins_url", property="jenkinsUrl", jdbcType=JdbcType.VARCHAR),
        @Result(column="jenkins_action", property="jenkinsAction", jdbcType=JdbcType.VARCHAR),
        @Result(column="remark", property="remark", jdbcType=JdbcType.VARCHAR),
        @Result(column="security", property="security", jdbcType=JdbcType.VARCHAR),
        @Result(column="k8order_id", property="k8orderId", jdbcType=JdbcType.VARCHAR),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="remark_admin", property="remarkAdmin", jdbcType=JdbcType.VARCHAR)
    })
    K8OrderCicd selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table k8order_cicd
     *
     * @mbg.generated Wed Jul 26 16:12:19 CST 2017
     */
    @Select({
        "select",
        "id, war_name, war_location, war_url, zone, repo_name, source_type, source_url, ",
        "source_username, source_repo, source_password, source_token, deliver_type, cluster_count, ",
        "resource_type, storage_type, disk_space, database_name, jenkins_url, jenkins_action, cd_type,",
        "remark, security, k8order_id, user_id, remark_admin",
        "from k8order_cicd",
        "order by id desc"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="war_name", property="warName", jdbcType=JdbcType.VARCHAR),
        @Result(column="war_location", property="warLocation", jdbcType=JdbcType.VARCHAR),
        @Result(column="war_url", property="warUrl", jdbcType=JdbcType.VARCHAR),
        @Result(column="zone", property="zone", jdbcType=JdbcType.INTEGER),
        @Result(column="repo_name", property="repoName", jdbcType=JdbcType.VARCHAR),
        @Result(column="cd_type",property="cdType", jdbcType=JdbcType.INTEGER),
        @Result(column="source_type", property="sourceType", jdbcType=JdbcType.INTEGER),
        @Result(column="source_url", property="sourceUrl", jdbcType=JdbcType.VARCHAR),
        @Result(column="source_username", property="sourceUsername", jdbcType=JdbcType.VARCHAR),
        @Result(column="source_repo", property="sourceRepo", jdbcType=JdbcType.VARCHAR),
        @Result(column="source_password", property="sourcePassword", jdbcType=JdbcType.VARCHAR),
        @Result(column="source_token", property="sourceToken", jdbcType=JdbcType.VARCHAR),
        @Result(column="deliver_type", property="deliverType", jdbcType=JdbcType.INTEGER),
        @Result(column="cluster_count", property="clusterCount", jdbcType=JdbcType.INTEGER),
        @Result(column="resource_type", property="resourceType", jdbcType=JdbcType.INTEGER),
        @Result(column="storage_type", property="storageType", jdbcType=JdbcType.INTEGER),
        @Result(column="disk_space", property="diskSpace", jdbcType=JdbcType.INTEGER),
        @Result(column="database_name", property="databaseName", jdbcType=JdbcType.VARCHAR),
        @Result(column="jenkins_url", property="jenkinsUrl", jdbcType=JdbcType.VARCHAR),
        @Result(column="jenkins_action", property="jenkinsAction", jdbcType=JdbcType.VARCHAR),
        @Result(column="remark", property="remark", jdbcType=JdbcType.VARCHAR),
        @Result(column="security", property="security", jdbcType=JdbcType.VARCHAR),
        @Result(column="k8order_id", property="k8orderId", jdbcType=JdbcType.VARCHAR),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="remark_admin", property="remarkAdmin", jdbcType=JdbcType.VARCHAR)
    })
    List<K8OrderCicd> selectAll();
    
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table k8order_cicd
     *
     * @mbg.generated Wed Jul 26 16:12:19 CST 2017
     */
    @Select({
    	  "select",
          "a.id,a.repo_name,a.k8order_id,a.remark_admin,a.cd_type,a.remark,a.source_type,b.status",
          "from k8order_cicd a, k8order b",
          "where (a.k8order_id=b.k8order_id)",
          "order by b.create_time desc"
      })
      @Results({
      	 @Result(column="id", property="id", jdbcType=JdbcType.VARCHAR),
          @Result(column="repo_name", property="repoName", jdbcType=JdbcType.VARCHAR),
          @Result(column="k8order_id", property="k8orderId", jdbcType=JdbcType.VARCHAR),
          @Result(column="remark_admin", property="remarkAdmin", jdbcType=JdbcType.VARCHAR),
          @Result(column="cd_type",property="cdType", jdbcType=JdbcType.INTEGER),
          @Result(column="remark", property="remark", jdbcType=JdbcType.VARCHAR),
          @Result(column="source_type", property="sourceType", jdbcType=JdbcType.INTEGER),
          @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
          @Result(column="status", property="status", jdbcType=JdbcType.INTEGER)
    })
    List<K8OrderCicd> selectAllCicd();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table k8order_cicd
     *
     * @mbg.generated Wed Jul 26 16:12:19 CST 2017
     */
    @Select({
        "select",
        "a.id,a.repo_name,a.k8order_id,a.remark_admin,a.cd_type,a.remark,a.source_type,b.status",
        "from k8order_cicd a, k8order b",
        "where (a.k8order_id=b.k8order_id) and (a.user_id= #{user,jdbcType=VARCHAR})",
        "order by b.create_time desc"
    })
    @Results({
    	 @Result(column="id", property="id", jdbcType=JdbcType.VARCHAR),
        @Result(column="repo_name", property="repoName", jdbcType=JdbcType.VARCHAR),
        @Result(column="k8order_id", property="k8orderId", jdbcType=JdbcType.VARCHAR),
        @Result(column="remark_admin", property="remarkAdmin", jdbcType=JdbcType.VARCHAR),
        @Result(column="cd_type",property="cdType", jdbcType=JdbcType.INTEGER),
        @Result(column="remark", property="remark", jdbcType=JdbcType.VARCHAR),
        @Result(column="source_type", property="sourceType", jdbcType=JdbcType.INTEGER),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="status", property="status", jdbcType=JdbcType.INTEGER)
    })
    List<K8OrderCicd> selectByRepoName(String user);

    
    
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table k8order_cicd
     *
     * @mbg.generated Wed Jul 26 16:12:19 CST 2017
     */
    @Select({
        "select",
        "id, war_name, war_location, war_url, zone, repo_name, source_type, source_url, ",
        "source_username, source_repo, source_password, source_token, deliver_type, cluster_count, ",
        "resource_type, storage_type, disk_space, database_name, jenkins_url, jenkins_action, cd_type,",
        "remark, security, k8order_id, user_id, remark_admin",
        "from k8order_cicd",
        "where k8order_id = #{k8order_id,jdbcType=VARCHAR}",
        "order by id desc"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="war_name", property="warName", jdbcType=JdbcType.VARCHAR),
        @Result(column="war_location", property="warLocation", jdbcType=JdbcType.VARCHAR),
        @Result(column="war_url", property="warUrl", jdbcType=JdbcType.VARCHAR),
        @Result(column="zone", property="zone", jdbcType=JdbcType.INTEGER),
        @Result(column="repo_name", property="repoName", jdbcType=JdbcType.VARCHAR),
        @Result(column="source_type", property="sourceType", jdbcType=JdbcType.INTEGER),
        @Result(column="source_url", property="sourceUrl", jdbcType=JdbcType.VARCHAR),
        @Result(column="cd_type",property="cdType", jdbcType=JdbcType.INTEGER),
        @Result(column="source_username", property="sourceUsername", jdbcType=JdbcType.VARCHAR),
        @Result(column="source_repo", property="sourceRepo", jdbcType=JdbcType.VARCHAR),
        @Result(column="source_password", property="sourcePassword", jdbcType=JdbcType.VARCHAR),
        @Result(column="source_token", property="sourceToken", jdbcType=JdbcType.VARCHAR),
        @Result(column="deliver_type", property="deliverType", jdbcType=JdbcType.INTEGER),
        @Result(column="cluster_count", property="clusterCount", jdbcType=JdbcType.INTEGER),
        @Result(column="resource_type", property="resourceType", jdbcType=JdbcType.INTEGER),
        @Result(column="storage_type", property="storageType", jdbcType=JdbcType.INTEGER),
        @Result(column="disk_space", property="diskSpace", jdbcType=JdbcType.INTEGER),
        @Result(column="database_name", property="databaseName", jdbcType=JdbcType.VARCHAR),
        @Result(column="jenkins_url", property="jenkinsUrl", jdbcType=JdbcType.VARCHAR),
        @Result(column="jenkins_action", property="jenkinsAction", jdbcType=JdbcType.VARCHAR),
        @Result(column="remark", property="remark", jdbcType=JdbcType.VARCHAR),
        @Result(column="security", property="security", jdbcType=JdbcType.VARCHAR),
        @Result(column="k8order_id", property="k8orderId", jdbcType=JdbcType.VARCHAR),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="remark_admin", property="remarkAdmin", jdbcType=JdbcType.VARCHAR)
    })
    K8OrderCicd selectByOrderId(String k8orderId);
    
    

     
    
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table k8order_cicd
     *
     * @mbg.generated Wed Jul 26 16:12:19 CST 2017
     */
    @Select({
        "select",
        "zone, repo_name, source_type, source_url, ",
        "source_username, source_repo, source_password, source_token, deliver_type,  ",
        "resource_type, storage_type, disk_space, cd_type, ",
        "remark, k8order_id, user_id, remark_admin",
        "from k8order_cicd",
        "order by id desc"
    })
    @Results({
        @Result(column="zone", property="zone", jdbcType=JdbcType.INTEGER),
        @Result(column="repo_name", property="repoName", jdbcType=JdbcType.VARCHAR),
        @Result(column="source_type", property="sourceType", jdbcType=JdbcType.INTEGER),
        @Result(column="source_url", property="sourceUrl", jdbcType=JdbcType.VARCHAR),
        @Result(column="source_username", property="sourceUsername", jdbcType=JdbcType.VARCHAR),
        @Result(column="cd_type",property="cdType", jdbcType=JdbcType.INTEGER),
        @Result(column="source_repo", property="sourceRepo", jdbcType=JdbcType.VARCHAR),
        @Result(column="source_password", property="sourcePassword", jdbcType=JdbcType.VARCHAR),
        @Result(column="source_token", property="sourceToken", jdbcType=JdbcType.VARCHAR),
        @Result(column="deliver_type", property="deliverType", jdbcType=JdbcType.INTEGER),
        @Result(column="resource_type", property="resourceType", jdbcType=JdbcType.INTEGER),
        @Result(column="storage_type", property="storageType", jdbcType=JdbcType.INTEGER),
        @Result(column="disk_space", property="diskSpace", jdbcType=JdbcType.INTEGER),
        @Result(column="remark", property="remark", jdbcType=JdbcType.VARCHAR),
        @Result(column="k8order_id", property="k8orderId", jdbcType=JdbcType.VARCHAR),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="remark_admin", property="remarkAdmin", jdbcType=JdbcType.VARCHAR)
    })
    List<K8OrderCicd> selectAllWebOrder();
    
    
    
    
    
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table k8order_cicd
     *
     * @mbg.generated Wed Jul 26 16:12:19 CST 2017
     */
    @Update({
        "update k8order_cicd",
        "set war_name = #{warName,jdbcType=VARCHAR},",
          "war_location = #{warLocation,jdbcType=VARCHAR},",
          "war_url = #{warUrl,jdbcType=VARCHAR},",
          "zone = #{zone,jdbcType=INTEGER},",
          "repo_name = #{repoName,jdbcType=VARCHAR},",
          "source_type = #{sourceType,jdbcType=INTEGER},",
          "source_url = #{sourceUrl,jdbcType=VARCHAR},",
          "source_username = #{sourceUsername,jdbcType=VARCHAR},",
          "source_repo = #{sourceRepo,jdbcType=VARCHAR},",
          "source_password = #{sourcePassword,jdbcType=VARCHAR},",
          "source_token = #{sourceToken,jdbcType=VARCHAR},",
          "deliver_type = #{deliverType,jdbcType=INTEGER},",
          "cluster_count = #{clusterCount,jdbcType=INTEGER},",
          "resource_type = #{resourceType,jdbcType=INTEGER},",
          "storage_type = #{storageType,jdbcType=INTEGER},",
          "disk_space = #{diskSpace,jdbcType=INTEGER},",
          "database_name = #{databaseName,jdbcType=VARCHAR},",
          "jenkins_url = #{jenkinsUrl,jdbcType=VARCHAR},",
          "jenkins_action = #{jenkinsAction,jdbcType=VARCHAR},",
          "remark = #{remark,jdbcType=VARCHAR},",
          "security = #{security,jdbcType=VARCHAR},",
          "k8order_id = #{k8orderId,jdbcType=VARCHAR},",
          "user_id = #{userId,jdbcType=VARCHAR},",
          "cd_type = #{cdType,jdbcType=INTEGER},",
          "remark_admin = #{remarkAdmin,jdbcType=VARCHAR}",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(K8OrderCicd record);
    
    @Update({
        "update k8order_cicd a,k8order b",
        "set b.status = #{status,jdbcType=INTEGER},",
        "remark_admin = #{remarkAdmin,jdbcType=VARCHAR},",
        "source_url = #{sourceUrl,jdbcType=VARCHAR},",
        "b.start_time = #{startTime,jdbcType=TIMESTAMP}",
        "where a.k8order_id = #{k8orderId,jdbcType=VARCHAR} and a.k8order_id=b.k8order_id"
    })
    int updateByK8OrderId(K8OrderCicd record);
    
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table k8order_cicd
     *
     * @mbg.generated Wed Jul 26 16:12:19 CST 2017
     */
    @Select({
        "select",
        "war_name",
        "from k8order_cicd ",
        "where k8order_id = #{k8orderId,jdbcType=VARCHAR}"
    })
    @Results({
        @Result(column="war_name", property="warName", jdbcType=JdbcType.VARCHAR),
    })
    String selectwarnameByk8orderId(String k8order_id);
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table k8order_cicd
     *
     * @mbg.generated Wed Jul 26 16:12:19 CST 2017
     */
    @Select({
        "select",
        "war_location",
        "from k8order_cicd ",
        "where k8order_id = #{k8orderId,jdbcType=VARCHAR}"
    })
    @Results({
        @Result(column="war_location", property="warLocation", jdbcType=JdbcType.VARCHAR),
    })
    String selectwarlocationByk8orderId(String k8order_id);


    
}