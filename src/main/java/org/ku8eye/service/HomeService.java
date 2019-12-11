package org.ku8eye.service;

import java.util.List;

import org.ku8eye.mapping.HomeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cudcos.vo.AblityCatalogVO;

@Service
public class HomeService {
	
	@Autowired
	private HomeMapper mapper;

	public List<AblityCatalogVO> homeAblityStatistic(){
		return mapper.homeAblityStatistic();
	}
	
	public List<AblityCatalogVO> homeAblityStatisticByLocal(String zoneLocal){
		return mapper.homeAblityStatisticByLocal(zoneLocal);
	}
	
}
