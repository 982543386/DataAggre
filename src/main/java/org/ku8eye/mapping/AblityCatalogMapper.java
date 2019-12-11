package org.ku8eye.mapping;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;

import com.cudcos.model.catalog.AblityCatalog;

public interface AblityCatalogMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ku8_project
     *
     * @mbggenerated
     */
    @Delete({
        "delete from dcos_ability_catalog ",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);


    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ku8_project
     *
     * @mbggenerated
     */
    @Select({
    	"select ",
        "id, ability_id, ability_name, ability_type, ability_desc, create_url, zone_id, version ",
        "from dcos_ability_catalog",
        "where id = #{id,jdbcType=INTEGER}"
    })
    @Results({
    	@Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="ability_id", property="abilityId", jdbcType=JdbcType.VARCHAR),
        @Result(column="ability_name", property="ablityName", jdbcType=JdbcType.VARCHAR),
        @Result(column="ability_type", property="ablityType", jdbcType=JdbcType.INTEGER),
        @Result(column="ability_desc", property="abilityDesc", jdbcType=JdbcType.VARCHAR),
        @Result(column="create_url", property="createUrl", jdbcType=JdbcType.VARCHAR),
        @Result(column="zone_id", property="zoneId", jdbcType=JdbcType.INTEGER),
        @Result(column="version", property="version", jdbcType=JdbcType.VARCHAR)
    })
    AblityCatalog selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ku8_project
     *
     * @mbggenerated
     */
    @Select({
    	"select ",
        "id, ability_id, ability_name, ability_type, ability_desc, create_url, zone_id, version ",
        "from dcos_ability_catalog"
    })
    @Results({
    	@Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="ability_id", property="abilityId", jdbcType=JdbcType.VARCHAR),
        @Result(column="ability_name", property="ablityName", jdbcType=JdbcType.VARCHAR),
        @Result(column="ability_type", property="ablityType", jdbcType=JdbcType.INTEGER),
        @Result(column="ability_desc", property="abilityDesc", jdbcType=JdbcType.VARCHAR),
        @Result(column="create_url", property="createUrl", jdbcType=JdbcType.VARCHAR),
        @Result(column="zone_id", property="zoneId", jdbcType=JdbcType.INTEGER),
        @Result(column="version", property="version", jdbcType=JdbcType.VARCHAR)
    })
    List<AblityCatalog> selectAll();

    
    @Select({
    	"select ",
        "id, ability_id, ability_name, ability_type, ability_desc, create_url, zone_id, version ",
        "from dcos_ability_catalog where ability_type = #{type,jdbcType=INTEGER}"
    })
    @Results({
    	@Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="ability_id", property="abilityId", jdbcType=JdbcType.VARCHAR),
        @Result(column="ability_name", property="ablityName", jdbcType=JdbcType.VARCHAR),
        @Result(column="ability_type", property="ablityType", jdbcType=JdbcType.INTEGER),
        @Result(column="ability_desc", property="abilityDesc", jdbcType=JdbcType.VARCHAR),
        @Result(column="create_url", property="createUrl", jdbcType=JdbcType.VARCHAR),
        @Result(column="zone_id", property="zoneId", jdbcType=JdbcType.INTEGER),
        @Result(column="version", property="version", jdbcType=JdbcType.VARCHAR)
    })
    List<AblityCatalog> selectByType(int type);
    
    @Select({
    	"select ",
        "id, ability_id, ability_name, ability_type, ability_desc, create_url, zone_id, version ",
        "from dcos_ability_catalog where zone_id = #{zoneId,jdbcType=INTEGER}"
    })
    @Results({
    	@Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="ability_id", property="abilityId", jdbcType=JdbcType.VARCHAR),
        @Result(column="ability_name", property="ablityName", jdbcType=JdbcType.VARCHAR),
        @Result(column="ability_type", property="ablityType", jdbcType=JdbcType.INTEGER),
        @Result(column="ability_desc", property="abilityDesc", jdbcType=JdbcType.VARCHAR),
        @Result(column="create_url", property="createUrl", jdbcType=JdbcType.VARCHAR),
        @Result(column="zone_id", property="zoneId", jdbcType=JdbcType.INTEGER),
        @Result(column="version", property="version", jdbcType=JdbcType.VARCHAR)
    })
    List<AblityCatalog> selectByZoneId(int zoneId);
    
    
    @Select({
    	"select ",
        "id, ability_id, ability_name, ability_type, ability_desc, create_url, zone_id, version ",
        "from dcos_ability_catalog where zone_id = #{zoneId, jdbcType=INTEGER} and ability_type = #{type,jdbcType=INTEGER}"
    })
    @Results({
    	@Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="ability_id", property="abilityId", jdbcType=JdbcType.VARCHAR),
        @Result(column="ability_name", property="ablityName", jdbcType=JdbcType.VARCHAR),
        @Result(column="ability_type", property="ablityType", jdbcType=JdbcType.INTEGER),
        @Result(column="ability_desc", property="abilityDesc", jdbcType=JdbcType.VARCHAR),
        @Result(column="create_url", property="createUrl", jdbcType=JdbcType.VARCHAR),
        @Result(column="zone_id", property="zoneId", jdbcType=JdbcType.INTEGER),
        @Result(column="version", property="version", jdbcType=JdbcType.VARCHAR)
    })
    List<AblityCatalog> selectByCond(@Param("zoneId")int zoneId, @Param("type")int type);
    
    @Select({
    	"select ",
        "id, ability_id, ability_name, ability_type, ability_desc, create_url, zone_id, version ",
        "from dcos_ability_catalog where zone_id = #{zoneId, jdbcType=INTEGER}"
    })
    @Results({
    	@Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="ability_id", property="abilityId", jdbcType=JdbcType.VARCHAR),
        @Result(column="ability_name", property="ablityName", jdbcType=JdbcType.VARCHAR),
        @Result(column="ability_type", property="ablityType", jdbcType=JdbcType.INTEGER),
        @Result(column="ability_desc", property="abilityDesc", jdbcType=JdbcType.VARCHAR),
        @Result(column="create_url", property="createUrl", jdbcType=JdbcType.VARCHAR),
        @Result(column="zone_id", property="zoneId", jdbcType=JdbcType.INTEGER),
        @Result(column="version", property="version", jdbcType=JdbcType.VARCHAR)
    })
    List<AblityCatalog> selectByZoneID(@Param("zoneId")int zoneId);
    
}