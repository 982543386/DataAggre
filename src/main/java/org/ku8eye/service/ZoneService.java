package org.ku8eye.service;

import java.util.List;

import org.ku8eye.domain.Zone;
import org.ku8eye.mapping.ZoneMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ZoneService {
	
	@Autowired
	private ZoneMapper zoneDao;
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Zone> getAllZone() {
		return zoneDao.selectAll();
	}
	

}
