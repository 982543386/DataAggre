package org.ku8eye.service;

import java.util.List;

import org.ku8eye.Constants;
import org.ku8eye.domain.Ku8Job;
import org.ku8eye.mapping.Ku8JobMapper;
import org.ku8eye.mapping.TaskLogMapper;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class JobService {

	@Autowired
	private Ku8JobMapper jobDao;
	@Autowired
	private TaskScheduleJobService _taskScheduleJobService;
	@Autowired
	private TaskLogMapper _taskLogMapper;

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Ku8Job> getJobs() {
		return jobDao.selectAll();
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Ku8Job> getJobsByTenant(int tenantId) {
		return jobDao.selectByTenant(tenantId);
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Ku8Job> getJobsByOwner(String owner) {
		return jobDao.selectByOwner(owner);
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Ku8Job getJob(int id) {
		return jobDao.selectByPrimaryKey(id);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int addJob(int tenantId, String owner, String name, String version, String note, String json) {
		Ku8Job ku8job = new Ku8Job();
		ku8job.setTenantId(tenantId);
		ku8job.setOwner(owner);
		ku8job.setName(name);
		ku8job.setIconUrl("blank");
		ku8job.setVersion(version);
		ku8job.setZoneId(1);
		ku8job.setClusterId(0);
		ku8job.setNote(note);
		ku8job.setJsonSpec(json);
		ku8job.setPrevJsonSpec(null);
		ku8job.setStatus(Constants.KU8_APP_INIT_STATUS);
		ku8job.setResPart(null);
		
		return jobDao.insert(ku8job);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int updateJob(Ku8Job ku8p) {
		return jobDao.updateByPrimaryKey(ku8p);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int updateJobJSON(int id, String jsonSpec, String prevJsonSpec) {
		return jobDao.updateJSON(id, jsonSpec, prevJsonSpec);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public int deleteJob(int id) throws SchedulerException {
		Ku8Job job = jobDao.selectByPrimaryKey(id);
		if(!StringUtils.isEmpty(job.getCronExpression())&&job.getStatus()!=Constants.KU8_APP_INIT_STATUS){
			_taskLogMapper.deleteByJobId(id);
			_taskScheduleJobService.deleteScheduleJob(job.getId()+"", job.getClusterId()+"_"+job.getResPart());
		}
		return jobDao.deleteByPrimaryKey(id);
		
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<String> hasJobName(String jobName) {
		jobName = "%\"name\":\"" + jobName + "\"%";
		return jobDao.hasInJSON(jobName);
	}

	public List<Ku8Job> getScheduleJob() {
		return jobDao.selectScheduleJob();
	}
}
