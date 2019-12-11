package org.ku8eye.ctrl;

import java.util.List;

import org.apache.log4j.Logger;
import org.ku8eye.Constants;
import org.ku8eye.bean.task.TaskResponse;
import org.ku8eye.domain.Ku8Job;
import org.ku8eye.domain.TaskLog;
import org.ku8eye.domain.User;
import org.ku8eye.service.JobService;
import org.ku8eye.service.TaskLogService;
import org.ku8eye.service.TaskScheduleJobService;
import org.quartz.CronScheduleBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

@RestController
@SessionAttributes(org.ku8eye.Constants.USER_SESSION_KEY)
public class TaskScheduleJobController
{

	private Logger log = Logger.getLogger(this.toString());

	@Autowired
	private TaskScheduleJobService _taskScheduleJobService;
	@Autowired
	private TaskLogService _taskLogService;
	@Autowired
	private JobService _jobService;

	/**
	 * Description:更新指定job的数据库和quartz
	 * 
	 * @param model
	 * @param newtaskScheduleJob
	 * @return
	 */
	@RequestMapping(value = "/task/updateTask")
	public TaskResponse updateTask(int ku8ID, String cronExpression, Integer taskStatus, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);
		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new TaskResponse(1, "USER NOT LOGGED");
		}
		
		try
		{
			TaskResponse TaskResponse = new TaskResponse();
			
			try
			{
				@SuppressWarnings("unused")
				CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
			} 
			catch (Exception e)
			{
				return new TaskResponse(-1, cronExpression + "<br/>cron表达式有误，不能被解析！");
			}


			if (log.isInfoEnabled())
				log.info("[USER: " + user.getUserId() + "] Update job id:" + ku8ID);

			Ku8Job job = _jobService.getJob(ku8ID);

			if (job == null)
			{
				log.error("[USER: " + user.getUserId() + "] JOB NOT FOUND, id" + ku8ID);
				return new TaskResponse(1, "TASK NOT FOUND");
			}

			if (!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !job.getOwner().equals(user.getUserId()))
			{
				log.error("[USER: " + user.getUserId() + "] JOB " + ku8ID + " does not belong to this user");
				return new TaskResponse(1, "USER UNAUTHORIZED");
			}

			if (job.getTaskStatus() != (byte) taskStatus.intValue())
			{
				job.setCronExpression(cronExpression);
				job.setTaskStatus((byte) taskStatus.intValue());
				_taskScheduleJobService.changJobStatus(job);
			}
			if (!job.getCronExpression().equals(cronExpression) && Constants.TASK_STATUS_RUNNING.equals((byte) taskStatus.intValue()))
			{
				job.setCronExpression(cronExpression);
				_taskScheduleJobService.updateScheduleJobCron(job);
			}
			job.setCronExpression(cronExpression);
			job.setTaskStatus((byte) taskStatus.intValue());
			int ret = _jobService.updateJob(job);
			if (ret == 0)
			{
				TaskResponse.setStatus(-1);
				TaskResponse.setMessage("fail to update task");
			}

			TaskResponse.setMessage("SUCCESS");
			TaskResponse.setStatus(1);

			log.info("[USER: " + user.getUserId() + "] JOB id: " + ku8ID + " updated, cron expression: "
					+ cronExpression + ", status: " + taskStatus);

			return TaskResponse;

		} 
		catch (Exception e)
		{
			log.error("[USER: " + user.getUserId() + "] Exception on updateTask, " + e.getMessage());
			return new TaskResponse(-1, "Fail to update task");
		}
	}

	/**
	 * Description:获取指定taskId的所有log
	 * 
	 * @param model
	 * @param jobId
	 * @return
	 */
	@RequestMapping(value = "/task/getTaskLog")
	public TaskResponse getTaskLog(ModelMap model, @RequestParam("jobId") Integer jobId)
	{
		TaskResponse taskResponse = new TaskResponse();
		try
		{
			User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

			if (user == null)
			{
				log.error("ERROR USER NOT LOGGED IN");
				return new TaskResponse(1, "USER NOT LOGGED");
			}

			if (log.isInfoEnabled())
				log.info("[USER: " + user.getUserId() + "] Get taskLog taskId:" + jobId);

			Ku8Job job = _jobService.getJob(jobId);

			if (job == null)
			{
				log.error("[USER: " + user.getUserId() + "] TASK NOT FOUND, taskId" + jobId);
				return new TaskResponse(1, "JOB NOT FOUND");
			}

			if (!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !job.getOwner().equals(user.getUserId()))
			{
				log.error("[USER: " + user.getUserId() + "] TASK " + jobId + " does not belong to this user");
				return new TaskResponse(1, "USER UNAUTHORIZED");
			}

			List<TaskLog> taskLogs = _taskLogService.getLogByJobId(jobId);
			taskResponse.setData(taskLogs);
		} catch (Exception e)
		{
			log.error(e);
			e.printStackTrace();
			taskResponse.setStatus(1);
			taskResponse.setMessage("fail to get TaskLog");
		}
		return taskResponse;
	}

	/**
	 * Description:删除指定id的task的log
	 * 
	 * @param model
	 * @param taskLogId
	 * @return
	 */
	@RequestMapping(value = "/task/deleteTaskLog")
	public TaskResponse deleteTaskLog(ModelMap model, @RequestParam("taskLogId") String taskLogId)
	{
		TaskResponse taskResponse = new TaskResponse();
		try
		{
			User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

			if (user == null)
			{
				log.error("ERROR USER NOT LOGGED IN");
				return new TaskResponse(1, "USER NOT LOGGED");
			}

			if (log.isInfoEnabled())
				log.info("[USER: " + user.getUserId() + "] Deleting taskLog id:" + taskLogId);

			TaskLog taskLog = _taskLogService.getTaskLogByByPrimaryKey(taskLogId);

			if (taskLog == null)
			{
				log.error("[USER: " + user.getUserId() + "] TASKLOG NOT FOUND, id" + taskLogId);
				return new TaskResponse(1, "TASKLOG NOT FOUND");
			}

			Ku8Job job = _jobService.getJob(taskLog.getJobId());

			if (!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !job.getOwner().equals(user.getUserId()))
			{
				log.error("[USER: " + user.getUserId() + "] TASK " + taskLogId + " does not belong to this user");
				return new TaskResponse(1, "USER UNAUTHORIZED");
			}

			int ret = _taskLogService.deleteLogByTaskId(taskLogId);
			if (ret != 1)
			{
				taskResponse.setStatus(1);
				taskResponse.setMessage("fail to delete taskLog");
			}
		} catch (Exception e)
		{
			log.error(e);
			e.printStackTrace();
			taskResponse.setStatus(1);
			taskResponse.setMessage("fail to delete taskLog");
		}
		return taskResponse;
	}

	// /**
	// * Description:获取数据库中存在的定时任务
	// * @param model
	// * @return
	// */
	// @RequestMapping(value = "/task/getTasks")
	// public TaskResponse getAlltask(ModelMap model) {
	// User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);
	//
	// try {
	// if (user == null)
	// {
	// log.error("ERROR USER NOT LOGGED IN");
	// return new TaskResponse(1,"ERROR USER NOT LOGGED IN");
	// }
	//
	// List<TaskScheduleJob> taskScheduleJobList;
	//
	// if (user.getUserType().equals(Constants.USERTYPE_ADMIN))
	// {
	// taskScheduleJobList = _taskScheduleJobService.getAll();
	// }
	// else if (user.getUserType().equals(Constants.USERTYPE_TENANT_ADMIN))
	// {
	// taskScheduleJobList =
	// _taskScheduleJobService.getTaskByTenant(user.getTenantId());
	// }
	// else
	// {
	// taskScheduleJobList =
	// _taskScheduleJobService.getTaskByOwner(user.getUserId());
	// }
	//
	// if(log.isInfoEnabled())
	// log.info("[USER: " + user.getUserId() + "] Found " +
	// taskScheduleJobList.size() + " Tasks");
	// return new TaskResponse(0,null,taskScheduleJobList);
	// } catch (Exception e) {
	// log.error(e);
	// e.printStackTrace();
	// return new TaskResponse(1,"fail to list task");
	// }
	// }

	// /**
	// * Description:向数据库和quartz添加任务，每个cluster_namespace占用一条数据库记录和quartz任务记录
	// * @param model
	// * @param taskScheduleJob
	// * @return
	// */
	// @RequestMapping(value = "/task/addTask")
	// public TaskResponse addTask(ModelMap model,TaskScheduleJob
	// taskScheduleJob) {
	// //注，只会出现一个namespace
	// TaskResponse taskResponse = new TaskResponse();
	// User user = (User)model.get(org.ku8eye.Constants.USER_SESSION_KEY);
	// taskScheduleJob.setConcurrent(new Byte("0"));//设置不可同步执行
	// try {
	// if(StringUtils.isEmpty(taskScheduleJob.getJobGroup())||StringUtils.isEmpty(taskScheduleJob.getJobId())
	// ||StringUtils.isEmpty(taskScheduleJob.getConcurrent())||StringUtils.isEmpty(taskScheduleJob.getCronExpression())
	// ||StringUtils.isEmpty(taskScheduleJob.getJobStatus())){
	// String ret = "bad parameter";
	// log.warn(ret+" "+taskScheduleJob.toString());
	// taskResponse.setStatus(1);
	// taskResponse.setMessage(ret);
	// return taskResponse;
	// }
	// try {
	// @SuppressWarnings("unused")
	// CronScheduleBuilder scheduleBuilder =
	// CronScheduleBuilder.cronSchedule(taskScheduleJob.getCronExpression());
	// } catch (Exception e) {
	// taskResponse.setStatus(1);
	// taskResponse.setMessage("cron表达式有误，不能被解析！");
	// return taskResponse;
	// }
	// List<String> namespacesList =
	// Arrays.asList(taskScheduleJob.getJobGroup().split(","));
	// for(String namespaces : namespacesList){
	// TaskScheduleJob queryTaskScheduleJob =
	// _taskScheduleJobService.getByNamespaceJobname(0,namespaces,taskScheduleJob.getJobId());
	// if(queryTaskScheduleJob!=null){
	// log.info("job "+taskScheduleJob.getJobId()+" exist in
	// partition"+namespaces);
	// return new TaskResponse(1,"job "+taskScheduleJob.getJobId()+" exist in
	// partition"+namespaces);
	// }
	// break;
	// }
	//
	// taskScheduleJob.setOwner(user.getUserId());
	// taskScheduleJob.setTenantId(user.getTenantId());
	// taskScheduleJob.setClusterId(0);
	// taskScheduleJob.setBeanClass(DEFAULCLASS);
	// taskScheduleJob.setMethodName(DEFAULMETHOD);
	// taskScheduleJob.setLastUpdated(new Date());
	// for(String namespaces : namespacesList){
	// taskScheduleJob.setJobGroup(namespaces);
	// _taskScheduleJobService.addTask(taskScheduleJob);
	// _taskScheduleJobService.addScheduleJob(taskScheduleJob);
	// log.info("add task "+taskScheduleJob.getJobId()+" in
	// parition"+namespaces+" of "+taskScheduleJob.getClusterId());
	// break;
	// }
	// } catch (Exception e) {
	// log.error(e);
	// e.printStackTrace();
	// taskResponse.setStatus(1);
	// taskResponse.setMessage("fail to add task");
	// }
	// return taskResponse;
	// }

	/**
	 * Description:通过task id 删除指定任务
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	// @RequestMapping(value = "/task/deleteTask")
	// public TaskResponse deleteTask(ModelMap model,@RequestParam("taskId")
	// Integer id) {
	// TaskResponse TaskResponse = new TaskResponse();
	// try {
	// User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);
	//
	// if (user == null)
	// {
	// log.error("ERROR USER NOT LOGGED IN");
	// return new TaskResponse(1, "USER NOT LOGGED");
	// }
	//
	// if(log.isInfoEnabled())
	// log.info("[USER: " + user.getUserId() + "] Deleting task id:" +id);
	//
	// TaskScheduleJob taskScheduleJob =
	// _taskScheduleJobService.getByByPrimaryKey(id);
	//
	// if(taskScheduleJob == null)
	// {
	// log.error("[USER: " + user.getUserId() + "] TASK NOT FOUND, id" + id);
	// return new TaskResponse(1, "TASK NOT FOUND");
	// }
	//
	// if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) &&
	// !taskScheduleJob.getOwner().equals(user.getUserId()))
	// {
	// log.error("[USER: " + user.getUserId() + "] Task " + id + " does not
	// belong to this user");
	// return new TaskResponse(1, "USER UNAUTHORIZED");
	// }
	//
	// int re = _taskScheduleJobService.deleteTask(id);
	// if (re == 0) {
	// TaskResponse.setStatus(1);
	// TaskResponse
	// .setMessage("fail to delete task");
	// }
	// } catch (Exception e) {
	// log.error(e);
	// e.printStackTrace();
	// TaskResponse.setStatus(1);
	// TaskResponse.setMessage("fail to delete task");
	// }
	// return TaskResponse;
	// }

	/**
	 * Description:获取指定namespace下的所有job
	 * 
	 * @param model
	 * @param namespace
	 * @return
	 */
	// @RequestMapping(value = "/task/getJobs")
	// public TaskResponse getJobs(ModelMap model,
	// @RequestParam("namespace") String namespace) {
	// TaskResponse taskResponse = new TaskResponse();
	// try {
	// User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);
	//
	// if (user == null)
	// {
	// log.error("ERROR USER NOT LOGGED IN");
	// return new TaskResponse(1, "USER NOT LOGGED");
	// }
	//
	// List<Integer> clusters = new ArrayList<Integer>();
	// clusters.add(0);
	//
	// List<String> jobs = new ArrayList<String>();
	//
	// for (int clusterId : clusters)
	// {
	// if(log.isInfoEnabled())
	// log.info("Looping cluster: " + clusterId);
	// jobs.addAll(_ku8ResPartionService.getAllJobName(clusterId, namespace));
	//
	// if(jobs.size()==0)
	// {
	// log.info("No jobs found in "+namespace+", continue");
	// continue;
	// }
	//
	// if(log.isInfoEnabled())
	// log.info("Found namespaces: " + jobs);
	// }
	// taskResponse.setData(jobs);
	// } catch (Exception e) {
	// log.error(e);
	// e.printStackTrace();
	// taskResponse.setStatus(1);
	// taskResponse.setMessage("fail to update task");
	// }
	// return taskResponse;
	// }

}
