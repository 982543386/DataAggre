var dcos = {
	
	installCluster : function() {
		var name = $("#name").val();
		var network = $("#network").val();
		var sshUserName = $("#sshUserName").val();
		var sshUserPassword = $("#sshUserPassword").val();
		
		var bootNode = "";
		var mNodes = new Array();
		var sNodes = new Array();
		var dnsArr = new Array();
		
		$("#bootstrapNode input").each(function(i,item){
			bootNode = $(item).val();
		});
		
		$("#masterNodes input").each(function(i,item){
			mNodes.push($(item).val());
		});
		
		$("#slaveNodes input").each(function(i,item){
			sNodes.push($(item).val());
		});
		
		$("#dnsInfo input").each(function(i,item){
			dnsArr.push($(item).val());
		});
		
		var clusterMeta = {};
		clusterMeta.cluster_name = name;
		clusterMeta.bootstrap_ip = bootNode;
		clusterMeta.master_ip_list = mNodes;
		clusterMeta.private_agent_ip_list = sNodes;
		clusterMeta.dns_ip_list = dnsArr;
		clusterMeta.network = network;
		clusterMeta.sshUserName = sshUserName;
		clusterMeta.sshUserPassword = sshUserPassword;
		
		$.ajax({
			url: "/dcos/installCluster",
			type: "POST",
			dataType:"json",
			contentType: "application/json; charset=utf-8",  
			data: JSON.stringify(clusterMeta),
			success: function(response){
				
			}
		});
		$('.content').load('dcos_cluster.html');
		alertSuccess('安装中', '安装需要几分钟的时间，请耐心等待！！！');
	},
	
	deleteCluster : function(clusterId) {
		
	},
	
	deleteNode : function(ip,username,password) {
		
	},
	
	addNode : function() {
		var cluster = JSON.parse(sessionStorage.getItem("dcos_cluster"));
		var nodeArr = new Array();
		$("#masterNodes div[name='label']").each(function(i,item){
			var nodeIp = $(this).find("input[name='nodeIp']").val();
			var sshUserName = $(this).find("input[name='sshUserName']").val();
			var sshUserPassword = $(this).find("input[name='sshUserPassword']").val();
			if (nodeIp.trim() != '' && sshUserName != '' && sshUserPassword != '') {
				var node = {};
				node.nodeIp = nodeIp;
				node.sshUserName = sshUserName;
				node.sshUserPassword = sshUserPassword;
				node.nodeType = "master";
				nodeArr.push(node);
			}
			
		});
		
		$("#slaveNodes div[name='label']").each(function(i,item){
			
			var nodeIp = $(this).find("input[name='nodeIp']").val();
			var sshUserName = $(this).find("input[name='sshUserName']").val();
			var sshUserPassword = $(this).find("input[name='sshUserPassword']").val();
			if (nodeIp.trim() != '' && sshUserName != '' && sshUserPassword != '') {
				var node = {};
				node.nodeIp = nodeIp;
				node.sshUserName = sshUserName;
				node.sshUserPassword = sshUserPassword;
				node.nodeType = "slave";
				nodeArr.push(node);
			}
		});
		
		$.ajax({
			url: "/dcos/"+cluster.id+"/addNode",
			type: "PUT",
			dataType:"json",
			contentType: "application/json; charset=utf-8",  
			data: JSON.stringify(nodeArr),
			success: function(response){
				console.log(response);
			}
		});
		
		alertSuccess('添加中', '操作需要几分钟的时间，请耐心等待！！！');
	},
	
	addMonitorRule : function() {
		var ruleId = $("#ruleId").val();
		var ruleName = $("#ruleName").val();
		var clusterId = $("#clusterId").val();
		var appId = $("#appId").val();
		var counterName = $("#counterName").val();
		var conditionType = $("#conditionType").val();
		var conditionValue = $("#conditionValue").val();
		var span = $("#span").val();
		var ruleType = $("#ruleType").val();
		
		var url = "/monitorRule/add";
		var rule = {};
		if (ruleId) {
			rule.id = ruleId;
			url = "/monitorRule/update"
		}
		rule.ruleName = ruleName;
		rule.clusterId = clusterId;
		rule.appId = appId;
		rule.counterName = counterName;
		rule.conditionType = conditionType;
		rule.conditionValue = conditionValue;
		rule.span = span;
		rule.ruleType = ruleType;
		
		$.ajax({
			url: url,
			type: "POST",
			dataType:"json",
			contentType: "application/json; charset=utf-8",  
			data: JSON.stringify(rule),
			success: function(response){
				$('.content').load('dcos_monitor_custom_rule.html');
				alertSuccess('修改成功', '');
			}
		});
	}
};