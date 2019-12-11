$(document).ready(function(){ 
	
	/*Environment Variables addEnvironmentVariable*/
	$("#addEnvironmentVariable").on("click",function(){
		var tmpDiv = "";
		tmpDiv+='<div class="reduceEnvironmentVariableDiv">';
		tmpDiv+='	<div class="col-sm-5">';
		tmpDiv+='		<label class="control-label">Key</label>';
		tmpDiv+='		<input type="text" name="environmentVariablesKey" class="form-control" value="" style="text-transform:uppercase;"/>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-5">';
		tmpDiv+='		<label class="control-label">Value</label>';
		tmpDiv+='		<input type="text" name="environmentVariablesValue" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-2">';
		tmpDiv+='		<label class="control-label"></label>';
		tmpDiv+='		<div onclick="reduceEnvironmentVariable(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 14px 12px;"></span>';
		tmpDiv+='		</div>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='</div>';
		$("#addEnvironmentVariableDiv").append(tmpDiv);
	});
	//$("#addEnvironmentVariable").click();
	
});

/*Environment Variables reduceEndpoint*/
function reduceEnvironmentVariable(obj){
	$(obj).closest(".reduceEnvironmentVariableDiv").remove();
}

function editEnvironmentVariable(environmentVariableArray){
	var tmpDiv = "";
	jQuery.each(environmentVariableArray, function(key, val) {  
		tmpDiv+='<div class="reduceEnvironmentVariableDiv">';
		tmpDiv+='	<div class="col-sm-5">';
		tmpDiv+='		<label class="control-label">Key</label>';
		tmpDiv+='		<input type="text" name="environmentVariablesKey" class="form-control" value="'+key+'" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-5">';
		tmpDiv+='		<label class="control-label">Value</label>';
		tmpDiv+='		<input type="text" name="environmentVariablesValue" class="form-control" value="'+val+'" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-2">';
		tmpDiv+='		<label class="control-label"></label>';
		tmpDiv+='		<div onclick="reduceEnvironmentVariable(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 14px 12px;"></span>';
		tmpDiv+='		</div>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='</div>';
	});  
	$("#addEnvironmentVariableDiv").append(tmpDiv);
}

function formatEnvironmentVariablesArray(data){
	var tmpenvironmentVariablesKeyArray = new Array();
	var i=0;
	$("input[type='text'][name='environmentVariablesKey']").each(function(){
		tmpenvironmentVariablesKeyArray[i] = $(this).val().toUpperCase(); 
		i = i+1;
	});
	data.environmentVariablesKey = tmpenvironmentVariablesKeyArray;
	var tmpenvironmentVariablesValueArray = new Array();
	var i=0;
	$("input[type='text'][name='environmentVariablesValue']").each(function(){
		tmpenvironmentVariablesValueArray[i] = $(this).val(); 
		i = i+1;
	});
	data.environmentVariablesValue = tmpenvironmentVariablesValueArray;
}