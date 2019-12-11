$(document).ready(function(){ 
	
	/*Network addAnEndpoint*/
	$("#addAnEndpoint").on("click",function(){
		var dockerNetwork = $("#dockerNetwork").val();
		var tmpDiv = "";
		if(dockerNetwork=='HOST'){
			tmpDiv+='<div class="reduceEndpointDiv">';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<label class="control-label">LB Port</label>';
			tmpDiv+='		<input type="number" min="1" name="netWorkContainerPort" class="form-control" value="" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<label class="control-label">Name</label>';
			tmpDiv+='		<input type="text" name="netWorkName" class="form-control" value="" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-2">';
			tmpDiv+='		<label class="control-label">Protocol</label>';
			tmpDiv+='		<select name="netWorkProtocol" class="form-control">';
			tmpDiv+='			<option value="tcp">tcp</option>';
			tmpDiv+='			<option value="udp">udp</option>';
			tmpDiv+='			<option value="udp,tcp">udp,tcp</option>';
			tmpDiv+='		</select>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<div style="display: block;width: 100%;height: 34px;padding: 30px 12px;"><input type="checkbox" name="netWorkLoadBalanced" value="true" >Load Balanced</div>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-1">';
			tmpDiv+='		<div onclick="reduceEndpoint(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 35px 12px;"></span>';
			tmpDiv+='		</div>';
			tmpDiv+='	</div>';
			tmpDiv+='</div>';
		}else if(dockerNetwork=='BRIDGE'){
			tmpDiv+='<div class="reduceEndpointDiv">';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<label class="control-label">Container Port</label>';
			tmpDiv+='		<input type="number" min="1" name="port" class="form-control" value="" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<label class="control-label">Name</label>';
			tmpDiv+='		<input type="text" name="name" class="form-control" value="" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-2">';
			tmpDiv+='		<label class="control-label">Protocol</label>';
			tmpDiv+='		<select name="protocol" class="form-control">';
			tmpDiv+='			<option value="tcp">tcp</option>';
			tmpDiv+='			<option value="udp">udp</option>';
			tmpDiv+='			<option value="udp,tcp">udp,tcp</option>';
			tmpDiv+='		</select>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<div style="display: block;width: 100%;height: 34px;padding: 30px 12px;"><input type="checkbox" name="privileged" value="true" >Load Balanced</div>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-1">';
			tmpDiv+='		<div onclick="reduceEndpoint(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 35px 12px;"></span>';
			tmpDiv+='		</div>';
			tmpDiv+='	</div>';
			tmpDiv+='</div>';
		}else{
			tmpDiv+='<div class="reduceEndpointDiv">';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<label class="control-label">Container Port</label>';
			tmpDiv+='		<input type="number" min="1" name="netWorkContainerPort" class="form-control" value="" onchange="initNetWorkEndpoint(this)"/>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<label class="control-label">Name</label>';
			tmpDiv+='		<input type="text" name="netWorkName" class="form-control" value="" onchange="initNetWorkEndpoint(this)"/>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-2">';
			tmpDiv+='		<label class="control-label">Protocol</label>';
			tmpDiv+='		<select name="netWorkProtocol" class="form-control" onchange="initNetWorkEndpoint(this)">';
			tmpDiv+='			<option value="tcp">tcp</option>';
			tmpDiv+='			<option value="udp">udp</option>';
			tmpDiv+='			<option value="udp,tcp">udp,tcp</option>';
			tmpDiv+='		</select>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<div style="display: block;width: 100%;height: 34px;padding: 30px 12px;"><input type="checkbox" name="privileged" value="true" >Load Balanced</div>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-1">';
			tmpDiv+='		<div onclick="reduceEndpoint(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 35px 12px;"></span>';
			tmpDiv+='		</div>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-1">';	
			tmpDiv+='		<input type="checkbox" name="netWorkEndpoint" value="true" >';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-3">';	
			tmpDiv+='		<div class="initNetWorkEndpoint"></div>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-8">';	
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';	
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='</div>';
		}
		$("#addAnEndpointDiv").append(tmpDiv);
		//初始化endpoint
		initNetWorkEndpoint($(".reduceEndpointDiv").last().find("select[name='netWorkProtocol']"));
	});
	//$("#addAnEndpoint").click();
	
});

/*Network reduceEndpoint*/
function reduceEndpoint(obj){
	$(obj).closest(".reduceEndpointDiv").remove();
}

function initDockerNetworkContent(){
	$(".reduceEndpointDiv").remove();
}

function initNetworkType(){
	var dockerImage = $("#dockerImage").val();
	if(dockerImage==''){
		$("#initDockerNetwork").hide();
	}else{
		$("#initDockerNetwork").show();
	}
}

/*PortMapping */
function editDockerNetWork(netWorkArray){
	var dockerNetwork = $("#dockerNetwork").val();
	for(var i=0;i<netWorkArray.length;i++){
		var tmpDiv = "";
		var tmpName = netWorkArray[i].name==null?"":netWorkArray[i].name;
		if(dockerNetwork=='HOST'){
			tmpDiv+='<div class="reduceEndpointDiv">';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<label class="control-label">LB Port</label>';
			tmpDiv+='		<input type="number" min="1" name="netWorkContainerPort" class="form-control" value="'+netWorkArray[i].containerPort+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<label class="control-label">Name</label>';
			tmpDiv+='		<input type="text" name="netWorkName" class="form-control" value="'+tmpName+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-2">';
			tmpDiv+='		<label class="control-label">Protocol</label>';
			tmpDiv+='		<select name="netWorkProtocol" class="form-control">';
			tmpDiv+='			<option value="tcp">tcp</option>';
			tmpDiv+='			<option value="udp">udp</option>';
			tmpDiv+='			<option value="udp,tcp">udp,tcp</option>';
			tmpDiv+='		</select>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<div style="display: block;width: 100%;height: 34px;padding: 30px 12px;"><input type="checkbox" name="netWorkLoadBalanced" value="true" >Load Balanced</div>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-1">';
			tmpDiv+='		<div onclick="reduceEndpoint(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 35px 12px;"></span>';
			tmpDiv+='		</div>';
			tmpDiv+='	</div>';
			tmpDiv+='</div>';
		}else if(dockerNetwork=='BRIDGE'){
			tmpDiv+='<div class="reduceEndpointDiv">';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<label class="control-label">Container Port</label>';
			tmpDiv+='		<input type="number" min="1" name="port" class="form-control" value="'+netWorkArray[i].containerPort+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<label class="control-label">Name</label>';
			tmpDiv+='		<input type="text" name="name" class="form-control" value="'+tmpName+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-2">';
			tmpDiv+='		<label class="control-label">Protocol</label>';
			tmpDiv+='		<select name="protocol" class="form-control">';
			tmpDiv+='			<option value="tcp">tcp</option>';
			tmpDiv+='			<option value="udp">udp</option>';
			tmpDiv+='			<option value="udp,tcp">udp,tcp</option>';
			tmpDiv+='		</select>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<div style="display: block;width: 100%;height: 34px;padding: 30px 12px;"><input type="checkbox" name="privileged" value="true" >Load Balanced</div>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-1">';
			tmpDiv+='		<div onclick="reduceEndpoint(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 35px 12px;"></span>';
			tmpDiv+='		</div>';
			tmpDiv+='	</div>';
			tmpDiv+='</div>';
		}else{
			tmpDiv+='<div class="reduceEndpointDiv">';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<label class="control-label">Container Port</label>';
			tmpDiv+='		<input type="number" min="1" name="netWorkContainerPort" class="form-control" value="'+netWorkArray[i].containerPort+'" onchange="initNetWorkEndpoint(this)"/>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<label class="control-label">Name</label>';
			tmpDiv+='		<input type="text" name="netWorkName" class="form-control" value="'+tmpName+'" onchange="initNetWorkEndpoint(this)"/>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-2">';
			tmpDiv+='		<label class="control-label">Protocol</label>';
			tmpDiv+='		<select name="netWorkProtocol" class="form-control" onchange="initNetWorkEndpoint(this)">';
			tmpDiv+='			<option value="tcp">tcp</option>';
			tmpDiv+='			<option value="udp">udp</option>';
			tmpDiv+='			<option value="udp,tcp">udp,tcp</option>';
			tmpDiv+='		</select>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<div style="display: block;width: 100%;height: 34px;padding: 30px 12px;"><input type="checkbox" name="privileged" value="true" >Load Balanced</div>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-1">';
			tmpDiv+='		<div onclick="reduceEndpoint(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 35px 12px;"></span>';
			tmpDiv+='		</div>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-1">';	
			tmpDiv+='		<input type="checkbox" name="netWorkEndpoint" value="true" >';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-3">';	
			tmpDiv+='		<div class="initNetWorkEndpoint"></div>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-8">';	
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';	
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='</div>';
		}
		$("#addAnEndpointDiv").append(tmpDiv);
		//初始化select checkbox的值
		$(".reduceEndpointDiv").last().find("select[name='netWorkProtocol'][value='"+netWorkArray[i].protocol+"']").prop("selected","selected");
		if(netWorkArray[i].labels){
			$(".reduceEndpointDiv").last().find("checkbox[name='netWorkLoadBalanced']").prop("checked",true);
		}else{
			$(".reduceEndpointDiv").last().find("checkbox[name='netWorkLoadBalanced']").prop("checked",false);
		}
		if(netWorkArray[i].hostPort){
			$(".reduceEndpointDiv").last().find("checkbox[name='netWorkEndpoint']").prop("checked",true);
		}else{
			$(".reduceEndpointDiv").last().find("checkbox[name='netWorkEndpoint']").prop("checked",false);
		}
		//初始化endpoint
		initNetWorkEndpoint($(".reduceEndpointDiv").last().find("select[name='netWorkProtocol']"));
	}
}

function initNetWorkEndpoint(obj){
	var netWorkContainerPort = $(obj).closest(".reduceEndpointDiv").find("input[type='number'][name='netWorkContainerPort']").val();
	netWorkContainerPort = netWorkContainerPort==""?"0":netWorkContainerPort;
	var netWorkProtocol = $(obj).closest(".reduceEndpointDiv").find("select[name='netWorkProtocol']").val();
	var netWorkName = $(obj).closest(".reduceEndpointDiv").find("input[type='text'][name='netWorkName']").val();
	netWorkName = netWorkName==""?"null":netWorkName;
	$(obj).closest(".reduceEndpointDiv").find(".initNetWorkEndpoint").html(netWorkName+" ("+netWorkContainerPort+"/"+netWorkProtocol+")");
}

function formatNetWorkArray(data){
	var tmpnetWorkContainerPortArray = new Array();
	var i=0;
	$("input[type='number'][name='netWorkContainerPort']").each(function(){
		tmpnetWorkContainerPortArray[i] = $(this).val(); 
		i = i+1;
	});
	data.netWorkContainerPort = tmpnetWorkContainerPortArray;
	var tmpnetWorkNameArray = new Array();
	var i=0;
	$("input[type='text'][name='netWorkName']").each(function(){
		tmpnetWorkNameArray[i] = $(this).val(); 
		i = i+1;
	});
	data.netWorkName = tmpnetWorkNameArray;
	var tmpnetWorkProtocolArray = new Array();
	var i=0;
	$("select[name='netWorkProtocol']").each(function(){
		tmpnetWorkProtocolArray[i] = $(this).val(); 
		i = i+1;
	});
	data.netWorkProtocol = tmpnetWorkProtocolArray;
	var tmpnetWorkLoadBalancedArray = new Array();
	var i=0;
	$("input[type='checkbox'][name='netWorkLoadBalanced']").each(function(){
		tmpnetWorkLoadBalancedArray[i] = $(this).prop("checked");
		i = i+1;
	});
	data.netWorkLoadBalanced = tmpnetWorkLoadBalancedArray;
	var tmpnetWorkEndpointArray = new Array();
	var i=0;
	$("input[type='checkbox'][name='netWorkEndpoint']").each(function(){
		tmpnetWorkEndpointArray[i] = $(this).prop("checked");
		i = i+1;
	});
	data.netWorkEndpoint = tmpnetWorkEndpointArray;
}

