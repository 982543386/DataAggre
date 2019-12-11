package org.ku8eye.ctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ku8eye.bean.GridData;
import org.ku8eye.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cudcos.model.catalog.AblityCatalog;
import com.cudcos.service.AblityCatalogService;
import com.cudcos.util.CacheDcosUrlConfig;
import com.cudcos.vo.AblityCatalogVO;

@RestController
@RequestMapping("/home")
public class HomeController {
	
	@Autowired
	private HomeService homeService;
	
	@Autowired
	private AblityCatalogService ablityCatalogService;

	/**
	 * 按照省份汇总能力数量  
	 */
	@RequestMapping(value = "/homeAblityStatistic",method = RequestMethod.POST)
	public List<JSONObject> homeAblityStatistic(){
		List<AblityCatalogVO> acVoList = this.homeService.homeAblityStatistic();
		List<JSONObject> list = new ArrayList<JSONObject>();
		for(AblityCatalogVO acVo:acVoList){
			JSONObject jSONObject = new JSONObject();
			jSONObject.put("name",acVo.getZoneLocal());
			jSONObject.put("value",acVo.getAbilityNum());
			list.add(jSONObject);
		}
        return list;
	}
	
	
	/**
	 *  按照指定省份按照数据中心，能力类型汇总能力数量
	 */
	@RequestMapping(value = "/homeAblityStatisticByLocal",produces="application/json; charset=utf-8",method = RequestMethod.POST)
	public List<JSONObject> homeAblityStatisticByLocal(@RequestBody HashMap<String,String> map){
		List<AblityCatalogVO> acVoList = this.homeService.homeAblityStatisticByLocal(map.get("zoneLocal"));
		List<JSONObject> list = new ArrayList<JSONObject>();
		for(AblityCatalogVO acVo:acVoList){
			JSONObject jSONObject = new JSONObject();
			jSONObject.put("zoneName",acVo.getZoneName());
			jSONObject.put("ablityType", acVo.getAblityType());
			jSONObject.put("abilityNum",acVo.getAbilityNum());
			jSONObject.put("zoneLocalIp", acVo.getZoneLocalIp());
			jSONObject.put("zoneId", acVo.getZoneId());
			list.add(jSONObject);
		}
        return list;
	}
	
	/**
	 * 能力列表
	 */
	@RequestMapping(value = "/ablityCatalogs")
	public GridData ablityCatalogs(ModelMap model) {
		GridData grid = new GridData();
		List<AblityCatalog> list = ablityCatalogService.listByZoneId(Integer.parseInt(CacheDcosUrlConfig.getZoneId()));
		List<JSONObject> resultList = new ArrayList<JSONObject>();
		for(AblityCatalog ac:list){
			JSONObject jSONObject = (JSONObject)JSON.toJSON(ac);
			if(ac.getAblityType()==1){
				jSONObject.put("ablityTypeName", "大数据服务");
			}else if(ac.getAblityType()==2){
				jSONObject.put("ablityTypeName", "数据库服务");
			}else if(ac.getAblityType()==3){
				jSONObject.put("ablityTypeName", "容器服务");
			}else{
				jSONObject.put("ablityTypeName", "其他类服务");
			}
			resultList.add(jSONObject);
		}
		grid.setData(resultList);
		return grid;
	}
}
