package org.ku8eye.service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import org.ku8eye.domain.TaskLog;
import org.ku8eye.mapping.TaskLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskLogService {

	@Autowired
	private TaskLogMapper _taskLogMapper;

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<TaskLog> getLogByJobId(Integer jobId) {
		return _taskLogMapper.selectByJobId(jobId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public int deleteLogByTaskId(String taskLogId) {
		return _taskLogMapper.deleteByPrimaryKey(taskLogId);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int insert(TaskLog taskLog) {
		return _taskLogMapper.insert(taskLog);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public int update(TaskLog taskLog) {
		return _taskLogMapper.updateByPrimaryKey(taskLog);
//		return _taskLogMapper.updateByTaskIdStarTime(taskLog.getTaskId(),
//				Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(taskLog.getStartTime())),taskLog.getEndTime(),
//				taskLog.getStatus(),taskLog.getHint(),taskLog.getLastUpdated());
		
	}
	
	public TaskLog getTaskLogByByPrimaryKey(String taskLogId) {
		return _taskLogMapper.selectByPrimaryKey(taskLogId);
	}

	public TaskLog getLastLogByJobId(int jobId) {
		return _taskLogMapper.selectLastLogByJobId(jobId);
		
	}
}
