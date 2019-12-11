$(document).ready(function(){ 
	$('#addOrEdit').on('hide.bs.modal', function () {
		cleanForm();
	});
	$('#jsonMode').on('hide.bs.modal', function () {
		cleanForm();
	});
	/*初始化校验*/
	validateTable();
	/* 初始化datatables */
	initDataTables();
	/* 给数据添加删除，修改操作 */
	initEditCell();
	/* 初始化添加按钮 */
	$("#addBtn").on("click",function(){
		$("#isNew").val("1");
		$("#addOrEdit").modal("show");
	});
	/* 取消拟态框 */
	$("#cancelBtn").on("click",function(){
		validateForm.resetForm();  
		$('#addOrEdit').modal('hide');
	});
	/* 确认拟态框 */
	$("#sureBtn").on("click",function(){
		 if(validateForm.form()){
			 var result = addOrUpdate();
			 //console.log(result);
			 if(result){
				 $('#addOrEdit').modal('hide'); 
			 }
		  }
	});
	/*切换Json视图*/
	$("#switchJsonModeBtn").on("click",function(){
		initJsonMode();
		$('#addOrEdit').modal('hide'); 
		$("#jsonMode").modal('show');
	});
	/*切换编辑视图*/
	$("#switchJsonModeBtn2").on("click",function(){
		getJsonMode();
		$('#addOrEdit').modal('show'); 
		$("#jsonMode").modal('hide');
	});
	/*Json视图的取消和确定*/
	$("#jsonModeCancelBtn").on("click",function(){
		$("#jsonMode").modal('hide');
	});
	$("#jsonModeSureBtn").on("click",function(){
		var result = addOrUpdateForJsonMode();
		 //console.log(result);
		 if(result){
			 $("#jsonMode").modal('hide'); 
		 }
	});
	
});

/* 新增或修改 */
function addOrUpdate(){
	var data = $("#addOrEditForm").formToObj();
	
	//dockerParameter 数组个性化处理
	formatContainerSettingsArray(data);
	//net wok docker Tab 数据个性化处理
	formatNetWorkArray(data);
	//Environment Variables 数组个性化处理
	formatEnvironmentVariablesArray(data);
	//Labels 数组个性化处理
	formatLabelsArray(data);
	//Volumes Tab数组个性化处理
	formatVolumesArray(data);
	//Health Checks Tab
	formatHealthChecksArray(data);
	//console.log(data);
	var result = new Boolean();
	$.ajax({
        url : "marathon/app/save",
        dataType : "json",
        type:"post",
        async: false,
        data :JSON.stringify(data),
        contentType : "application/json; charset=utf-8",
        success : function(res)
        {
        	if(res.status!=200 && res.status!=201){
        		bootbox.alert(res.body);
        		result =  false;
        	}else{
        		refreshDataTables();
        		result = true;
        	}
        	
        }
    });
	return result;
}

function addOrUpdateForJsonMode(){
	var result = new Boolean();
	var tmpJson = editor.get();
	tmpJson.isNew = $("#isNew").val();
	//console.log(tmpJson);
	$.ajax({
        url : "marathon/app/saveByJson",
        dataType : "json",
        type:"post",
        async: false,
        data :JSON.stringify(tmpJson),
        contentType : "application/json; charset=utf-8",
        success : function(res)
        {
        	if(res.status!=200 && res.status!=201){
        		bootbox.alert(res.body);
        		result =  false;
        	}else{
        		refreshDataTables();
        		result = true;
        	}
        	
        }
    });
	return result;
}

/* 刷新datatables */
function refreshDataTables(){
	frameTable.fnDestroy();
	initDataTables();
	initEditCell();
}
/*
 * 清空提交后的表单
 */
function cleanForm(){
	$("#id").val("/");
	$("#cpus").val("1");
	$("#mem").val("128");
	$("#disk").val("128");
	$("#instances").val("0");
	$("#executor").val("");
	$("#fetchUri").val("");
	$("#constraints").val("");
	$("#acceptedResourceRoles").val("");
	$("#user").val("");
	$("#dockerImage").val("");
	$("input[type='checkbox'][name='dockerForcePullImage']").prop("checked", false);
	$("input[type='checkbox'][name='dockerPrivileged']").prop("checked", false);
	$("#dockerNetwork").val("");
	
	$("#addParameterDiv").empty();
	$("#addAnEndpointDiv").empty();
	$("#addEnvironmentVariableDiv").empty();
	$("#addLabelDiv").empty();
	$("#addHealthCheckDiv").empty();
	$("#addLocalVolumeDiv").empty();
	$("#addContainerVolumeDiv").empty();
	$("#addExternalVolumeDiv").empty();
	
	$("#generalTab").click();//关闭默认选择第一个Tab页面
}

var validateForm ;
function validateTable(){
	validateForm = $("#addOrEditForm").validate({
		rules:{
			id:{required:true},
			cpus:{required:true,number:true},
			mem:{required:true,number:true},
			disk:{required:true,number:true},
			//cmd:{required:true},
			instances:{required:true,number:true},
		},
		messages:{
			id:{required:"必须填写"},
			cpus:{required:"必须填写",numer:"填写正确的数字"},
			mem:{required:"必须填写",numer:"填写正确的数字"},
			disk:{required:"必须填写",numer:"填写正确的数字"},
			//cmd:{required:"必须填写"},
			instances:{required:"必须填写",numer:"填写正确的数字"},
		}
	});
}
var frameTable ;
function initDataTables(){
	/**
	 * dom 说明： l - 每页显示行数的控件 f - 检索条件的控件 t - 表格控件 i - 表信息总结的控件 p - 分页控件 r -
	 * 处理中的控件
	 */
	// "dom":'<"top"lTf>t<"bottom"lp><"clear">',
	frameTable = $("#frameTable").dataTable({
		//"dom": 'lTfitp',
        "tableTools": {
            "sSwfPath": "static/js/plugins/dataTables/swf/copy_csv_xls_pdf.swf"
        },
        "bPaginate" : true, // 是否显示（应用）分页器
        //"serverSide": true,//打开后台分页  
        //"bFilter" : true,
        "bInfo" : true, // 是否显示页脚信息，DataTables插件左下角显示记录数
        "aLengthMenu": [10] , // 每页显示数量
        "bSort" : false,// 排序
        "oLanguage": { // 国际化配置
            "sProcessing" : "正在获取数据，请稍后...",
            "sLengthMenu" : "显示 _MENU_ 条",    
            "sZeroRecords" : "没有您要搜索的内容",    
            "sInfo" : "从 _START_ 到  _END_ 条记录 总记录数为 _TOTAL_ 条",    
            "sInfoEmpty" : "记录数为0",    
            "sInfoFiltered" : "(全部记录数 _MAX_ 条)",    
            "sInfoPostFix" : "",    
            "sSearch" : "搜索",    
            "sUrl" : "",    
            "oPaginate": {    
                "sFirst" : "第一页",    
                "sPrevious" : "上一页",    
                "sNext" : "下一页",    
                "sLast" : "最后一页"    
            }  
        },
        "sAjaxSource": "/marathon/app/datatables",
        "fnServerData" : function(sSource, aDataSet, fnCallback, oSettings) {
        	aDataSet = {
            	//urlSuffix:"/v2/apps"
            };
        	aDataSet = getReqMap(aDataSet);
        	//console.log(aDataSet);
        	oSettings.jqXHR = $.ajax({
    	        "dataType": 'json',
    	        "type": "POST",
    	        "url": sSource,
    	        "contentType" : "application/json; charset=utf-8",
    	        //"data": JSON.stringify(aDataSet),
    	        "success" : function(res)
				{
					successCallback(res, aDataSet, fnCallback);
				}
        	 } );
        },
        "aoColumns" : [{
               "mData" : "id",
               "bVisible" : true,
               "sTitle" : "名称"
           },{
               "mData" : "cpus",
               "bVisible" : true,
               "sTitle" : "Cpu数量(个)",
           },{
               "mData" : "mem",
               "bVisible" : true,
               "sTitle" : "内存大小(MiB)",
           },{
        	   "mData" : "disk",
        	   "bVisible" : true,
               "sTitle" : "磁盘(MiB)",
           },{
        	   "mData" : "tasksStaged",
        	   "bVisible" : true,
               "sTitle" : "已结束任务数",
           },{
        	   "mData" : "tasksRunning",
        	   "bVisible" : true,
               "sTitle" : "运行中任务数",
           },{
        	   "mData" : "tasksHealthy",
        	   "bVisible" : true,
               "sTitle" : "健康任务数",
           },{
        	   "mData" : "tasksUnhealthy",
        	   "bVisible" : true,
               "sTitle" : "不健康任务数",
           },{
            "mData" : "id",
            "sTitle" : "操作",
            "mRender": function (data,type,full) {
            	return '<a href="javascript:void(0);" class="icon-edit" title="修改"><i class="fa fa-pencil-square-o"></i></a>&nbsp;|&nbsp;'+
            	'<a href="javascript:void(0);" class="icon-del" title="删除"><i class="fa fa-trash-o"></i></a>&nbsp;|&nbsp;'+
            	'<a id="'+data+'" href="javascript:void(0);" class="icon-tasks" title="查看运行中任务"><i class="fa fa-tasks"></i></a>&nbsp;';
            }
        }],
        "fnDrawCallback": function( oSettings ) { 
        	
        }
	}); 
}
/* 添加修改删除操作 */
function initEditCell(){
	// 修改操作
	$("#frameTable tbody").on("click","tr>td>a.icon-edit",function(){
		$("#addParameterDiv").empty();
		$("#addAnEndpointDiv").empty();
		$("#addEnvironmentVariableDiv").empty();
		$("#addLabelDiv").empty();
		$("#addHealthCheckDiv").empty();
		$("#addLocalVolumeDiv").empty();
		$("#addContainerVolumeDiv").empty();
		$("#addExternalVolumeDiv").empty();
		
		var rowNode = this.parentNode.parentNode ;
		var rowData = frameTable.fnGetData(rowNode);
		//console.log(rowData);
		$("#isNew").val("0");
		//General Tab
		$("#id").val(rowData.id);
		$("#cpus").val(rowData.cpus);
		$("#mem").val(rowData.mem);
		$("#disk").val(rowData.disk);
		$("#instances").val(rowData.instances);
		$("#cmd").val(rowData.cmd);
		//Container Settings Tab  Volumes Tab  NetWork Tab
		var dockerNetWorks;
		if(rowData.container!==undefined && rowData.container!=null){
			if(rowData.container.docker!==undefined && rowData.container.docker!=null){
				$("#dockerImage").val(rowData.container.docker.image);
				$("#dockerNetwork").val(rowData.container.docker.network);//设置NetWork Tab
				$("input[type='checkbox'][name='dockerForcePullImage']").prop("checked",rowData.container.docker.forcePullImage);
				$("input[type='checkbox'][name='dockerPrivileged']").prop("checked", rowData.container.docker.privileged);
				if(rowData.container.docker.parameters!==undefined && rowData.container.docker.parameters!=null){
					editDockerParameter(rowData.container.docker.parameters);
				}
				if(rowData.container.docker.portMappings!==undefined && rowData.container.docker.portMappings!=null){
					dockerNetWorks = rowData.container.docker.portMappings;
					editDockerNetWork(rowData.container.docker.portMappings);
				}
			}
			if(rowData.container.volumes){
				editVolumes(rowData.container.volumes);
			}
		}
		//Net Work Tab
		//初始化Network Type
		initNetworkType();
		//初始化netWork 页面
		if(dockerNetWorks==undefined){
			if(rowData.portDefinitions!==undefined && rowData.portDefinitions!=null && rowData.portDefinitions.length>0){
				for(var i=0;i<rowData.portDefinitions.length;i++){
					rowData.portDefinitions[i].containerPort = rowData.portDefinitions[i].port;
				}		
				editDockerNetWork(rowData.portDefinitions);
			}
		}
		//Environment Variables Tab
		if(rowData.env!==undefined){
			editEnvironmentVariable(rowData.env);
		}
		//HealthCheck Tab
		if(rowData.healthChecks!==undefined && rowData.healthChecks!=null && rowData.healthChecks.length>0){
			editHealthChecks(rowData.healthChecks);
		}
		//Labels Tab
		if(rowData.labels!==undefined && rowData.labels!=null && rowData.labels.length>0){
			editLables(rowData.labels);
		}
		//Optional Tab
		$("#executor").val(rowData.executor);
		if(rowData.fetch!==undefined && rowData.fetch!=null && rowData.fetch.length>0){
			var tmpFetch = '';
			for(var i=0;i<rowData.fetch.length;i++){
				tmpFetch += ","+rowData.fetch[i].uri;
			}
			tmpFetch = tmpFetch.substring(1);
			$("#fetchUri").val(tmpFetch);
		}
		if(rowData.constraints!==undefined && rowData.constraints!=null && rowData.constraints.length>0){
			var constraint = rowData.constraints[0];
			var tmpConstraint = '';
			for(var i=0;i<constraint.length;i++){
				tmpConstraint+=":"+constraint[i];
			}
			tmpConstraint = tmpConstraint.substring(1);
			$("#constraints").val(tmpConstraint);
		}
		if(rowData.acceptedResourceRoles!==undefined && rowData.acceptedResourceRoles!=null && rowData.acceptedResourceRoles.length>0){
			var tmpAcceptedResourceRoles = '';
			for(var i=0;i<rowData.acceptedResourceRoles.length;i++){
				tmpAcceptedResourceRoles+=","+rowData.acceptedResourceRoles[i];
			}	
			$("#acceptedResourceRoles").val(tmpAcceptedResourceRoles);
		}
		$("#user").val(rowData.user);
		$("#addOrEdit").modal("show");
	});
	// 删除操作
	$("#frameTable tbody").on("click", "tr>td>a.icon-del", function()
	{
		var rowNode = this.parentNode.parentNode;
		var rowData = frameTable.fnGetData(rowNode);
		var tmpId = (rowData.id).replace("/","");
		bootbox.setLocale("zh_CN");  
		bootbox.confirm("确认删除吗？",function(YesOrNo){
			if (YesOrNo){
				// 确定后逻辑
				$.ajax(
				{
					url : "marathon/app/deleteById/"+tmpId,
					dataType : "json",
					type : "delete",
					success : function(result)
					{
						//console.log(result);
						refreshDataTables();
					},
					error : function()
					{
						bootbox.alert("删除操作失败，请稍后再试！");
					}
				});
			}
		});
	});
	//查看运行中的任务
	$("#frameTable tbody").on("click", "tr>td>a.icon-tasks", function()
	{
		var rowNode = this.parentNode.parentNode;
		var rowData = frameTable.fnGetData(rowNode);
		store.remove("marathonAppId");
		store.remove("marathonAppInstances");
		store.set("marathonAppId",(rowData.id).replace("/",""));
		store.set("marathonAppInstances",rowData.instances);
		$(".content").load("./marathon/frame_task_list.html");
	});
}

var editor;
function initJsonMode(){
	// create the editor
	  $("#jsonModeDiv").empty();
	  var container = document.getElementById('jsonModeDiv');
	  var options = {
			    mode: 'code',
			    modes: ['code', 'form', 'text', 'tree', 'view'], // allowed modes
			    onError: function (err) {
			      alert(err.toString());
			    },
			    onModeChange: function (newMode, oldMode) {
			      console.log('Mode switched from', oldMode, 'to', newMode);
			    }
	  };
	  editor = new JSONEditor(container,options);
	  var data = $("#addOrEditForm").formToObj();
	  //console.log(data);
	  	//dockerParameter 数组个性化处理
		formatContainerSettingsArray(data);
		//net wok docker Tab 数据个性化处理
		formatNetWorkArray(data);
		//Environment Variables 数组个性化处理
		formatEnvironmentVariablesArray(data);
		//Labels 数组个性化处理
		formatLabelsArray(data);
		//Volumes Tab数组个性化处理
		formatVolumesArray(data);
		//Health Checks Tab
		formatHealthChecksArray(data);
	  $.ajax({
	        url : "marathon/app/viewConversionObject",
	        dataType : "json",
	        type:"post",
	        async: false,
	        data :JSON.stringify(data),
	        contentType : "application/json; charset=utf-8",
	        success : function(res)
	        {
	        	console.log(res);
	        	// set json
	      	  //json中删除isNew隐藏字段
	    	  	delete res.isNew;
	    	    editor.set(res);
	        }
	  });
}

// get json
function getJsonMode(){
	$("#addParameterDiv").empty();
	$("#addAnEndpointDiv").empty();
	$("#addEnvironmentVariableDiv").empty();
	$("#addLabelDiv").empty();
	$("#addHealthCheckDiv").empty();
	$("#addLocalVolumeDiv").empty();
	$("#addContainerVolumeDiv").empty();
	$("#addExternalVolumeDiv").empty();
	
	var rowData = editor.get();
	//console.log(rowData);
	//General Tab
	$("#id").val(rowData.id);
	$("#cpus").val(rowData.cpus);
	$("#mem").val(rowData.mem);
	$("#disk").val(rowData.disk);
	$("#instances").val(rowData.instances);
	$("#cmd").val(rowData.cmd);
	//Container Settings Tab  Volumes Tab  NetWork Tab
	var dockerNetWorks;
	if(rowData.container!==undefined && rowData.container!=null){
		if(rowData.container.docker!==undefined && rowData.container.docker!=null){
			$("#dockerImage").val(rowData.container.docker.image);
			$("#dockerNetwork").val(rowData.container.docker.network);//设置NetWork Tab
			$("input[type='checkbox'][name='dockerForcePullImage']").prop("checked",rowData.container.docker.forcePullImage);
			$("input[type='checkbox'][name='dockerPrivileged']").prop("checked", rowData.container.docker.privileged);
			if(rowData.container.docker.parameters!==undefined && rowData.container.docker.parameters!=null){
				editDockerParameter(rowData.container.docker.parameters);
			}
			if(rowData.container.docker.portMappings!==undefined && rowData.container.docker.portMappings!=null){
				dockerNetWorks = rowData.container.docker.portMappings;
				editDockerNetWork(rowData.container.docker.portMappings);
			}
		}
		if(rowData.container.volumes){
			editVolumes(rowData.container.volumes);
		}
	}
	//Net Work Tab
	//初始化Network Type
	initNetworkType();
	//初始化netWork 页面
	if(dockerNetWorks==undefined){
		if(rowData.portDefinitions!==undefined && rowData.portDefinitions!=null && rowData.portDefinitions.length>0){
			for(var i=0;i<rowData.portDefinitions.length;i++){
				rowData.portDefinitions[i].containerPort = rowData.portDefinitions[i].port;
			}		
			editDockerNetWork(rowData.portDefinitions);
		}
	}
	//Environment Variables Tab
	if(rowData.env!==undefined){
		editEnvironmentVariable(rowData.env);
	}
	//HealthCheck Tab
	if(rowData.healthChecks!==undefined && rowData.healthChecks!=null && rowData.healthChecks.length>0){
		editHealthChecks(rowData.healthChecks);
	}
	//Labels Tab
	if(rowData.labels!==undefined && rowData.labels!=null && rowData.labels.length>0){
		editLables(rowData.labels);
	}
	//Optional Tab
	$("#executor").val(rowData.executor);
	if(rowData.fetch!==undefined && rowData.fetch!=null){
		if(rowData.fetch.length>0){
			var tmpFetch = '';
			for(var i=0;i<rowData.fetch.length;i++){
				tmpFetch += ","+rowData.fetch[i].uri;
			}
			tmpFetch = tmpFetch.substring(1);
			$("#fetchUri").val(tmpFetch);
		}
	}
	if(rowData.constraints!==undefined && rowData.constraints!=null && rowData.constraints.length>0){
		var constraint = rowData.constraints[0];
		var tmpConstraint = '';
		for(var i=0;i<constraint.length;i++){
			tmpConstraint+=":"+constraint[i];
		}
		tmpConstraint = tmpConstraint.substring(1);
		$("#constraints").val(tmpConstraint);
	}
	if(rowData.acceptedResourceRoles!==undefined && rowData.acceptedResourceRoles!=null && rowData.acceptedResourceRoles.length>0){
		var tmpAcceptedResourceRoles = '';
		for(var i=0;i<rowData.acceptedResourceRoles.length;i++){
			tmpAcceptedResourceRoles+=","+rowData.acceptedResourceRoles[i];
		}	
		$("#acceptedResourceRoles").val(tmpAcceptedResourceRoles);
	}
	$("#user").val(rowData.user);
}

