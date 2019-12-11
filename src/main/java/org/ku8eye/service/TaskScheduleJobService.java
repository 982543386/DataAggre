package org.ku8eye.service;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.ku8eye.Constants;
import org.ku8eye.bean.task.TaskInfo;
import org.ku8eye.ctrl.JobController;
import org.ku8eye.domain.Ku8Job;
import org.ku8eye.domain.TaskLog;
import org.ku8eye.service.k8s.K8sAPIService;
import org.ku8eye.service.k8s.K8toF8APIService;
import org.ku8eye.task.QuartzJobFactoryDisallowConcurrentExecution;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskScheduleJobService {
	static final Logger log = Logger.getLogger(TaskScheduleJobService.class);
	@Autowired
	private SchedulerFactoryBean _schedulerFactoryBean;
	@Autowired
	private TaskLogService _taskLogService;
	@Autowired
	private K8sAPIService _k8sAPIService;
	@Autowired
	JobService _jobService;
	@Autowired
	UserService _userService;
	@Autowired
	K8toF8APIService _K8toF8APIService;
	@Autowired
	JobController _jobController;
	
	@PostConstruct
	public void init() throws Exception {
		// 这里获取任务信息数据
		List<Ku8Job> jobList = _jobService.getScheduleJob();
	
		for (Ku8Job job : jobList) {
			addScheduleJob(job);
		}
	}

	/**
	 * 添加任务到Scheduler
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addScheduleJob(Ku8Job job) throws SchedulerException {
		if (job == null || !Constants.TASK_STATUS_RUNNING.equals(job.getTaskStatus().toString())) {
			return;
		}

		Scheduler scheduler = _schedulerFactoryBean.getScheduler();
		log.debug(scheduler + ".......................................................................................add");
		TriggerKey triggerKey = TriggerKey.triggerKey(job.getId()+"", job.getClusterId()+"_"+job.getResPart());

		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
		// 不存在，创建一个
		if (null == trigger) {
//			Class clazz = Constants.TASK_CONCURRENT_IS.equals(job.getConcurrent()) ? QuartzJobFactory.class : QuartzJobFactoryDisallowConcurrentExecution.class;
			Class clazz = QuartzJobFactoryDisallowConcurrentExecution.class;

			JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(job.getId()+"", job.getClusterId()+"_"+job.getResPart()).build();

			jobDetail.getJobDataMap().put("scheduleJob", job);
			jobDetail.getJobDataMap().put("taskLogService", _taskLogService);
			jobDetail.getJobDataMap().put("k8sAPIService", _k8sAPIService);
			jobDetail.getJobDataMap().put("jobService", _jobService);
			jobDetail.getJobDataMap().put("userService", _userService);
			jobDetail.getJobDataMap().put("K8toF8APIService", _K8toF8APIService);
			jobDetail.getJobDataMap().put("jobController", _jobController);

			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());

			trigger = TriggerBuilder.newTrigger().withIdentity(job.getId()+"", job.getClusterId()+"_"+job.getResPart()).withSchedule(scheduleBuilder).build();

			scheduler.scheduleJob(jobDetail, trigger);
		} else {
			// Trigger已存在，那么更新相应的定时设置
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());

			// 按新的cronExpression表达式重新构建trigger
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

			// 按新的trigger重新设置job执行
			scheduler.rescheduleJob(triggerKey, trigger);
		}
	}

	/**
	 * 删除一个job
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void deleteScheduleJob(String jobName, String jobGroup) throws SchedulerException {
		Scheduler scheduler = _schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		scheduler.deleteJob(jobKey);
	}

	/**
	 * Description:获取当恰任务的状态信息
	 * @param jobId
	 * @throws SchedulerException
	 */
	public TaskInfo getScheduleJobInfo(int jobId) throws SchedulerException {
		Ku8Job ku8Job = _jobService.getJob(jobId);
		
		TaskLog lastTaskLog = _taskLogService.getLastLogByJobId(jobId);
		TaskInfo taskInfo = new TaskInfo();
		
		Scheduler scheduler = _schedulerFactoryBean.getScheduler();
		TriggerKey triggerKey = TriggerKey.triggerKey(ku8Job.getId()+"", ku8Job.getClusterId()+"_"+ku8Job.getResPart());
		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
		
		if(trigger != null)
		{
			Date nextRunTime = trigger.getNextFireTime();
			taskInfo.setNextTime(nextRunTime);
		}

		if(lastTaskLog==null){
			taskInfo.setStatus((byte)0);
			taskInfo.setLastTime(null);
		}else{
			taskInfo.setStatus(lastTaskLog.getStatus());
			taskInfo.setLastTime(lastTaskLog.getLastUpdated());
		}
		return taskInfo;
	}
	/**
	 * 更新job时间表达式
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateScheduleJobCron(Ku8Job job) throws SchedulerException {
		Scheduler scheduler = _schedulerFactoryBean.getScheduler();

		TriggerKey triggerKey = TriggerKey.triggerKey(job.getId()+"", job.getClusterId()+"_"+job.getResPart());

		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());

		trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

		scheduler.rescheduleJob(triggerKey, trigger);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void changJobStatus(Ku8Job job) throws SchedulerException {
		if (Constants.TASK_STATUS_NOT_RUNNING.equals(job.getTaskStatus().toString())) {
			deleteScheduleJob(job.getId()+"", job.getClusterId()+"_"+job.getResPart());
		} else if (Constants.TASK_STATUS_RUNNING.equals(job.getTaskStatus().toString())) {
			addScheduleJob(job);
		}
	}

	/**
	 * 立即执行job
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void runAJobNow(Ku8Job job) throws SchedulerException {
		Scheduler scheduler = _schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(job.getId()+"", job.getClusterId()+"_"+job.getResPart());
		scheduler.triggerJob(jobKey);
	}
//	@Transactional(propagation = Propagation.NOT_SUPPORTED)
//	public TaskScheduleJob getByNamespaceJobname(int cluserId, String jobGroup, int jobId) {
//		return _taskScheduleJobDao.selectByClusterNamespaceJobId(cluserId, jobId, jobGroup); 		
//	}
//	
//	public List<TaskScheduleJob> getAll(){
//		return _taskScheduleJobDao.selectAll();
//	}
//	
//	@Transactional(propagation = Propagation.NOT_SUPPORTED)
//	public List<TaskScheduleJob> getTaskByTenant(Integer tenantId) {
//		return _taskScheduleJobDao.selectTaskByTenant(tenantId);
//	}
//
//	@Transactional(propagation = Propagation.NOT_SUPPORTED)
//	public List<TaskScheduleJob> getTaskByOwner(String userId) {
//		return _taskScheduleJobDao.selectTaskByOwner(userId);
//	}
//	
//	@Transactional(propagation = Propagation.NOT_SUPPORTED)
//	public TaskScheduleJob getByByPrimaryKey(Integer TaskScheduleJobID){
//		return _taskScheduleJobDao.selectByPrimaryKey(TaskScheduleJobID);
//	}
//
//	@Transactional(propagation = Propagation.REQUIRED)
//	public int deleteTask(int taskId) throws SchedulerException {
//		TaskScheduleJob task = _taskScheduleJobDao.selectByPrimaryKey(taskId);
//		_taskLogMapper.deleteByJobId(task.getId());
//		deleteScheduleJob(task.getJobId()+"", task.getClusterId()+"_"+task.getJobGroup());
//		int ret = _taskScheduleJobDao.deleteByPrimaryKey(taskId);
//		return ret;
//	}
//
//	@Transactional(propagation = Propagation.REQUIRED)
//	public int updateTask(TaskScheduleJob taskScheduleJob) {
//		return _taskScheduleJobDao.updateByPrimaryKey(taskScheduleJob);
//	}
//
//	public void addTask(TaskScheduleJob task) {
//		_taskScheduleJobDao.insert(task);
//	}
//	

}
