package org.ku8eye;

public class Constants
{

	public static final String USER_SESSION_KEY = "user";

	// user type constant
	public static final String USERTYPE_ADMIN = "1";
	public static final String USERTYPE_TENANT = "0";

	public static final int TENANT_DEF_ID = 1;
	public static final int USERTYPE_TENANT_ADMIN = 3;
	public static final int USERTYPE_GROUP_ADMIN = 2;
	public static final String USER_GROUP_ALL = "ALL";

	public static final int ku8_master_id = 1;

	public static final String EXTERNAL_URL_ROOT = "/external";
	public static final String k8sparam_cluster_docker0_ip_srange = "cluster-docker0-ip-range";
	public static final byte K8S_TYPE_API_SERVICE = 1;
	public static final byte K8S_TYPE_ETCD_SERVICE = 2;
	public static final byte K8S_TYPE_REGISTRY_SERVICE = 3;
	public static final byte K8S_SERICE_STATUS_OK = 1;
	public static final byte K8S_SERICE_STATUS_ERR = -1;
	public static final byte HOST_USAGED = 1;

	public static final byte K8S_AUTO_NOT_INSTALL = 1;
	public static final byte K8S_AUTO_INSTALLED = 2;
	public static final byte K8S_MANUAL_INSTALLED = 3;

	// ku8微服务以及Ku8RcInst对象的的Flag标记
	public static final byte DELETED_FLAG = -1;// 删除标记
	public static final byte NO_K8S_SRV_RES_NOT_FOUND_FLAG_ = -2;// 找不到对应的k8s
																	// service资源描述
	public static final byte NO_K8S_RC_RES_NOT_FOUND_FLAG_ = -3;// 找不到对应的k8s
																// rc资源描述
	public static final byte NO_K8S_RC_RES_SYNED_FLAG_ = 1;// k8s RES资源同步
	public static final byte NO_K8S_RC_RES_NOT_SYNED_FLAG_ = -1;// k8s RES资源不同步

	// Ku8 Application/Public Service 状态
	public static final byte KU8_APP_INIT_STATUS = 0;// 未发布
	public static final byte KU8_APP_DEPLOYING_STATUS = 1;// 正在发布中
	public static final byte KU8_APP_DEPLOYED_STATUS = 2;// 已发布并且运行正常
	public static final byte KU8_APP_SCHEDULED_STATUS = 3;
	public static final byte KU8_APP_FAILED_STATUS = -1;// 已发布并且全部服务异常
	public static final byte KU8_APP_PART_FAILED_STATUS = -2;// 已发布并且部分服务异常

	public static final String KU8_UNLIMITED = "Unlimited";
	public static final String KU8_APPNAME = "appname";
	public static final String KU8_APPTYPE = "apptype";
	public static final String KU8_PUBLIC = "public";
	public static final String KU8_PRIVATE = "private";
	public static final String KU8_DEFAULT_NAMESPACE = "default";
	public static final String KU8_JOBGROUP = "jobgroup";
	public static final String KU8_PODSELECTOR = "name";
	
	public static final int KU8_SINGLE_JOB = 0;
	public static final int KU8_SCHEDULED_JOB = 1;

	public static final String BASEIMAGE = "google_containers";
	
	public static final String TASK_STATUS_RUNNING = "1";//运行
	public static final String TASK_STATUS_NOT_RUNNING = "0";//未运行
	public static final String TASK_CONCURRENT_IS = "1";//覆盖执行任务
	public static final String TASK_CONCURRENT_NOT = "0";//等待任务执行完，再执行任务
	
	public static final String KU8_CONSOLE_EXIT = "exit";
	
	public static final byte ORDER_INIT_STATUS = 0;// 待处理
	public static final byte ORDER_DEPLOYING_STATUS = 1;// 处理中
	public static final byte ORDER_DEPLOYED_STATUS = 2;// 已处理
	public static final byte ORDER_REFUSED_STATUS = 3;//拒绝
	public static final byte ORDER_FAILED_STATUS = -1;// 已发布并且全部服务异常
	
	public static final byte ABLITY_DSJ_TYPE = 1;// 大数据服务
	public static final byte ABLITY_DB_TYPE = 2;// 数据库服务
	public static final byte ABLITY_YW_TYPE = 3;// 业务服务
}
