package org.ku8eye.mapping;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;

import com.cudcos.vo.AblityCatalogVO;

public interface HomeMapper {

	@Select({
    	"select a.local as zoneLocal,count(b.id) as abilityNum from zone a ",
        "left join dcos_ability_catalog b on b.zone_id = a.id ",
        "group by a.local"
    })
    @Results({
        @Result(column="zoneLocal", property="zoneLocal", jdbcType=JdbcType.VARCHAR),
        @Result(column="abilityNum", property="abilityNum", jdbcType=JdbcType.INTEGER)
    })
	public List<AblityCatalogVO> homeAblityStatistic();
	
	@Select({
    	"select a.name as zoneName,a.local_ip as zoneLocalIp,a.id as zoneId,b.ability_type as abilityType,count(b.id) as abilityNum from zone a ",
        "left join dcos_ability_catalog b on b.zone_id = a.id ",
        "where a.local = #{zoneLocal,jdbcType=VARCHAR} ",
        "group by a.name,a.local_ip,a.id,b.ability_type ",
        "order by a.name,a.local_ip,a.id,b.ability_type "
    })
    @Results({
        @Result(column="zoneName", property="zoneName", jdbcType=JdbcType.VARCHAR),
        @Result(column="abilityType", property="ablityType", jdbcType=JdbcType.INTEGER),
        @Result(column="abilityNum", property="abilityNum", jdbcType=JdbcType.INTEGER),
        @Result(column="zoneLocalIp", property="zoneLocalIp", jdbcType=JdbcType.VARCHAR),
        @Result(column="zoneId", property="zoneId", jdbcType=JdbcType.INTEGER)
    })
	public List<AblityCatalogVO> homeAblityStatisticByLocal(@Param("zoneLocal") String zoneLocal);
	
	
}
