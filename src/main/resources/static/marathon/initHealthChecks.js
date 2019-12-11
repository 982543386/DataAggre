$(document).ready(function(){ 
	
	/*Health Checks addHealthCheck*/
	$("#addHealthCheck").on("click",function(){
		var reduceHealthCheckDivNum = $(".reduceHealthCheckDiv").length;
		var tmpDiv = "";
		tmpDiv+='<div class="reduceHealthCheckDiv" id="reduceHealthCheckDiv'+(reduceHealthCheckDivNum+1)+'" >';
		tmpDiv+='	<div class="col-sm-10">';
		tmpDiv+='		<div class="reduceHealthCheckDivHead"><h4>Health Check #'+(reduceHealthCheckDivNum+1)+'</h4></div>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-2">';
		tmpDiv+='		<div onclick="reduceHealthCheck(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 14px 12px;"></span>';
		tmpDiv+='		</div>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-3">';
		tmpDiv+='		<label class="control-label">Protocol</label>';
		tmpDiv+='		<select name="healthChecksProtocol" class="form-control changeProtocol" onchange="initHealthCheckProtocol(this)" >';
		tmpDiv+='			<option value="HTTP">HTTP</option>';
		tmpDiv+='			<option value="COMMAND">COMMAND</option>';
		tmpDiv+='			<option value="TCP">TCP</option>';
		tmpDiv+='		</select>';
		tmpDiv+='	</div>';
		tmpDiv+='   <div class="initHealthCheckProtocol">';
		tmpDiv+='   </div>';
		tmpDiv+='</div>';
		$("#addHealthCheckDiv").append(tmpDiv);
		//自动触发一下onchange事件
		$("#reduceHealthCheckDiv"+(reduceHealthCheckDivNum+1)).find(".changeProtocol").change();
	});
	//$("#addHealthCheck").click();
});

/*Health Checks reduceHealthCheck*/
function reduceHealthCheck(obj){
	$(obj).closest(".reduceHealthCheckDiv").remove();
	//修改reduceHealthCheckDiv编码
	var i=1;
	$(".reduceHealthCheckDiv").each(function(){
		$(this).attr("id","reduceHealthCheckDiv"+i);
		$(this).find(".reduceHealthCheckDivHead").html("<h4>Health Check #"+i+"</h4>");
		i = i+1;
	});
}

/*Health Checks 根据协议获取健康度的对象*/
function initHealthCheckProtocol(obj){
	var healthCheckProtocol = $(obj).val();
	var tmpDiv = "";
	if(healthCheckProtocol=='HTTP'){
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	<div style="display:none">';
		tmpDiv+='		<input type="text" name="healthChecksCommand" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='		<label class="control-label">Path</label>';
		tmpDiv+='		<input type="text" name="healthChecksPath" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='		<label class="control-label">Grace Period&nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-question-sign" aria-hidden="true" title="Grace period in seconds"></span></label>';
		tmpDiv+='		<input type="number" name="healthChecksGracePeriodSeconds" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='		<label class="control-label">Interval&nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-question-sign" aria-hidden="true" title="Interval in seconds"></span></label>';
		tmpDiv+='		<input type="number" name="healthChecksIntervalSeconds" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='		<label class="control-label">Timeout&nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-question-sign" aria-hidden="true" title="Timeout in seconds"></span></label>';
		tmpDiv+='		<input type="number" name="healthChecksTimeoutSeconds" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='		<label class="control-label">Maximum Consecutive Failures</label>';
		tmpDiv+='		<input type="number" name="healthChecksMaxConsecutiveFailures" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='   <div class="initHealthCheckPort">';
		
		tmpDiv+='   </div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-3">';
		tmpDiv+='		<label class="control-label">Port Type</label>';
		tmpDiv+='		<select name="healthChecksPortType" class="form-control changePortType" onchange="initHealthCheckPort(this)">';
		tmpDiv+='			<option value="portIndex">Port Index</option>';
		tmpDiv+='			<option value="portNumber">Port Number</option>';
		tmpDiv+='		</select>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='		<input type="checkbox" name="healthChecksIgnoreHttp1xx" value="true">Ignore HTTP Status Codes 100 to 199';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
	}else if(healthCheckProtocol=='COMMAND'){
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	<div style="display:none">';
		tmpDiv+='		<input type="text" name="healthChecksPath" class="form-control" value="" />';
		tmpDiv+='		<input type="checkbox" name="healthChecksIgnoreHttp1xx" value="true"/>Ignore HTTP Status Codes 100 to 199';
		tmpDiv+='	</div>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='		<label class="control-label">COMMAND</label>';
		tmpDiv+='		<input type="text" name="healthChecksCommand" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='		<label class="control-label">Grace Period&nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-question-sign" aria-hidden="true" title="Grace period in seconds"></span></label>';
		tmpDiv+='		<input type="number" name="healthChecksGracePeriodSeconds" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='		<label class="control-label">Interval&nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-question-sign" aria-hidden="true" title="Interval in seconds"></span></label>';
		tmpDiv+='		<input type="number" name="healthChecksIntervalSeconds" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='		<label class="control-label">Timeout&nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-question-sign" aria-hidden="true" title="Timeout in seconds"></span></label>';
		tmpDiv+='		<input type="number" name="healthChecksTimeoutSeconds" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='		<label class="control-label">Maximum Consecutive Failures</label>';
		tmpDiv+='		<input type="number" name="healthChecksMaxConsecutiveFailures" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='   <div class="initHealthCheckPort">';
		
		tmpDiv+='   </div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-3">';
		tmpDiv+='		<label class="control-label">Port Type</label>';
		tmpDiv+='		<select name="healthChecksPortType" class="form-control changePortType" onchange="initHealthCheckPort(this)">';
		tmpDiv+='			<option value="portIndex">Port Index</option>';
		tmpDiv+='			<option value="portNumber">Port Number</option>';
		tmpDiv+='		</select>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
	}else{
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	<div style="display:none">';
		tmpDiv+='		<input type="text" name="healthChecksPath" class="form-control" value="" />';
		tmpDiv+='		<input type="text" name="healthChecksCommand" class="form-control" value="" />';
		tmpDiv+='		<input type="checkbox" name="healthChecksIgnoreHttp1xx" value="true"/>Ignore HTTP Status Codes 100 to 199';
		tmpDiv+='	</div>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='		<label class="control-label">Grace Period&nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-question-sign" aria-hidden="true" title="Grace period in seconds"></span></label>';
		tmpDiv+='		<input type="number" name="healthChecksGracePeriodSeconds" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='		<label class="control-label">Interval&nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-question-sign" aria-hidden="true" title="Interval in seconds"></span></label>';
		tmpDiv+='		<input type="number" name="healthChecksIntervalSeconds" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='		<label class="control-label">Timeout&nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-question-sign" aria-hidden="true" title="Timeout in seconds"></span></label>';
		tmpDiv+='		<input type="number" name="healthChecksTimeoutSeconds" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='		<label class="control-label">Maximum Consecutive Failures</label>';
		tmpDiv+='		<input type="number" name="healthChecksMaxConsecutiveFailures" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='   <div class="initHealthCheckPort">';
		
		tmpDiv+='   </div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-3">';
		tmpDiv+='		<label class="control-label">Port Type</label>';
		tmpDiv+='		<select name="healthChecksPortType" class="form-control changePortType" onchange="initHealthCheckPort(this)">';
		tmpDiv+='			<option value="portIndex">Port Index</option>';
		tmpDiv+='			<option value="portNumber">Port Number</option>';
		tmpDiv+='		</select>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
	}
	$(obj).closest(".reduceHealthCheckDiv").find(".initHealthCheckProtocol").html(tmpDiv);
	//自动触发一下onchange事件
	$(obj).closest(".reduceHealthCheckDiv").find(".changePortType").change();
}

function initHealthCheckPort(obj){
	var initHealthCheckPort = $(obj).val();
	var tmpDiv = "";
	if(initHealthCheckPort=='portIndex'){
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='		<label class="control-label">Port Index</label>';
		tmpDiv+='		<input type="number" name="healthChecksPortIndex" class="form-control" value="" />';
		tmpDiv+='	<div style="display:none">';
		tmpDiv+='		<input type="number" name="healthChecksPort" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	</div>';
	}else{
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='		<label class="control-label">Port Number</label>';
		tmpDiv+='		<input type="number" name="healthChecksPort" class="form-control" value="" />';
		tmpDiv+='	<div style="display:none">';
		tmpDiv+='		<input type="number" name="healthChecksPortIndex" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	</div>';
		
	}
	$(obj).closest(".reduceHealthCheckDiv").find(".initHealthCheckPort").html(tmpDiv);
}

function editHealthChecks(healthChecksArray){
	for(var i=0;i<healthChecksArray.length;i++){
		var tmpDiv = "";
		tmpDiv+='<div class="reduceHealthCheckDiv" id="reduceHealthCheckDiv'+(i+1)+'" >';
		tmpDiv+='	<div class="col-sm-10">';
		tmpDiv+='		<div class="reduceHealthCheckDivHead"><h4>Health Check #'+(i+1)+'</h4></div>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-2">';
		tmpDiv+='		<div onclick="reduceHealthCheck(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 14px 12px;"></span>';
		tmpDiv+='		</div>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-3">';
		tmpDiv+='		<label class="control-label">Protocol</label>';
		tmpDiv+='		<select name="healthChecksProtocol" class="form-control changeProtocol" onchange="initHealthCheckProtocol(this)" >';
		if(healthChecksArray[i].protocol=='HTTP'){
			tmpDiv+='			<option value="HTTP" selected="selected">HTTP</option>';
			tmpDiv+='			<option value="COMMAND">COMMAND</option>';
			tmpDiv+='			<option value="TCP">TCP</option>';
		}else if(healthChecksArray[i].protocol=='COMMAND'){
			tmpDiv+='			<option value="HTTP">HTTP</option>';
			tmpDiv+='			<option value="COMMAND" selected="selected">COMMAND</option>';
			tmpDiv+='			<option value="TCP">TCP</option>';
		}else{
			tmpDiv+='			<option value="HTTP">HTTP</option>';
			tmpDiv+='			<option value="COMMAND">COMMAND</option>';
			tmpDiv+='			<option value="TCP" selected="selected">TCP</option>';
		}
		tmpDiv+='		</select>';
		tmpDiv+='	</div>';
		tmpDiv+='   <div class="initHealthCheckProtocol">';
		if(healthChecksArray[i].protocol=='HTTP'){
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	<div style="display:none">';
			tmpDiv+='		<input type="text" name="healthChecksCommand" class="form-control" value="" />';
			tmpDiv+='	</div>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='		<label class="control-label">Path</label>';
			tmpDiv+='		<input type="text" name="healthChecksPath" class="form-control" value="'+healthChecksArray[i].path+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='		<label class="control-label">Grace Period&nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-question-sign" aria-hidden="true" title="Grace period in seconds"></span></label>';
			tmpDiv+='		<input type="number" name="healthChecksGracePeriodSeconds" class="form-control" value="'+healthChecksArray[i].gracePeriodSeconds+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='		<label class="control-label">Interval&nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-question-sign" aria-hidden="true" title="Interval in seconds"></span></label>';
			tmpDiv+='		<input type="number" name="healthChecksIntervalSeconds" class="form-control" value="'+healthChecksArray[i].intervalSeconds+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='		<label class="control-label">Timeout&nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-question-sign" aria-hidden="true" title="Timeout in seconds"></span></label>';
			tmpDiv+='		<input type="number" name="healthChecksTimeoutSeconds" class="form-control" value="'+healthChecksArray[i].timeoutSeconds+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='		<label class="control-label">Maximum Consecutive Failures</label>';
			tmpDiv+='		<input type="number" name="healthChecksMaxConsecutiveFailures" class="form-control" value="'+healthChecksArray[i].maxConsecutiveFailures+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='   <div class="initHealthCheckPort">';
			if(healthChecksArray[i].portIndex!==undefined){
				tmpDiv+='	<div class="col-sm-12">';
				tmpDiv+='		<label class="control-label">Port Index</label>';
				tmpDiv+='		<input type="number" name="healthChecksPortIndex" class="form-control" value="'+healthChecksArray[i].portIndex+'" />';
				tmpDiv+='	<div style="display:none">';
				tmpDiv+='		<input type="number" name="healthChecksPort" class="form-control" value="" />';
				tmpDiv+='	</div>';
				tmpDiv+='	</div>';
			}else{
				tmpDiv+='	<div class="col-sm-12">';
				tmpDiv+='		<label class="control-label">Port Number</label>';
				tmpDiv+='		<input type="number" name="healthChecksPort" class="form-control" value="'+healthChecksArray[i].port+'" />';
				tmpDiv+='	<div style="display:none">';
				tmpDiv+='		<input type="number" name="healthChecksPortIndex" class="form-control" value="" />';
				tmpDiv+='	</div>';
				tmpDiv+='	</div>';
			}
			tmpDiv+='   </div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<label class="control-label">Port Type</label>';
			tmpDiv+='		<select name="healthChecksPortType" class="form-control changePortType" onchange="initHealthCheckPort(this)">';
			if(healthChecksArray[i].portIndex!==undefined){
				tmpDiv+='			<option value="portIndex" selected="selected">Port Index</option>';
				tmpDiv+='			<option value="portNumber">Port Number</option>';
			}else{
				tmpDiv+='			<option value="portIndex">Port Index</option>';
				tmpDiv+='			<option value="portNumber" selected="selected">Port Number</option>';
			}
			tmpDiv+='		</select>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			if(healthChecksArray[i].ignoreHttp1xx){
				tmpDiv+='		<input type="checkbox" name="healthChecksIgnoreHttp1xx" value="true" checked="true">Ignore HTTP Status Codes 100 to 199';
			}else{
				tmpDiv+='		<input type="checkbox" name="healthChecksIgnoreHttp1xx" value="true">Ignore HTTP Status Codes 100 to 199';
			}
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
		}else if(healthChecksArray[i].protocol=='COMMAND'){
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	<div style="display:none">';
			tmpDiv+='		<input type="text" name="healthChecksPath" class="form-control" value="" />';
			tmpDiv+='		<input type="checkbox" name="healthChecksIgnoreHttp1xx" value="true"/>Ignore HTTP Status Codes 100 to 199';
			tmpDiv+='	</div>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='		<label class="control-label">COMMAND</label>';
			tmpDiv+='		<input type="text" name="healthChecksCommand" class="form-control" value="'+healthChecksArray[i].command.value+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='		<label class="control-label">Grace Period&nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-question-sign" aria-hidden="true" title="Grace period in seconds"></span></label>';
			tmpDiv+='		<input type="number" name="healthChecksGracePeriodSeconds" class="form-control" value="'+healthChecksArray[i].gracePeriodSeconds+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='		<label class="control-label">Interval&nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-question-sign" aria-hidden="true" title="Interval in seconds"></span></label>';
			tmpDiv+='		<input type="number" name="healthChecksIntervalSeconds" class="form-control" value="'+healthChecksArray[i].intervalSeconds+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='		<label class="control-label">Timeout&nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-question-sign" aria-hidden="true" title="Timeout in seconds"></span></label>';
			tmpDiv+='		<input type="number" name="healthChecksTimeoutSeconds" class="form-control" value="'+healthChecksArray[i].timeoutSeconds+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='		<label class="control-label">Maximum Consecutive Failures</label>';
			tmpDiv+='		<input type="number" name="healthChecksMaxConsecutiveFailures" class="form-control" value="'+healthChecksArray[i].maxConsecutiveFailures+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='   <div class="initHealthCheckPort">';
			if(healthChecksArray[i].portIndex!==undefined){
				tmpDiv+='	<div class="col-sm-12">';
				tmpDiv+='		<label class="control-label">Port Index</label>';
				tmpDiv+='		<input type="number" name="healthChecksPortIndex" class="form-control" value="'+healthChecksArray[i].portIndex+'" />';
				tmpDiv+='	<div style="display:none">';
				tmpDiv+='		<input type="number" name="healthChecksPort" class="form-control" value="" />';
				tmpDiv+='	</div>';
				tmpDiv+='	</div>';
			}else{
				tmpDiv+='	<div class="col-sm-12">';
				tmpDiv+='		<label class="control-label">Port Number</label>';
				tmpDiv+='		<input type="number" name="healthChecksPort" class="form-control" value="'+healthChecksArray[i].port+'" />';
				tmpDiv+='	<div style="display:none">';
				tmpDiv+='		<input type="number" name="healthChecksPortIndex" class="form-control" value="" />';
				tmpDiv+='	</div>';
				tmpDiv+='	</div>';
			}
			tmpDiv+='   </div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<label class="control-label">Port Type</label>';
			tmpDiv+='		<select name="healthChecksPortType" class="form-control changePortType" onchange="initHealthCheckPort(this)">';
			if(healthChecksArray[i].portIndex!==undefined){
				tmpDiv+='			<option value="portIndex" selected="selected">Port Index</option>';
				tmpDiv+='			<option value="portNumber">Port Number</option>';
			}else{
				tmpDiv+='			<option value="portIndex">Port Index</option>';
				tmpDiv+='			<option value="portNumber" selected="selected">Port Number</option>';
			}
			tmpDiv+='		</select>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
		}else{
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	<div style="display:none">';
			tmpDiv+='		<input type="text" name="healthChecksPath" class="form-control" value="" />';
			tmpDiv+='		<input type="text" name="healthChecksCommand" class="form-control" value="" />';
			tmpDiv+='		<input type="checkbox" name="healthChecksIgnoreHttp1xx" value="true"/>Ignore HTTP Status Codes 100 to 199';
			tmpDiv+='	</div>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='		<label class="control-label">Grace Period&nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-question-sign" aria-hidden="true" title="Grace period in seconds"></span></label>';
			tmpDiv+='		<input type="number" name="healthChecksGracePeriodSeconds" class="form-control" value="'+healthChecksArray[i].gracePeriodSeconds+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='		<label class="control-label">Interval&nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-question-sign" aria-hidden="true" title="Interval in seconds"></span></label>';
			tmpDiv+='		<input type="number" name="healthChecksIntervalSeconds" class="form-control" value="'+healthChecksArray[i].intervalSeconds+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='		<label class="control-label">Timeout&nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-question-sign" aria-hidden="true" title="Timeout in seconds"></span></label>';
			tmpDiv+='		<input type="number" name="healthChecksTimeoutSeconds" class="form-control" value="'+healthChecksArray[i].timeoutSeconds+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='		<label class="control-label">Maximum Consecutive Failures</label>';
			tmpDiv+='		<input type="number" name="healthChecksMaxConsecutiveFailures" class="form-control" value="'+healthChecksArray[i].maxConsecutiveFailures+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='   <div class="initHealthCheckPort">';
			if(healthChecksArray[i].portIndex!==undefined){
				tmpDiv+='	<div class="col-sm-12">';
				tmpDiv+='		<label class="control-label">Port Index</label>';
				tmpDiv+='		<input type="number" name="healthChecksPortIndex" class="form-control" value="'+healthChecksArray[i].portIndex+'" />';
				tmpDiv+='	<div style="display:none">';
				tmpDiv+='		<input type="number" name="healthChecksPort" class="form-control" value="" />';
				tmpDiv+='	</div>';
				tmpDiv+='	</div>';
			}else{
				tmpDiv+='	<div class="col-sm-12">';
				tmpDiv+='		<label class="control-label">Port Number</label>';
				tmpDiv+='		<input type="number" name="healthChecksPort" class="form-control" value="'+healthChecksArray[i].port+'" />';
				tmpDiv+='	<div style="display:none">';
				tmpDiv+='		<input type="number" name="healthChecksPortIndex" class="form-control" value="" />';
				tmpDiv+='	</div>';
				tmpDiv+='	</div>';
			}
			tmpDiv+='   </div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<label class="control-label">Port Type</label>';
			tmpDiv+='		<select name="healthChecksPortType" class="form-control changePortType" onchange="initHealthCheckPort(this)">';
			if(healthChecksArray[i].portIndex!==undefined){
				tmpDiv+='			<option value="portIndex" selected="selected">Port Index</option>';
				tmpDiv+='			<option value="portNumber">Port Number</option>';
			}else{
				tmpDiv+='			<option value="portIndex">Port Index</option>';
				tmpDiv+='			<option value="portNumber" selected="selected">Port Number</option>';
			}
			tmpDiv+='		</select>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
		}
		tmpDiv+='   </div>';
		tmpDiv+='</div>';
		$("#addHealthCheckDiv").append(tmpDiv);	
	}
}

function formatHealthChecksArray(data){
	var tmphealthChecksProtocolArray = new Array();
	var i=0;
	$("select[name='healthChecksProtocol']").each(function(){
		tmphealthChecksProtocolArray[i] = $(this).val(); 
		i = i+1;
	});
	data.healthChecksProtocol = tmphealthChecksProtocolArray;
	var tmphealthChecksPathArray = new Array();
	var i=0;
	$("input[type='text'][name='healthChecksPath']").each(function(){
		tmphealthChecksPathArray[i] = $(this).val(); 
		i = i+1;
	});
	data.healthChecksPath = tmphealthChecksPathArray;
	var tmphealthChecksCommandArray = new Array();
	var i=0;
	$("input[type='text'][name='healthChecksCommand']").each(function(){
		tmphealthChecksCommandArray[i] = $(this).val(); 
		i = i+1;
	});
	data.healthChecksCommand = tmphealthChecksCommandArray;
	var tmphealthChecksGracePeriodSecondsArray = new Array();
	var i=0;
	$("input[type='number'][name='healthChecksGracePeriodSeconds']").each(function(){
		tmphealthChecksGracePeriodSecondsArray[i] = $(this).val(); 
		i = i+1;
	});
	data.healthChecksGracePeriodSeconds = tmphealthChecksGracePeriodSecondsArray;
	var tmphealthChecksIntervalSecondsArray = new Array();
	var i=0;
	$("input[type='number'][name='healthChecksIntervalSeconds']").each(function(){
		tmphealthChecksIntervalSecondsArray[i] = $(this).val(); 
		i = i+1;
	});
	data.healthChecksIntervalSeconds = tmphealthChecksIntervalSecondsArray;
	var tmphealthChecksTimeoutSecondsArray = new Array();
	var i=0;
	$("input[type='number'][name='healthChecksTimeoutSeconds']").each(function(){
		tmphealthChecksTimeoutSecondsArray[i] = $(this).val(); 
		i = i+1;
	});
	data.healthChecksTimeoutSeconds = tmphealthChecksTimeoutSecondsArray;
	var tmphealthChecksMaxConsecutiveFailuresArray = new Array();
	var i=0;
	$("input[type='number'][name='healthChecksMaxConsecutiveFailures']").each(function(){
		tmphealthChecksMaxConsecutiveFailuresArray[i] = $(this).val(); 
		i = i+1;
	});
	data.healthChecksMaxConsecutiveFailures = tmphealthChecksMaxConsecutiveFailuresArray;
	var tmphealthChecksPortIndexArray = new Array();
	var i=0;
	$("input[type='number'][name='healthChecksPortIndex']").each(function(){
		tmphealthChecksPortIndexArray[i] = $(this).val(); 
		i = i+1;
	});
	data.healthChecksPortIndex = tmphealthChecksPortIndexArray;
	var tmphealthChecksPortArray = new Array();
	var i=0;
	$("input[type='number'][name='healthChecksPort']").each(function(){
		tmphealthChecksPortArray[i] = $(this).val(); 
		i = i+1;
	});
	data.healthChecksPort = tmphealthChecksPortArray;
	var tmphealthChecksPortTypeArray = new Array();
	var i=0;
	$("select[name='healthChecksPortType']").each(function(){
		tmphealthChecksPortTypeArray[i] = $(this).val(); 
		i = i+1;
	});
	data.healthChecksPortType = tmphealthChecksPortTypeArray;
	var tmphealthChecksIgnoreHttp1xxArray = new Array();
	var i=0;
	$("input[type='checkbox'][name='healthChecksIgnoreHttp1xx']").each(function(){
		tmphealthChecksIgnoreHttp1xxArray[i] = $(this).prop("checked");
		i = i+1;
	});
	data.healthChecksIgnoreHttp1xx = tmphealthChecksIgnoreHttp1xxArray;
	//console.log('healthChecksIgnoreHttp1xx==='+data.healthChecksIgnoreHttp1xx);
}