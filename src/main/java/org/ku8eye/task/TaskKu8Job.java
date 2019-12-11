package org.ku8eye.task;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.batch.Job;
//k8 client 1.9 import io.fabric8.kubernetes.api.model.extensions.Job;
import io.fabric8.kubernetes.client.KubernetesClientException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ku8eye.Constants;
import org.ku8eye.bean.project.JobSet;
import org.ku8eye.ctrl.JobController;
import org.ku8eye.domain.Ku8Job;
import org.ku8eye.domain.TaskLog;
import org.ku8eye.domain.User;
import org.ku8eye.service.JobService;
import org.ku8eye.service.TaskLogService;
import org.ku8eye.service.UserService;
import org.ku8eye.service.k8s.K8sAPIService;
import org.ku8eye.util.AjaxReponse;
import org.springframework.stereotype.Component;

/**
 * @Description: ku8相关任务的处理类
 * @author yaoy
 * @date 2016年4月14日 下午9:03:00
 *
 */
@Component
public class TaskKu8Job {
	public final Logger log = Logger.getLogger(this.getClass());
	private Ku8Job _scheduleJob; 
	private TaskLogService _taskLogService; 
	private K8sAPIService _k8sAPIService;
	private JobService _jobService;
	private UserService _userService;
	private JobController _jobController;
	TaskLog taskLog = new TaskLog();
	Ku8Job ku8job=null;
	
	/**
	 * Description:运行job
	 * @param _scheduleJob
	 * @param _taskLogService
	 * @param _k8sAPIService
	 */
	public void run(Ku8Job scheduleJob, TaskLogService taskLogService, K8sAPIService k8sAPIService, 
			JobService jobService, UserService userService, JobController jobController) {
		_scheduleJob = scheduleJob;
		_taskLogService = taskLogService;
		_k8sAPIService=k8sAPIService;
		_jobService=jobService;
		_userService=userService;
		_jobController = jobController;
		User user = _userService.getUserByUserId(_scheduleJob.getOwner());
		
		log.info("job[clusterId:"+_scheduleJob.getClusterId()+",namespace:"+_scheduleJob.getResPart()+",jobId:"+_scheduleJob.getId()+"]----------启动并运行");
		try{
			taskLog.setJobId(_scheduleJob.getId());
			taskLog.setId(_scheduleJob.getId()+new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
			taskLog.setStartTime(new Date());
			taskLog.setStatus(new Byte("1"));
			taskLog.setHint("Runnning");
			taskLog.setLastUpdated(new Date());
			_taskLogService.insert(taskLog);
			int clusterId=_scheduleJob.getClusterId();
			String namespaces = _scheduleJob.getResPart();
			ku8job = _jobService.getJob(_scheduleJob.getId());
			if(ku8job == null)
			{
				log.error("JOB NOT FOUND, id" + _scheduleJob.getId());
				updateLog("-1","JOB NOT FOUND, id" + _scheduleJob.getId());
			}
			
			
			if(Constants.KU8_APP_INIT_STATUS!=ku8job.getStatus()){
				
				String json = ku8job.getJsonSpec();
				JobSet jobSet = JobSet.getFromJSON(json);
				List<String> namespacesList = Arrays.asList(namespaces.split(","));

				
				for (org.ku8eye.bean.project.Job j : jobSet.getJobs())
				{
					for(String namespace : namespacesList){
						Job job = _k8sAPIService.getJobByName(clusterId, namespace, j.getName());
						if(job!=null){
							if(1==existRunningJob(job,clusterId,namespace,j.getName(),taskLog)){
								return;//存在正在运行的job，则退出不执行
							}
						}
					}
					for(String namespace : namespacesList){
						try
						{
							boolean deleted = false;
							if(log.isInfoEnabled())
								log.info("[USER: " + user.getUserId() + "] Deleting Job: " + j.getName());
							deleted = _k8sAPIService.deleteJob(ku8job.getClusterId(), namespace, j.getName());
							if(!deleted)
								log.error("[USER: " + user.getUserId() + "] Failed to delete Job, " + j.getName());
							Map<String, String> l_map = new HashMap<String, String>();
							l_map.put(Constants.KU8_PODSELECTOR, j.getName());
							List<Pod> podList  = _k8sAPIService.getPodsByLabelsSelector(clusterId,namespace, l_map).getItems();
							for(Pod pod : podList){
								boolean  retdelpod = _k8sAPIService.deletePod(clusterId,namespace, pod.getMetadata().getName());
								log.info("delete pod[clusterId:"+clusterId+",namespace:"+namespace+",pod:"+pod.getMetadata().getName()+"]"+retdelpod);
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
							updateLog("-1","JOB DELETE FAILED:"+msg);
						}
					}
					
				}
			}

		runJob(ku8job, namespaces, user);

		}catch(Exception pe){
			pe.printStackTrace();
			taskLog.setEndTime(new Date());
			updateLog("-1",pe.toString());			
		}finally{
			ku8job.setStatus(Constants.KU8_APP_SCHEDULED_STATUS);
			_jobService.updateJob(ku8job);
		}

	}
	
	private int existRunningJob(Job job, int clusterId, String namespace, String jobName, TaskLog taskLog){

		try{
			if(1==job.getStatus().getActive()){
				log.info("job[clusterId:"+clusterId+",namespace:"+namespace+",jobName:"+jobName+"] is runnig,exist");
				taskLog.setEndTime(new Date());
				updateLog("-1","job[clusterId:"+clusterId+",namespace:"+namespace+",jobName:"+jobName+"] is runnig,exist");
				return 1;
			}
			return 0;
		}catch(Exception e){
			if(!"java.lang.NullPointerException".equals(e.toString())){
				throw e;
			}
			return 0;
		}

//		Job f8Job = new JobBuilder().withKind("Job")
//				.withNewMetadata()
//				.withName(jobName)
//				.withNamespace(namespace)
//				.withLabels(job.getMetadata().getLabels())
//				.endMetadata()
//				.withNewSpec()
//				.withCompletions(job.getSpec().getCompletions())
//				.withParallelism(job.getSpec().getParallelism())
//				.withNewTemplate()
//				.withNewMetadata()
//				.withLabels(job.getSpec().getTemplate().getMetadata().getLabels())
//				.endMetadata()
//				.withSpec(job.getSpec().getTemplate().getSpec())
////				.withNewSpec()
////				.withContainers(job.getSpec().getTemplate().getSpec().getContainers())
////				.withVolumes(job.getSpec().getTemplate().getSpec().getVolumes())
////				.withRestartPolicy("Never")
////				.endSpec()
//				.endTemplate()
//				.endSpec()
//				.build();
//		boolean del = _k8sAPIService.deleteJob(clusterId,namespace,jobName);
//		log.info("delete job[clusterId:"+clusterId+",namespace:"+namespace+",jobName:"+jobName+"]"+del);
//		Map<String, String> l_map = new HashMap<String, String>();
//		l_map.put("job-name", jobName);
//		List<Pod> podList  = _k8sAPIService.getPodsByLabelsSelector(clusterId,namespace, l_map).getItems();
//		for(Pod pod : podList){
//			boolean  retdelpod = _k8sAPIService.deletePod(clusterId,namespace, pod.getMetadata().getName());
//			log.info("delete pod[clusterId:"+clusterId+",namespace:"+namespace+",jobName:"+pod.getMetadata().getName()+"]"+retdelpod);
//		}
//		
//		
//		Job response_f8Job = _k8sAPIService.createJob(clusterId,namespace, f8Job);
//		if(response_f8Job!=null){
//			log.info("success create job[clusterId:"+clusterId+",namespace:"+namespace+",jobName:"+jobName+"]");
//			try{
//				while(job.getStatus().getActive()==1){
//					log.info("job[clusterId:"+clusterId+",namespace:"+namespace+",jobName:"+jobName+"] is runnig, continue");
//					try {
//						Thread.sleep(1000*60);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					job = _k8sAPIService.getJobByName(clusterId,namespace,jobName);
//				}
//			}catch(Exception e){
//				if(!"java.lang.NullPointerException".equals(e.toString())){
//					return;
//				}
//			}
//			updateLog(taskLog,"2","success finish");
//		}else{
//			log.info("fail create job[clusterId:"+clusterId+",namespace:"+namespace+",jobName:"+jobName+"]");
//			updateLog(taskLog,"-1","fail create job[clusterId:"+clusterId+",namespace:"+namespace+",jobName:"+jobName+"]");
//		}
	}
	
	private void runJob(Ku8Job ku8job, String namespaces, User user)
	{	
		List<String> namespaceList = new ArrayList<String>();
		namespaceList.add(namespaces);
		
		AjaxReponse response = _jobController.runJob(ku8job, namespaceList, user);
		taskLog.setEndTime(new Date());
		String status;
		if(response.getStatus()==1){
			status="2";
		}else{
			status=String.valueOf(response.getStatus());
		}
		updateLog(status, response.getMessage());
	}
	
	private void updateLog(String status, String hint){
		taskLog.setStatus(new Byte(status));
		taskLog.setHint(hint);
		taskLog.setLastUpdated(new Date());
		_taskLogService.update(taskLog);
	}
	
}
