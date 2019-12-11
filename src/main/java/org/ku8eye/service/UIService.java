package org.ku8eye.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ku8eye.Constants;
import org.ku8eye.bean.ui.Menu;
import org.ku8eye.domain.Host;
import org.ku8eye.domain.Ku8Project;
import org.ku8eye.domain.Ku8ResPartion;
import org.ku8eye.domain.User;
import org.ku8eye.mapping.HostMapper;
import org.ku8eye.mapping.Ku8ProjectMapper;
import org.ku8eye.mapping.Ku8ResPartionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UIService {
	private Logger log = Logger.getLogger(this.toString());

	private static final String MENU_TYPE_ZONE = "1";

	private static final String MENU_TYPE_CLUSTER_GROUP = "2";

	private static final String MENU_TYPE_CLUSTER_NODE = "3";

	private static final String MENU_TYPE_HOST_GROUP = "4";

	private static final String MENU_TYPE_HOST_NODE = "5";

	private static final String MENU_TYPE_PROJECT_GROUP = "6";

	private static final String MENU_TYPE_PROJECT_NODE = "7";

	private static final String MENU_TYPE_PROJECT_FRAMEWORK = "8";

	private static final String MENU_TYPE_PROJECT_USER = "9";
	
	private static final String MENU_TYPE_DOCKER_MANAGE = "12";
	
	private static final String MENU_TYPE_MY_ORDER = "13";
	
	private static final String MENU_TYPE_APPLY_CICD = "14";
	
	private static final String MENU_TYPE_APPLY = "15";
	
	private static final String MENU_TYPE_APPLY_WIZARD = "16";
	
	private static final String MENU_TYPE_GENERAL_MADE = "17";
	
	private static final String MENU_TYPE_CUSTOM_MADE = "18";
	
	private static final String MENU_TYPE_CICD_DETAIL = "19";
	
	private static final String MENU_TYPE_ORDER_DETAIL = "20";
	
	private static final String MENU_TYPE_CICD_ORDER_DETAIL = "21";
	
	@Autowired
	private Ku8ProjectMapper ku8ProjectDao;
	@Autowired
	private HostMapper hostDao;

	@Autowired
	private Ku8ResPartionMapper Ku8ResPartionDao;

	/**
	 * fetch current user's menu
	 * 
	 * @param curUser
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Menu> generateMenus(User curUser) {
		List<Menu> menus = new ArrayList<Menu>();

		
		Menu add_application = new Menu("add_application", "ku8eye添加", "add_application.html",
				MENU_TYPE_HOST_NODE);
		//工业互联网
		//设备管理
		Menu device_manage = new Menu("device_manage", "设备管理", "", MENU_TYPE_PROJECT_GROUP);
		Menu device_manage_list = new Menu("device_manage_list", "设备管理", "iiot/device/device_list.html",
				MENU_TYPE_HOST_NODE);
		//边缘节点
		Menu edge_node = new Menu("edge_node", "边缘节点", "", MENU_TYPE_PROJECT_GROUP);
		Menu edge_node_monitor = new Menu("edge_node_monitor", "边缘节点列表", "iiot/node/edge_node_monitor.html",
				MENU_TYPE_HOST_NODE);
		Menu edge_node_manage = new Menu("edge_node_manage", "边缘节点管理", "iiot/node/edge_node_manage.html",
				MENU_TYPE_HOST_NODE);
		//边缘应用
		Menu edge_app = new Menu("edge_app", "边缘应用", "", MENU_TYPE_PROJECT_GROUP);
		Menu edge_app_deploy = new Menu("edge_app_deploy", "边缘应用部署", "iiot/app/edge_app_deploy.html",
				MENU_TYPE_HOST_NODE);
		Menu edge_app_deploy_diy = new Menu("edge_app_deploy_diy", "边缘应用自定义", "iiot/app/edge_app_deploy_diy.html",
				MENU_TYPE_HOST_NODE);
		Menu edge_app_monitor = new Menu("edge_app_monitor", "边缘应用监控", "iiot/app/edge_app_monitor.html",
				MENU_TYPE_HOST_NODE);
		//边缘应用模板
		Menu edge_app_template = new Menu("edge_app_template", "边缘应用模板", "", MENU_TYPE_PROJECT_GROUP);
		Menu edge_app_add = new Menu("edge_app_add", "新建应用模板", "iiot/app/edge_app_add.html",
				MENU_TYPE_HOST_NODE);
		Menu edge_app_templates = new Menu("edge_app_templates", "边缘应用模板", "iiot/app/edge_app_templates.html",
				MENU_TYPE_HOST_NODE);
		
		
		Menu edge_app_template_manage = new Menu("edge_app_template_manage", "边缘应用模板", "iiot/app/edge_app_template_manage.html",
				MENU_TYPE_HOST_NODE);
		//边缘镜像库
		Menu edge_image = new Menu("edge_image", "边缘镜像库", "", MENU_TYPE_PROJECT_GROUP);
		Menu edge_image_manage = new Menu("edge_image_manage", "镜像列表", "iiot/image/edge_image_manage.html",
				MENU_TYPE_HOST_NODE);
		//边缘平台管理
		Menu edge_admin = new Menu("edge_admin", "边缘平台管理", "", MENU_TYPE_PROJECT_GROUP);
		Menu edge_admin_manage = new Menu("edge_admin_manage", "edge系统管理", "iiot/admin/edge_admin_manage.html",
				MENU_TYPE_HOST_NODE);
		//边缘市场
		Menu edge_market = new Menu("edge_market", "边缘市场", "", MENU_TYPE_PROJECT_GROUP);
		Menu edge_market_page = new Menu("edge_market_page", "服务列表", "iiot/market/edge_market_page.html",
				MENU_TYPE_HOST_NODE);
		
		//数据同步
		Menu data_communication = new Menu("data_communication", "数据同步", "", MENU_TYPE_PROJECT_GROUP);
		Menu rabbitmq_web_app = new Menu("rabbitmq_web_app", "RabbitMQ监控", "iiot/dataComm/rabbitmq_web.html",
				MENU_TYPE_HOST_NODE);
		Menu device_platform = new Menu("device_platform", "设备数据采集平台", "iiot/device/device_platform.html",
				MENU_TYPE_HOST_NODE);
		Menu device_dashboard = new Menu("device_dashboard", "设备面板", "iiot/device/device_dashboard.html",MENU_TYPE_HOST_NODE);
				
		//Menu device_dashboard = new Menu("device_dashboard", "dashboard", "http://10.1.24.91:31685/login",MENU_TYPE_HOST_NODE);
		
		
		
		
		
		// 用户Dashboard
		Menu dashboard_user = new Menu("dashboard_user", "DASHBOARD ", "", MENU_TYPE_HOST_GROUP);

		// Dashboard
		Menu dashboard = new Menu("dashboard", "集群管理 ", "", MENU_TYPE_HOST_GROUP);

		Menu dashboard_dcos_cluster = new Menu("dashboard_dcos_cluster", "集群列表", "dcos_cluster.html",
				MENU_TYPE_HOST_NODE);
		// Menu dashboard_dcos_monitor_custom_rule = new
		// Menu("dashboard_dcos_cluster_node", "调度策略",
		// "dcos_monitor_custom_rule.html", MENU_TYPE_HOST_NODE);
		Menu dashboard_dcos_cluster_log = new Menu("dashboard_dcos_cluster_log", "DCOS集群日志", "dcos_cluster_log.html",
				MENU_TYPE_HOST_NODE);

		// 调度策略
		Menu dashboard_dcos_monitor_custom = new Menu("dashboard_dcos_monitor_custom", "调度策略 ", "", MENU_TYPE_ZONE);
		Menu dashboard_dcos_monitor_custom_rule = new Menu("dashboard_dcos_monitor_custom_rule", "调度策略",
				"dcos_monitor_custom_rule.html", MENU_TYPE_HOST_GROUP);
		Menu select_image = new Menu("select_image", "查找镜像", "select_image.html", MENU_TYPE_HOST_GROUP);

		// Applications
		Menu ability = new Menu("application", "能力管理", "", MENU_TYPE_PROJECT_GROUP);

		Menu application_list_new = new Menu("application_list_new", "容器管理", "application_main_bak.html",
				MENU_TYPE_PROJECT_NODE);
		
		Menu k8order_helm = new Menu("k8order_helm", "我的容器服务", "k8order/containerService/containerAdminK8_admin.html",
				MENU_TYPE_PROJECT_NODE);
		
		Menu k8order_cicd = new Menu("k8order_cicd", "Devops服务管理", "cicd/service/admin/service_cicd_admin.html",
				MENU_TYPE_PROJECT_NODE);

		// 用户版的能力管理

		Menu ability_manage = new Menu("ability_manage", "用户能力", "", MENU_TYPE_PROJECT_GROUP);

		Menu application_list_user = new Menu("application_list_user", "容器管理", "application_main.html",
				MENU_TYPE_PROJECT_NODE);

		Menu application_list_Devops = new Menu("application_list_Devops", "敏捷开发", "Devops/devops_list_user.html",
				MENU_TYPE_PROJECT_NODE);

		Menu application_list_Devops_test = new Menu("application_list_Devops", "功能测试页",
				"Devops/order_list_user_test.html", MENU_TYPE_PROJECT_NODE);

		Menu database_user = new Menu("database_user", "数据库申请向导", "guide/database.html", MENU_TYPE_PROJECT_NODE);

		Menu database_user_test = new Menu("database_user_test", "向导测试", "guide/database_user2.html",
				MENU_TYPE_PROJECT_NODE);

		// 容器服务菜单
		Menu container_service = new Menu("container_service", "容器服务", "", MENU_TYPE_DOCKER_MANAGE);
		Menu container_service_manage = new Menu("container_service_manage", "我的容器服务",
				"k8order/containerService/containerAdminK8.html", MENU_TYPE_GENERAL_MADE);
		Menu custom_service_manage = new Menu("custom_service_manage", "我的定制服务",
				/*"k8order/containerService/containerAdminOld.html", MENU_TYPE_PROJECT_NODE);*/
				"order/order_list_user.html", MENU_TYPE_CUSTOM_MADE);

		// devops服务菜单
		Menu devops_service = new Menu("devops_service", "DevOps服务 ", "", MENU_TYPE_APPLY_CICD);
		Menu cicd_service = new Menu("cicd_service", "DevOps服务 ", "cicd/service/user/service_cicd_user.html",
				MENU_TYPE_CICD_DETAIL);

		Menu docker_image_user = new Menu("images", "镜像库 ", "", MENU_TYPE_PROJECT_GROUP);

		Menu docker_list_user = new Menu("docker_list_user", "我的镜像库", "application_docker_user.html",
				MENU_TYPE_PROJECT_NODE);

		/* 新增功能 */
		Menu k8orders = new Menu("k8orders", "我的订单 ", "", MENU_TYPE_MY_ORDER);
		Menu k8orders_detail = new Menu("k8orders_detail", "订单详情", "catalog/all_k8order.html", MENU_TYPE_ORDER_DETAIL);
		Menu k8orders_detail_admin = new Menu("k8orders_detail_admin", "容器服务订单管理", "k8order/admin/all_k8order_admin.html", MENU_TYPE_ORDER_DETAIL);
		Menu cicdOrder_detail = new Menu("cicdOrder_detail", "CICD订单详情", "cicd/order/cicd_catalog_user.html", MENU_TYPE_CICD_ORDER_DETAIL);
		/* 详情页面 */
		Menu order_detail = new Menu("order_detail", "我的容器服务", "", MENU_TYPE_PROJECT_GROUP);
		/* 中间件详情 */
		Menu k8order_detail_bigdata = new Menu("k8order_detail_bigdata", "中间件订单详情", "catalog/middleware.html",
				MENU_TYPE_PROJECT_NODE);
		/* 数据库详情 */
		Menu k8order_detail_database = new Menu("k8order_detail_database", "数据库订单详情", "catalog/database.html",
				MENU_TYPE_PROJECT_NODE);
		/* 大数据详情 */
		Menu k8order_detail_middleware = new Menu("k8order_detail_middleware", "大数据订单详情", "catalog/bigdata.html",
				MENU_TYPE_PROJECT_NODE);
		/* CICD详情 */
		Menu k8order_detail_cicd = new Menu("k8order_detail_cicd", "CICD订单详情", "cicd/order/cicd_catalog_user.html",
				MENU_TYPE_PROJECT_NODE);
		/*Menu k8order_detail_cicd = new Menu("k8order_detail_cicd", "CICD订单详情", "catalog/cicd.html",
				MENU_TYPE_PROJECT_NODE);*/
		
		/* 申请向导 */
		Menu guide = new Menu("guide", "申请容器服务 ", "", MENU_TYPE_APPLY);
		Menu useless = new Menu("useless", "以下为测试功能 ", "", "10");
		/* 统一入口 */
		Menu guide_all = new Menu("guide_all", "申请向导", "guide/guide_all.html", MENU_TYPE_APPLY_WIZARD);

		/* 测试页 */
		Menu test = new Menu("test", "测试功能", "", MENU_TYPE_PROJECT_GROUP);
		Menu guide_mysql = new Menu("guide_mysql", "mysql", "guide/database/mysql.html", "11");
		Menu guide_tomcat = new Menu("guide_tomcat", "tomcat", "guide/middleware/tomcat.html", MENU_TYPE_PROJECT_NODE);
		Menu guide_spark = new Menu("guide_spark", "spark", "guide/bigdata/spark.html", MENU_TYPE_PROJECT_NODE);
		Menu guide_cicd = new Menu("guide_cicd", "cicd", "guide/cicd.html", MENU_TYPE_PROJECT_NODE);

		/*
		 * Menu application_list = new Menu("application_list", "容器应用",
		 * "application_main.html", MENU_TYPE_PROJECT_NODE);
		 * 
		 * Menu public_services_list = new Menu("service_list", "容器服务",
		 * "service_main.html", MENU_TYPE_PROJECT_NODE); Menu job_list = new
		 * Menu("job_list", "批处理", "job_main.html", MENU_TYPE_PROJECT_NODE);
		 */
		Menu framework_service = new Menu("framework_service", "框架服务", "/marathon/frame_list.html",
				MENU_TYPE_HOST_GROUP);

		// Resources
		Menu resources = new Menu("resources", "资源管理 ", "", MENU_TYPE_ZONE);
		Menu resource_part = new Menu("resource_part", "资源分区", "respartion_main.html", MENU_TYPE_CLUSTER_GROUP);
		Menu host_pool = new Menu("host_pool", "主机分区", "host_main.html", MENU_TYPE_HOST_GROUP);
		// Menu cluster_install = new Menu("cluster_install", "集群纳管",
		// "cluster_list.html", MENU_TYPE_CLUSTER_GROUP);
		Menu configMap = new Menu("cluster_install", "应用配置", "configMap_list.html", MENU_TYPE_PROJECT_GROUP);

		Menu framework_resource = new Menu("framework_resource", "框架库", "/universe/dcos_universe.html",
				MENU_TYPE_CLUSTER_GROUP);
		Menu docker_list = new Menu("docker_list", "镜像库", "application_docker.html", MENU_TYPE_PROJECT_NODE);

		Menu monitor = new Menu("resources", "监控管理 ", "", MENU_TYPE_ZONE);

		Menu dcos_nodes = new Menu("dcos_nodes", "节点监控", "dcos_nodes.html", MENU_TYPE_HOST_GROUP);
		Menu dcos_frameworks = new Menu("dcos_frameworks", "框架监控", "dcos_frameworks.html", MENU_TYPE_HOST_GROUP);

		// Menu dashboard_hosts = new Menu("dashboard_host", "k8s节点监控",
		// "host_usage.html", MENU_TYPE_HOST_NODE);
		Menu dashboard_services = new Menu("dashboard_services", "容器监控", "single_service_usage.html",
				MENU_TYPE_HOST_NODE);
		// Framework
		// Menu framework = new Menu("resources", "框架管理 ", "",
		// MENU_TYPE_PROJECT_FRAMEWORK);

		// Security
		Menu security = new Menu("security", "租户管理 ", "", MENU_TYPE_ZONE);
		Menu user_security = new Menu("user_security", "租户管理", "user_security.html", MENU_TYPE_CLUSTER_GROUP);
		Menu group_security = new Menu("group_security", "租户组管理", "group_security.html", MENU_TYPE_HOST_GROUP);

		// order

		Menu order = new Menu("order", "订购管理", "", MENU_TYPE_PROJECT_GROUP);
		Menu ability_instance = new Menu("ability_instance", "租户实例",
				"userAbilityInstance/userAbilityInstance_list.html", MENU_TYPE_PROJECT_NODE);
		Menu order_list = new Menu("order_list", "定制服务订单管理", "order/order_list.html", MENU_TYPE_PROJECT_NODE);
		Menu order_cicd = new Menu("order_cicd", "CICD订单", "cicd/order/cicd_catalog_admin.html", MENU_TYPE_PROJECT_NODE);
		//k8order/admin/cicd_catalog_admin.html
		// 用户订购
		Menu user_order = new Menu("user_order", "用户订购", "", MENU_TYPE_PROJECT_GROUP);
		Menu user_ability_instance = new Menu("user_ability_instance", "已订能力",
				"userAbilityInstance/userAbilityInstance_list_user.html", MENU_TYPE_PROJECT_NODE);
		Menu user_order_list = new Menu("user_order_list", "我的订单", "order/order_list_user.html",
				MENU_TYPE_PROJECT_NODE);
		Menu order_access = new Menu("order_access", "订单入口", "order_access.html", MENU_TYPE_PROJECT_NODE);
		Menu test_page = new Menu("test_page", "测试页", "test_page.html", MENU_TYPE_PROJECT_NODE);
		log.info("User Type==>>" + curUser.getUserType() + "<<==");

		if (curUser.getUserType().equals(Constants.USERTYPE_ADMIN)) {
			log.info("User Type==>>in admin");

			
			// 能力管理
			
			//设备模板
			menus.add(device_manage);
			device_manage.getSubMenus().add(device_manage_list);
			device_manage.getSubMenus().add(device_platform);
			device_manage.getSubMenus().add(device_dashboard);
			//device_manage.getSubMenus().add(application_list_new);
			
			
			//边缘节点
			menus.add(edge_node);
			edge_node.getSubMenus().add(edge_node_monitor);
			/*edge_node.getSubMenus().add(edge_node_manage);
			edge_node.getSubMenus().add(add_application);*/
			
			//edge_node.getSubMenus().add(edge_node_manage);
			//边缘应用
			menus.add(edge_app);
			edge_app.getSubMenus().add(edge_app_monitor);
			edge_app.getSubMenus().add(edge_app_templates);
			//edge_app.getSubMenus().add(edge_app_deploy);
			//边缘市场
			menus.add(edge_market);
			edge_market.getSubMenus().add(edge_market_page);
			//edge_app.getSubMenus().add(edge_app_deploy_diy);
			//edge_app.getSubMenus().add(edge_app_add);
			
			//edge_app.getSubMenus().add(edge_app_template_manage);

			
			//边缘应用模板
			//menus.add(edge_app_template);
			//edge_app_template.getSubMenus().add(edge_app_template_manage);
			//edge_app_template.getSubMenus().add(edge_app_add);
			
			
			
			//边缘镜像库
			menus.add(edge_image);
			edge_image.getSubMenus().add(edge_image_manage);
			//边缘系统管理
			//menus.add(edge_admin);
			//edge_admin.getSubMenus().add(edge_admin_manage);
			
			//数据同步
			menus.add(data_communication);
			data_communication.getSubMenus().add(rabbitmq_web_app);
			
			/*
			 * Menu edge_warning = new Menu("edge_warning", "！！！以下为旧功能！！！", "",
			 * MENU_TYPE_PROJECT_GROUP); menus.add(edge_warning); menus.add(edge_warning);
			 * // 能力管理 menus.add(ability); // application.getSubMenus().add(docker_list); //
			 * ability.getSubMenus().add(application_list);
			 * ability.getSubMenus().add(k8order_helm);
			 * ability.getSubMenus().add(k8order_cicd);
			 * //ability.getSubMenus().add(application_list_new); //
			 * ability.getSubMenus().add(public_services_list); //
			 * ability.getSubMenus().add(job_list);
			 * 
			 * ability.getSubMenus().add(framework_service);
			 * 
			 * // 资源管理 menus.add(resources); resources.getSubMenus().add(host_pool);
			 * resources.getSubMenus().add(resource_part);
			 * resources.getSubMenus().add(docker_list);
			 * resources.getSubMenus().add(configMap); //
			 * resources.getSubMenus().add(dcos_nodes);
			 * resources.getSubMenus().add(framework_resource);
			 * 
			 * // 监控管理 menus.add(monitor); monitor.getSubMenus().add(dcos_nodes);
			 * monitor.getSubMenus().add(dcos_frameworks); //
			 * monitor.getSubMenus().add(dashboard_hosts);
			 * monitor.getSubMenus().add(dashboard_services);
			 * 
			 * // 集群管理 menus.add(dashboard); //
			 * dashboard.getSubMenus().add(dashboard_hosts); //
			 * dashboard.getSubMenus().add(dashboard_services);
			 * dashboard.getSubMenus().add(dashboard_dcos_cluster); //
			 * dashboard.getSubMenus().add(dashboard_dcos_monitor_custom_rule); //
			 * dashboard.getSubMenus().add(dashboard_dcos_cluster_log);
			 * 
			 * // 订单管理 menus.add(order); // 新增order
			 * order.getSubMenus().add(ability_instance);
			 * order.getSubMenus().add(order_list);
			 * order.getSubMenus().add(k8orders_detail_admin);
			 * //order.getSubMenus().add(order_cicd);
			 * 
			 * // 租户管理 menus.add(security); security.getSubMenus().add(user_security);
			 * security.getSubMenus().add(group_security);
			 * 
			 * // 策略管理 menus.add(dashboard_dcos_monitor_custom);
			 * dashboard_dcos_monitor_custom.getSubMenus().add(
			 * dashboard_dcos_monitor_custom_rule);
			 * dashboard_dcos_monitor_custom.getSubMenus().add(select_image);
			 */

		} else if (curUser.getUserType().equals(Constants.USERTYPE_TENANT)) {
			log.info("User Type==>>in TENANT");

			// dashboard
			// menus.add(dashboard_user);
			// dashboard_user.getSubMenus().add(test_page);
			// 订单入口
			//menus.add(guide);
			//guide.getSubMenus().add(guide_all);

			// 容器服务菜单
			menus.add(container_service);
			container_service.getSubMenus().add(container_service_manage);
			container_service.getSubMenus().add(custom_service_manage);

			// Devops服务菜单
			menus.add(devops_service);
			devops_service.getSubMenus().add(cicd_service);

			// 2.0新增功能 订单详情
			menus.add(k8orders);
			k8orders.getSubMenus().add(k8orders_detail);
			//k8orders.getSubMenus().add(cicdOrder_detail);

			// menus.add(order_detail);
			menus.add(docker_image_user);
			docker_image_user.getSubMenus().add(docker_list);

			
			
		/*	menus.add(useless);

			menus.add(user_order);
			user_order.getSubMenus().add(user_order_list);
			user_order.getSubMenus().add(user_ability_instance);
			user_order.getSubMenus().add(order_access);

			menus.add(ability_manage);
			ability_manage.getSubMenus().add(application_list_user);
			ability_manage.getSubMenus().add(application_list_Devops);
			ability_manage.getSubMenus().add(application_list_Devops_test);
			ability_manage.getSubMenus().add(database_user);
			ability_manage.getSubMenus().add(database_user_test);

			// 测试页
			menus.add(test);
			test.getSubMenus().add(guide_mysql);
			test.getSubMenus().add(guide_tomcat);
			test.getSubMenus().add(guide_spark);
			test.getSubMenus().add(guide_cicd);
			test.getSubMenus().add(k8order_detail_bigdata);
			test.getSubMenus().add(k8order_detail_database);
			test.getSubMenus().add(k8order_detail_middleware);
			test.getSubMenus().add(k8order_detail_cicd);*/

			/*
			 * ability.getSubMenus().add(application_list);
			 * ability.getSubMenus().add(job_list);
			 * dashboard.getSubMenus().add(dashboard_services);
			 */

			// menus.add(dashboard);

		}
		System.out.println(menus);
		return menus;

	}

	private List<Ku8ResPartion> getAllResPartions(int clusterId) {
		List<Ku8ResPartion> allPartions = Ku8ResPartionDao.selectAll();
		List<Ku8ResPartion> result = new LinkedList<Ku8ResPartion>();
		for (Ku8ResPartion resPt : allPartions) {
			if (resPt.getClusterId() == clusterId) {
				result.add(resPt);
			}
		}
		return result;
	}

	private Map<Integer, List<Host>> getAllHosts(User curUser) {
		List<Host> allClusters = hostDao.selectAll();
		Map<Integer, List<Host>> result = new HashMap<Integer, List<Host>>();
		for (Host host : allClusters) {
			List<Host> list = result.get(host.getZoneId());
			if (list == null) {
				list = new LinkedList<Host>();
				result.put(host.getZoneId(), list);

			}
			list.add(host);
		}
		return result;
	}

	private List<Ku8Project> getMyProjects(User curUser) {
		return ku8ProjectDao.selectAll();
	}

}
