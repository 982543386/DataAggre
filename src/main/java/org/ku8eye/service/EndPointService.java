package org.ku8eye.service;

import org.apache.log4j.Logger;
import org.ku8eye.bean.KubernetsMasterInfo;
import org.ku8eye.domain.Ku8sSrvEndpoint;
import org.ku8eye.mapping.Ku8sSrvEndpointMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EndPointService {
	@Autowired
	private Ku8sSrvEndpointMapper dao;
	private Logger log = Logger.getLogger(EndPointService.class);
	public KubernetsMasterInfo getById(Integer id) {

		Ku8sSrvEndpoint e = dao.selectByPrimaryKey(id);
		log.info("host====>>"+e.getSshHost());
		log.info("pass====>>"+e.getSshPass());
		log.info("user====>>"+e.getSshUser());
		log.info("port====>>"+e.getSshPort());
		return new KubernetsMasterInfo(e.getSshUser(), e.getSshPass(),
				e.getSshHost(), e.getSshPort());

	}
}
