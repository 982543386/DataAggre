$(document).ready(function(){ 
	
	/*Container Settings addParameter*/
	$("#addParameter").on("click",function(){
		var tmpDiv = "";
		tmpDiv+='<div class="reduceParameterDiv">';
		tmpDiv+='	<div class="col-sm-5">';
		tmpDiv+='		<label class="control-label">Key</label>';
		tmpDiv+='		<input type="text" name="dockerParameterKey" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-5">';
		tmpDiv+='		<label class="control-label">Value</label>';
		tmpDiv+='		<input type="text" name="dockerParameterValue" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-2">';
		tmpDiv+='		<label class="control-label"></label>';
		tmpDiv+='		<div onclick="reduceParameter(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 14px 12px;"></span>';
		tmpDiv+='		</div>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='</div>';
		$("#addParameterDiv").append(tmpDiv);
	});
	//$("#addParameter").click();
	
});

/*Container Settings reduceParameter*/
function reduceParameter(obj){
	$(obj).closest(".reduceParameterDiv").remove();
}

function editDockerParameter(dockerParameter){
	var tmpDiv = "";
	for(var i=0;i<dockerParameter.length;i++){
		tmpDiv+='<div class="reduceParameterDiv">';
		tmpDiv+='	<div class="col-sm-5">';
		tmpDiv+='		<label class="control-label">Key</label>';
		tmpDiv+='		<input type="text" name="dockerParameterKey" class="form-control" value="'+dockerParameter[i].key+'" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-5">';
		tmpDiv+='		<label class="control-label">Value</label>';
		tmpDiv+='		<input type="text" name="dockerParameterValue" class="form-control" value="'+dockerParameter[i].value+'" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-2">';
		tmpDiv+='		<label class="control-label"></label>';
		tmpDiv+='		<div onclick="reduceParameter(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 14px 12px;"></span>';
		tmpDiv+='		</div>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='</div>';
	}
	$("#addParameterDiv").append(tmpDiv);
}

function formatContainerSettingsArray(data){
	var tmpdockerParameterKeyArray = new Array();
	var i=0;
	$("input[type='text'][name='dockerParameterKey']").each(function(){
		tmpdockerParameterKeyArray[i] = $(this).val(); 
		i = i+1;
	});
	data.dockerParameterKey = tmpdockerParameterKeyArray;
	var tmpdockerParameterValueArray = new Array();
	var i=0;
	$("input[type='text'][name='dockerParameterValue']").each(function(){
		tmpdockerParameterValueArray[i] = $(this).val(); 
		i = i+1;
	});
	data.dockerParameterValue = tmpdockerParameterValueArray;
}