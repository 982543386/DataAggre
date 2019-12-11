$(document).ready(function(){ 
	$('#scaleModel').on('hide.bs.modal', function () {
		cleanForm();
	});
	/* 初始化datatables */
	initDataTables();
	/* 给数据添加删除操作 */
	initEditCell();
	/* 初始化按钮 */
	$("#scaleBtn").on("click",function(){
		$("#scaleNum").val(store.get("marathonAppInstances"));
		$("#scaleModel").modal("show");
	});
	$("#scaleCancelBtn").on("click",function(){
		$("#scaleModel").modal("hide");
	});
	$("#scaleSureBtn").on("click",function(){
		bootbox.confirm("scale？",function(YesOrNo){
			if (YesOrNo){
				// 确定后逻辑
				$.ajax(
				{
					url : "marathon/app/scaleById/"+store.get("marathonAppId")+"/"+$("#scaleNum").val(),
					dataType : "json",
					type : "post",
					success : function(res)
					{
						if(res.status!=200 && res.status!=201){
			        		bootbox.alert(res.body);
			        	}else{
			        		store.set("marathonAppInstances",$("#scaleNum").val());
			        		refreshDataTables();
			        	}
					},
					error : function()
					{
						bootbox.alert("调整任务实例操作失败，请稍后再试！");
					}
				});
			}
		});
		$("#scaleModel").modal("hide");
	});
	
	
	$("#restartBtn").on("click",function(){
		bootbox.confirm("restart？",function(YesOrNo){
			if (YesOrNo){
				// 确定后逻辑
				$.ajax(
				{
					url : "marathon/app/restartById/"+store.get("marathonAppId"),
					dataType : "json",
					type : "post",
					success : function(result)
					{
						//refreshDataTables();
					},
					error : function()
					{
						bootbox.alert("重启操作失败，请稍后再试！");
					}
				});
			}
		});
	});
	
	
	$("#suspendBtn").on("click",function(){
		bootbox.confirm("Are you sure you want to suspend "+store.get("marathonAppId")+" by scaling to 0 instances?",function(YesOrNo){
			if (YesOrNo){
				// 确定后逻辑
				$.ajax(
				{
					url : "marathon/app/scaleById/"+store.get("marathonAppId")+"/0",
					dataType : "json",
					type : "post",
					success : function(res)
					{
						if(res.status!=200 && res.status!=201){
			        		bootbox.alert(res.body);
			        	}else{
			        		store.set("marathonAppInstances",0);
			        		refreshDataTables();
			        	}
					},
					error : function()
					{
						bootbox.alert("调整任务实例操作失败，请稍后再试！");
					}
				});
			}
		});
	});
	
	$("#destoryBtn").on("click",function(){
		bootbox.confirm("destory？",function(YesOrNo){
			if (YesOrNo){
				// 确定后逻辑
				$.ajax(
				{
					url : "marathon/app/deleteById/"+store.get("marathonAppId"),
					dataType : "json",
					type : "delete",
					success : function(result)
					{
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
	
});

function cleanForm(){
	//$("#scaleNum").val(0);
}


/* 刷新datatables */
function refreshDataTables(){
	frameTaskTable.fnDestroy();
	initDataTables();
	initEditCell();
}

var frameTaskTable ;
function initDataTables(){
	/**
	 * dom 说明： l - 每页显示行数的控件 f - 检索条件的控件 t - 表格控件 i - 表信息总结的控件 p - 分页控件 r -
	 * 处理中的控件
	 */
	// "dom":'<"top"lTf>t<"bottom"lp><"clear">',
	frameTaskTable = $("#frameTaskTable").dataTable({
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
        "sAjaxSource": "marathon/task/datatables",
        "fnServerData" : function(sSource, aDataSet, fnCallback, oSettings) {
        	aDataSet = {
            	appId:store.get("marathonAppId")
            };
        	aDataSet = getReqMap(aDataSet);
        	//console.log(aDataSet);
        	oSettings.jqXHR = $.ajax({
    	        "dataType": 'json',
    	        "type": "POST",
    	        "url": sSource,
    	        "contentType" : "application/json; charset=utf-8",
    	        "data": JSON.stringify(aDataSet),
    	        "success" : function(res)
				{
					successCallback(res, aDataSet, fnCallback);
				}
        	 } );
        },
        "aoColumns" : [{
               "mData" : "id",
               "bVisible" : true,
               "sTitle" : "任务ID"
           },{
               "mData" : "appId",
               "bVisible" : true,
               "sTitle" : "任务名称",
               "mRender": function (data,type,full) {
            	   return data.replace("/","");
               }
           },{
               "mData" : "host",
               "bVisible" : true,
               "sTitle" : "主机",
           },{
        	   "mData" : "state",
        	   "bVisible" : true,
               "sTitle" : "状态",
           },{
        	   "mData" : "startedAt",
        	   "bVisible" : true,
               "sTitle" : "启动时间",
               "mRender":function (data,type,full){
            	   var tmp = new Date(data).Format("yyyy-MM-dd hh:mm:ss");
            	   return tmp;
               }
           },{
        	   "mData" : "stagedAt",
        	   "bVisible" : true,
               "sTitle" : "上次启动时间",
               "mRender":function (data,type,full){
            	   var tmp = new Date(data).Format("yyyy-MM-dd hh:mm:ss");
            	   return tmp;
               }
           },{
        	   "mData" : "version",
        	   "bVisible" : true,
               "sTitle" : "版本",
               "mRender":function (data,type,full){
            	   var tmp = new Date(data).Format("yyyy-MM-dd hh:mm:ss");
            	   return tmp;
               }
           },{
            "mData" : "id",
            "sTitle" : "操作",
            "mRender": function (data,type,full) {
            	return '<a href="javascript:void(0);" class="icon-del" title="删除"><i class="fa fa-trash-o"></i></a>&nbsp;|&nbsp;'+
            	'<a href="javascript:void(0);" class="icon-tasks" title="操作示例"><i class="fa fa-tasks"></i></a>';
            }
        }],
        "fnDrawCallback": function( oSettings ) { 
        	
        }
	}); 
}
/* 添加删除操作 */
function initEditCell(){

	// 删除操作
	$("#frameTaskTable tbody").on("click", "tr>td>a.icon-del", function()
	{
		var rowNode = this.parentNode.parentNode;
		var rowData = frameTaskTable.fnGetData(rowNode);
		var tmpAppId = (rowData.appId).replace("/","");
		bootbox.setLocale("zh_CN");  
		bootbox.confirm("确认删除吗？",function(YesOrNo){
			if (YesOrNo){
				// 确定后逻辑
				$.ajax(
				{
					url : "marathon/task/deleteById/"+rowData.id+"/"+tmpAppId,
					dataType : "json",
					type : "delete",
					success : function(result)
					{
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
	
	// 操作示例
	$("#frameTaskTable tbody").on("click", "tr>td>a.icon-tasks", function()
	{
		var rowNode = this.parentNode.parentNode;
		var rowData = frameTaskTable.fnGetData(rowNode);
		window.open('http://'+rowData.host+':8192','newwindow','width='+(window.screen.availWidth-10)+',height='+(window.screen.availHeight-30)+ 
				',top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no') ; 
	
	});
}

