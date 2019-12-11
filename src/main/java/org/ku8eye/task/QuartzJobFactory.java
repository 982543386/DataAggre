package org.ku8eye.task;

import org.apache.log4j.Logger;
import org.ku8eye.ctrl.JobController;
import org.ku8eye.domain.Ku8Job;
import org.ku8eye.service.JobService;
import org.ku8eye.service.TaskLogService;
import org.ku8eye.service.UserService;
import org.ku8eye.service.k8s.K8sAPIService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @Description: 计划任务执行处 无状态
 * @author yaoy
 * @date 2016年4月14日 下午9:06:50
 *
 */
public class QuartzJobFactory implements Job {
	public final Logger log = Logger.getLogger(this.getClass());
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Ku8Job scheduleJob = (Ku8Job) context.getMergedJobDataMap().get("scheduleJob");
		TaskLogService taskLogService = (TaskLogService) context.getMergedJobDataMap().get("taskLogService");
		K8sAPIService k8sAPIService = (K8sAPIService) context.getMergedJobDataMap().get("k8sAPIService");
		JobService jobService = (JobService) context.getMergedJobDataMap().get("jobService");
		UserService userService = (UserService) context.getMergedJobDataMap().get("userService");
		JobController jobController = (JobController) context.getMergedJobDataMap().get("jobController");
		TaskKu8Job tu = new TaskKu8Job();
		tu.run(scheduleJob, taskLogService, k8sAPIService, jobService, userService, jobController);
	}
}