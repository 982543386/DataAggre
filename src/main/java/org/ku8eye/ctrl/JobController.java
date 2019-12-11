package org.ku8eye.ctrl;

import io.fabric8.kubernetes.client.KubernetesClientException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ku8eye.Constants;
import org.ku8eye.bean.GridData;
import org.ku8eye.bean.project.Container;
import org.ku8eye.bean.project.Job;
import org.ku8eye.bean.project.JobSet;
import org.ku8eye.bean.project.Pod;
import org.ku8eye.bean.task.TaskInfo;
import org.ku8eye.domain.Ku8Job;
import org.ku8eye.domain.User;
import org.ku8eye.service.JobService;
import org.ku8eye.service.TaskScheduleJobService;
import org.ku8eye.service.k8s.F8toK8APIService;
import org.ku8eye.service.k8s.K8sAPIService;
import org.ku8eye.service.k8s.K8toF8APIService;
import org.ku8eye.util.AjaxReponse;
import org.ku8eye.util.JSONUtil;
import org.quartz.CronScheduleBuilder;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * @author Federico Gu 
 * Email: federico.gu@hpe.com
 * Date: 2016-05 
 * Description: Controller for Ku8 Jobs
 */
@RestController
@SessionAttributes(org.ku8eye.Constants.USER_SESSION_KEY)
public class JobController
{
	private static final Logger log = Logger.getLogger(JobController.class);

	@Autowired
	private JobService _jobService;

	@Autowired
	private K8toF8APIService _K8toF8APIService;
	
	@Autowired
	private F8toK8APIService _F8toK8APIService;
	
	@Autowired
	private K8sAPIService _k8sAPIService;
	
	@Autowired
	private TaskScheduleJobService _taskScheduleJobService;

	@RequestMapping(value = "/job/getJobs")
	public GridData getJobs(ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		List<Ku8Job> jobList;
		
		if (user.getUserType().equals(Constants.USERTYPE_ADMIN))
		{
			jobList = _jobService.getJobs();
		}
		else if (user.getUserType().equals(Constants.USERTYPE_TENANT_ADMIN))
		{
			jobList = _jobService.getJobsByTenant(user.getTenantId());
		}
		else
		{
			jobList = _jobService.getJobsByOwner(user.getUserId());
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Found " + jobList.size() + " Jobs");
		
		GridData grid = new GridData();
		grid.setData(jobList);
		return grid;
	}

	@RequestMapping(value = "/job/getJobSet")
	public Ku8Job getJobSet(int ku8ID, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		Ku8Job ku8job = _jobService.getJob(ku8ID);
		
		if(ku8job == null)
		{
			log.error("[USER: " + user.getUserId() + "] JOB NOT FOUND, id" + ku8ID);
			return null;
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8job.getOwner().equals(user.getUserId()))
		{
			log.error("[USER: " + user.getUserId() + "] Job id " + ku8ID + " does not belong to this user");
			return null;
		}
		else
		{
			
			if(ku8job.getJobType() != null && ku8job.getJobType() == Constants.KU8_SCHEDULED_JOB)
			{
				log.info("[USER: " + user.getUserId() + "] Getting Task Info for Scheduled Job");
				try
				{
					TaskInfo ku8TaskInfo = _taskScheduleJobService.getScheduleJobInfo(ku8ID);
					ku8job.setTaskInfo(ku8TaskInfo);
				} 
				catch (SchedulerException e)
				{
					log.error("[USER: " + user.getUserId() + "] Exception while getting Task Info for job id: " + ku8ID + ", " + e.getMessage());
				}
			}
			
			if(log.isInfoEnabled())
				log.info("[USER: " + user.getUserId() + "] Returning Job id: " + ku8ID);
			return ku8job;
		}
	}
	
	@RequestMapping(value = "/job/deleteJob")
	public AjaxReponse deleteJob(int ku8ID, ModelMap model)
	{	
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Deleting job id:" + ku8ID);
		
		Ku8Job ku8job = _jobService.getJob(ku8ID);
		
		if(ku8job == null)
		{
			log.error("[USER: " + user.getUserId() + "] JOB NOT FOUND, id" + ku8ID);
			return new AjaxReponse(-1, "JOB NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8job.getOwner().equals(user.getUserId()))
		{
			log.error("[USER: " + user.getUserId() + "] Job id " + ku8ID + " does not belong to this user");
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}
		
		String json = ku8job.getJsonSpec();
		JobSet jobSet = JobSet.getFromJSON(json);
		
		if(jobSet == null)
		{
			log.error("[USER: " + user.getUserId() + "] JOB DATA CORRUPTED, id" + ku8ID);
			return new AjaxReponse(-1, "JOB DATA CORRUPTED");
		}
		
		if(StringUtils.isAnyBlank(ku8job.getResPart()))
		{
			log.info("Job id " + ku8ID + " has not been deployed, skipping K8 Delete");
		}
		else
		{
			for (Job j : jobSet.getJobs())
			{
				try
				{
					boolean deleted = false;
					if(log.isInfoEnabled())
						log.info("[USER: " + user.getUserId() + "] Deleting Job: " + j.getName());
					deleted = _k8sAPIService.deleteJob(ku8job.getClusterId(), ku8job.getResPart(), j.getName());
					if(!deleted)
						log.error("[USER: " + user.getUserId() + "] Failed to delete Job, " + j.getName());
					Map<String, String> l_map = new HashMap<String, String>();
					l_map.put(Constants.KU8_PODSELECTOR, j.getName());
					List<io.fabric8.kubernetes.api.model.Pod> podList  = _k8sAPIService.getPodsByLabelsSelector(ku8job.getClusterId(), ku8job.getResPart(), l_map).getItems();
					for(io.fabric8.kubernetes.api.model.Pod pod : podList){
						boolean  retdelpod = _k8sAPIService.deletePod(ku8job.getClusterId(), ku8job.getResPart(), pod.getMetadata().getName());
						log.info("delete pod[clusterId:"+ku8job.getClusterId()+",namespace:"+ku8job.getResPart()+",pod:"+pod.getMetadata().getName()+"]"+retdelpod);
					}
				}
				catch (KubernetesClientException e)
				{
					String msg;
					if(e.getStatus() != null)
						msg = e.getStatus().getMessage();
					else
						msg = e.getMessage();
					
					log.error("[USER: " + user.getUserId() + "] Delete Job failed, " + msg);
					return new AjaxReponse(-1, "JOB DELETE FAILED<br/>"+ msg);
				}
			}
		}
		
		int res;
		try {
			res = _jobService.deleteJob(ku8ID);
		} catch (SchedulerException e) {
			log.error("[USER: " + user.getUserId() + "] Job id " + ku8ID + " failed to delete Scheduler");
			return new AjaxReponse(-1, "DELETE Job Scheduler FAILED");
		}
		
		if (res == -1)
		{
			log.error("[USER: " + user.getUserId() + "] Job id " + ku8ID + " failed to delete, SQL returned " + res);
			return new AjaxReponse(res, "JOB DELETE FAILED");
		}
		else
		{
			if(log.isInfoEnabled())
				log.info("[USER: " + user.getUserId() + "] Job id " + ku8ID + " deleted");
			return new AjaxReponse(res, "JOB <strong>" + ku8job.getName() + "</strong> DELETED");
		}
	}

	@RequestMapping(value = "/job/addJob")
	public AjaxReponse addJob(String name, String version, String note, String jsonStr, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}

		if (name.isEmpty() || version.isEmpty() || jsonStr.isEmpty())
		{
			log.error("[USER: " + user.getUserId() + "] EMPTY FIELDS JOB");
			return new AjaxReponse(-1, "PLEASE FILL ALL EMPTY FIELDS");
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Adding job name:" + name);

		int res = _jobService.addJob(user.getTenantId(), user.getUserId(), name, version, note, jsonStr);

		if (res == -1)
		{
			log.error("[USER: " + user.getUserId() + "] Job " + name + " failed to add, SQL returned " + res);
			return new AjaxReponse(res, "ADD FAILED");
		}
		else
		{
			if(log.isInfoEnabled())
				log.info("[USER: " + user.getUserId() + "] Job " + name + " added");
			return new AjaxReponse(res, "ADDED");
		}
	}

	public AjaxReponse runJob(Ku8Job ku8job, List<String> namespaces, User user)
	{
		if(log.isInfoEnabled())
		{
			log.info("[USER: " + user.getUserId() + "] Deploying job id:" + ku8job.getId() + ", with namespaces: " + namespaces);
			log.info("[USER: " + user.getUserId() + "] Setting job to deploying, status:" + Constants.KU8_APP_DEPLOYING_STATUS);
		}
		
		// Validation done
		// Get JSON
		String json = ku8job.getJsonSpec();
		
		if(log.isDebugEnabled())
			log.debug("[USER: " + user.getUserId() + "] Getting json str: " + json);

		// Parse JSON from the job
		JobSet jobSet = JobSet.getFromJSON(json);
		
		if(jobSet  == null)
		{
			ku8job.setStatus(Constants.KU8_APP_FAILED_STATUS);
			_jobService.updateJob(ku8job);
			
			log.error("[USER: " + user.getUserId() + "] JOB DATA CORRUPTED, id" + ku8job.getId());
			return new AjaxReponse(-1, "JOB DATA CORRUPTED");
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Job " + jobSet.getJobName() + " found");

		boolean failed = false;
		String failedDetails = "";
		String namespace = null;
		int progress = 100 / (namespaces.size() * jobSet.getJobs().length);
		
		//Created Job temp
		List<String> created = new ArrayList<String>();
				
		//Loop namespaces
		for (int i = 0; i < namespaces.size();)
		{	
			namespace = namespaces.get(i);
			
			for (Job j : jobSet.getJobs())
			{
				try
				{	
					if(log.isInfoEnabled())
						log.info("[USER: " + user.getUserId() + "] Creating job:" + j.getName());
					
					_K8toF8APIService.buildJob(ku8job.getClusterId(), namespace, jobSet.getJobName(), j, user);
					
					//Add to temp list
					created.add(j.getName());
					
					//Add progress
					int oldprogress = 0;
					if(ku8job.getProgress() != null)
						oldprogress = ku8job.getProgress();
					ku8job.setProgress(oldprogress + progress);
					_jobService.updateJob(ku8job);
				}
				catch (KubernetesClientException e)
				{
					String msg;
					if(e.getStatus() != null)
						msg = e.getStatus().getMessage();
					else
						msg = e.getMessage();
					
					failedDetails += msg + " <br/>";
					log.error("[USER: " + user.getUserId() + "] Deploy RC/Service failed, " + msg);
					failed = true;
				}
			}
			
			//Only 1 namespace allowed by the moment
			break;
		}

		if (failed)
		{
			//Clean orphaned Jobs
			if(log.isInfoEnabled())
				log.info("Rollback, cleaning Jobs, " + created);
			for(String name : created)
			{
				if(log.isInfoEnabled())
					log.info("Deleting Job: " + name);
				_k8sAPIService.deleteJob(ku8job.getClusterId(), namespace, name);
			}
			
			log.error("[USER: " + user.getUserId() + "] Job:" + ku8job.getName() +" failed to run, " + failedDetails);
			
			// Failed, set status to FAILED
			ku8job.setStatus(Constants.KU8_APP_FAILED_STATUS);
			_jobService.updateJob(ku8job);
			return new AjaxReponse(-1, "JOB <strong>" + ku8job.getName() + "</strong> FAILED TO RUN<br/>"+ failedDetails);
		}
		else
		{
			if(log.isInfoEnabled())
				log.info("[USER: " + user.getUserId() + "] Job:" + ku8job.getName() +" successfully deployed.");
			
			// Success, set status to DEPLOYED
			ku8job.setResPart(namespace);
			ku8job.setStatus(Constants.KU8_APP_DEPLOYED_STATUS);
			_jobService.updateJob(ku8job);
			return new AjaxReponse(1, "JOB <strong>" + ku8job.getName() + "</strong> SUCCESSFULLY DEPLOYED");
		}
	}
	
	@RequestMapping(value = "/job/runJob")
	public AjaxReponse runJob(int ku8ID, String namespaces, String cronExpression, Integer taskStatus, ModelMap model)
	{	
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}

		Ku8Job ku8job = _jobService.getJob(ku8ID);
		List<String> namespaceList = JSONUtil.toObjectList(namespaces, String.class);
		if(namespaceList == null)
		{
			log.error("[USER: " + user.getUserId() + "] JOB DATA CORRUPTED");
			return new AjaxReponse(-1, "JOB DATA CORRUPTED");
		}

		if(ku8job == null)
		{
			log.error("[USER: " + user.getUserId() + "] JOB NOT FOUND, id" + ku8ID);
			return new AjaxReponse(-1, "JOB NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8job.getOwner().equals(user.getUserId()))
		{
			log.error("[USER: " + user.getUserId() + "] Job id " + ku8ID + " does not belong to this user");
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}
		else
		{
			if(StringUtils.isEmpty(cronExpression))
			{
				// Start deploy, set status to DEPLOYING
				ku8job.setStatus(Constants.KU8_APP_DEPLOYING_STATUS);
				ku8job.setJobType(Constants.KU8_SINGLE_JOB);
				ku8job.setProgress(0);
				_jobService.updateJob(ku8job);
				
				return runJob(ku8job, namespaceList, user);
			}
			else
			{
				return addTask(ku8job, namespaceList, cronExpression, taskStatus, user);
			}
		}
	}
	
	private AjaxReponse addTask(Ku8Job ku8job, List<String> namespaceList, String cronExpression, Integer taskStatus, User user) {
		//注，只会出现一个namespace
		try 
		{
			if(StringUtils.isEmpty(cronExpression) || taskStatus == null)
			{
				String ret = "bad parameter";
				return new AjaxReponse(-1, ret);
			}
			try 
			{
				@SuppressWarnings("unused")
				CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
			} 
			catch (Exception e) 
			{
				return new AjaxReponse(-1, cronExpression+"<br/>cron表达式有误，不能被解析！");
			}

			for(String namespace : namespaceList){
				ku8job.setStatus(Constants.KU8_APP_SCHEDULED_STATUS);
				ku8job.setResPart(namespace);
				ku8job.setCronExpression(cronExpression);
				ku8job.setTaskStatus((byte)taskStatus.intValue());
				ku8job.setJobType(Constants.KU8_SCHEDULED_JOB);
				_jobService.updateJob(ku8job);
				_taskScheduleJobService.addScheduleJob(ku8job);
				log.info("add task "+ku8job.getId()+" in parition"+namespace+" of "+ku8job.getClusterId());
				break;
			}
			return new AjaxReponse(1, "ADD SCHEDULED JOB SUCCESS");

		} 
		catch (Exception e) 
		{
			ku8job.setStatus(Constants.KU8_APP_FAILED_STATUS);
			_jobService.updateJob(ku8job);
			return new AjaxReponse(-1, "ADD SCHEDULED JOB FAIL");
		}
	}

	@RequestMapping(value = "/job/getJob")
	public GridData getJob(int ku8ID, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Getting jobs for job list id:" + ku8ID);

		Ku8Job ku8job = _jobService.getJob(ku8ID);

		if(ku8job == null)
		{
			log.error("[USER: " + user.getUserId() + "] JOB NOT FOUND, id" + ku8ID);
			return null;
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8job.getOwner().equals(user.getUserId()))
		{
			log.error("[USER: " + user.getUserId() + "] Job id " + ku8ID + " does not belong to this user");
			return null;
		}
		else
		{
			try
			{
				List<Job> beanJobs = _F8toK8APIService.getJobs(ku8job.getName(), ku8job.getResPart(), ku8job.getClusterId(), user);
				
				GridData grid = new GridData();
				grid.setData(beanJobs);
				return grid;
			}
			catch (KubernetesClientException e)
			{
				log.error("[USER: " + user.getUserId() + "] Kubernetes error on getJobServices, " + e.getMessage());
				return null;
			}
		}
	}
	
//	@RequestMapping(value = "/job/getJobScheduleInfo")
//	public TaskInfo getJobScheduleInfo(int ku8ID, ModelMap model)
//	{
//		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);
//
//		if (user == null)
//		{
//			log.error("ERROR USER NOT LOGGED IN");
//			return null;
//		}
//		
//		if(log.isInfoEnabled())
//			log.info("[USER: " + user.getUserId() + "] Getting job schedule info for job id: " + ku8ID);
//
//		Ku8Job ku8job = _jobService.getJob(ku8ID);
//
//		if(ku8job == null)
//		{
//			log.error("[USER: " + user.getUserId() + "] JOB NOT FOUND, id" + ku8ID);
//			return null;
//		}
//		
//		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8job.getOwner().equals(user.getUserId()))
//		{
//			log.error("[USER: " + user.getUserId() + "] Job id " + ku8ID + " does not belong to this user");
//			return null;
//		}
//		else
//		{
//			
//			try
//			{
//				TaskInfo ku8taskInfo = _taskScheduleJobService.getScheduleJobInfo(ku8ID);
//				if(ku8taskInfo == null)
//				{
//					log.error("[USER: " + user.getUserId() + "] Task Schedule Info not found, id" + ku8ID);
//					return null;
//				}
//				
//				return ku8taskInfo;
//				
//			} 
//			catch (SchedulerException e)
//			{
//				log.error("[USER: " + user.getUserId() + "] Scheduler Exception getJobScheduleInfo, " + e.getMessage());
//				return null;
//			}
//		}
//	}
	
	@RequestMapping(value = "/job/getPods")
	public GridData getJobPods(int ku8ID, String name, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Getting pods for job: " + name +", job id: " + ku8ID);

		Ku8Job ku8job = _jobService.getJob(ku8ID);

		if(ku8job == null)
		{
			log.error("[USER: " + user.getUserId() + "] JOB NOT FOUND, id" + ku8ID);
			return null;
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8job.getOwner().equals(user.getUserId()))
		{
			log.error("[USER: " + user.getUserId() + "] Job id " + ku8ID + " does not belong to this user");
			return null;
		}
		else
		{
			try
			{
				List<Pod> beanPods = _F8toK8APIService.getPods(ku8job.getClusterId(), ku8job.getResPart(), name, user);
	
				GridData grid = new GridData();
				grid.setData(beanPods);
				return grid;
			}
			catch (KubernetesClientException e)
			{
				log.error("[USER: " + user.getUserId() + "] Kubernetes error on getPods, " + e.getMessage());
				return null;
			}
		}
	}
	
	@RequestMapping(value = "/job/getPodLogs")
	public AjaxReponse getJobPodLogs(int ku8ID, String podName, String containerName, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Getting logs for pod: " + podName + ", in Container: " + containerName);

		Ku8Job ku8job = _jobService.getJob(ku8ID);

		if (ku8job == null)
		{
			log.error("JOB NOT FOUND, id" + ku8ID);
			return new AjaxReponse(-1, "JOB NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8job.getOwner().equals(user.getUserId()))
		{
			log.error("[USER: " + user.getUserId() + "] Application id " + ku8ID + " does not belong to this user");
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}
		else
		{
			try
			{
				String log = _k8sAPIService.getPodLogByName(ku8job.getClusterId(), ku8job.getResPart(), podName, containerName);
				return new AjaxReponse(1, log);
			}
			catch (KubernetesClientException e)
			{
				String msg;
				if(e.getStatus() != null)
					msg = e.getStatus().getMessage();
				else
					msg = e.getMessage();
				
				log.error("Kubernetes error on getPodLogs, " + msg);
				return new AjaxReponse(-1, "GET POD LOGS<br/>" + msg);
			}
		}
	}
	
	@RequestMapping(value = "/job/getContainer")
	public GridData getJobContainer(int ku8ID, @RequestParam("name") String jobName, int cIndex, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Getting container: " + cIndex + ", on job: " + jobName + ", for job id:" + ku8ID);
		
		Ku8Job ku8job = _jobService.getJob(ku8ID);

		if(ku8job == null)
		{
			log.error("[USER: " + user.getUserId() + "] JOB NOT FOUND, id" + ku8ID);
			return null;
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8job.getOwner().equals(user.getUserId()))
		{
			log.error("[USER: " + user.getUserId() + "] Job id " + ku8ID + " does not belong to this user");
			return null;
		}
		else
		{
			try
			{
				Container beanContainer = _F8toK8APIService.getContainerByJob(ku8job.getName(), jobName, cIndex, ku8job.getResPart(), ku8job.getClusterId(), user);
				
				GridData grid = new GridData();
				grid.setData(beanContainer);
				return grid;
			}
			catch (KubernetesClientException e)
			{
				log.error("[USER: " + user.getUserId() + "] Kubernetes error on getJobContainer, " + e.getMessage());
				return null;
			}
		}
	}
	
	@RequestMapping(value = "/job/updateLabels")
	public AjaxReponse updateJobLabels(int ku8ID, @RequestParam("name") String jobName, String jsonStr, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Updating labels on Job:" + jobName + ", id:" + ku8ID +", lbls:" + jsonStr);

		Ku8Job ku8job = _jobService.getJob(ku8ID);
		
		if(ku8job == null)
		{
			log.error("[USER: " + user.getUserId() + "] JOB NOT FOUND, id" + ku8ID);
			return new AjaxReponse(-1, "JOB NOT FOUND");
		}
		
		if(!user.getUserType().equals(Constants.USERTYPE_ADMIN) && !ku8job.getOwner().equals(user.getUserId()))
		{
			log.error("[USER: " + user.getUserId() + "] Job id " + ku8ID + " does not belong to this user");
			return new AjaxReponse(-1, "USER UNAUTHORIZED");
		}
		else
		{
			
			@SuppressWarnings("unchecked")
			HashMap<String, String> lmap = JSONUtil.toObject(jsonStr, HashMap.class);
			lmap.put(Constants.KU8_JOBGROUP, ku8job.getName());
			
			_K8toF8APIService.updateJobLabels(ku8job.getClusterId(), ku8job.getResPart(), jobName, lmap, user);
			
			//Update DB
			JobSet jobSet = JobSet.getFromJSON(ku8job.getJsonSpec());
			for(Job j : jobSet.getJobs())
			{
				if(j.getName().equals(jobName))
				{
					j.setLabels(lmap);
					break;
				}
			}
			_jobService.updateJobJSON(ku8job.getId(), jobSet.toJSONString(), ku8job.getJsonSpec());
			
			return new AjaxReponse(1, "JOB <strong>" + jobName + "</strong> LABELS SUCCESSFULLY UPDATED");
		}
	}
	
	@RequestMapping(value = "/job/hasJobName")
	public GridData hasJobName(String jobName, ModelMap model)
	{
		User user = (User) model.get(org.ku8eye.Constants.USER_SESSION_KEY);

		if (user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return null;
		}
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Verifying Job Name: " + jobName + " in SQL");
		
		List<String> res_parts = _jobService.hasJobName(jobName);
		
		if(log.isInfoEnabled())
			log.info("[USER: " + user.getUserId() + "] Job Name: " + jobName + " exists in: " + res_parts);
		
		GridData grid = new GridData(res_parts);
		return grid;
	}
}
