$(document).ready(function(){ 
	
	/*Volumes addLocalVolume*/
	$("#addLocalVolume").on("click",function(){
		var tmpDiv = "";
		tmpDiv+='<div class="reduceLocalVolumeDiv">';
		tmpDiv+='	<div class="col-sm-5">';
		tmpDiv+='		<label class="control-label">Size (MiB)</label>';
		tmpDiv+='		<input type="number" name="localVolumeSize" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-5">';
		tmpDiv+='		<label class="control-label">Container Path</label>';
		tmpDiv+='		<input type="text" name="localVolumeContainerPath" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-2">';
		tmpDiv+='		<label class="control-label"></label>';
		tmpDiv+='		<div onclick="reduceLocalVolume(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 14px 12px;"></span>';
		tmpDiv+='		</div>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='</div>';
		$("#addLocalVolumeDiv").append(tmpDiv);
	});
	//$("#addLocalVolume").click();
	
	/*Volumes addContainerVolume*/
	$("#addContainerVolume").on("click",function(){
		var tmpDiv = "";
		tmpDiv+='<div class="reduceContainerVolumeDiv">';
		tmpDiv+='	<div class="col-sm-3">';
		tmpDiv+='		<label class="control-label">Container Path</label>';
		tmpDiv+='		<input type="text" name="containerVolumeContainerPath" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-4">';
		tmpDiv+='		<label class="control-label">Host Path</label>';
		tmpDiv+='		<input type="text" name="containerVolumeHostPath" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-3">';
		tmpDiv+='		<label class="control-label">Mode</label>';
		tmpDiv+='		<select name="containerVolumeMode" class="form-control">';
		tmpDiv+='			<option value="RO">Read Only</option>';
		tmpDiv+='			<option value="RW">Read And Write</option>';
		tmpDiv+='		</select>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-2">';
		tmpDiv+='		<label class="control-label"></label>';
		tmpDiv+='		<div onclick="reduceContainerVolume(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 14px 12px;"></span>';
		tmpDiv+='		</div>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='</div>';
		$("#addContainerVolumeDiv").append(tmpDiv);
	});
	//$("#addContainerVolume").click();
	
	/*Volumes addExternalVolume*/
	$("#addExternalVolume").on("click",function(){
		var tmpDiv = "";
		tmpDiv+='<div class="reduceExternalVolumeDiv">';
		tmpDiv+='	<div class="col-sm-5">';
		tmpDiv+='		<label class="control-label">Volume Name</label>';
		tmpDiv+='		<input type="text" name="externalVolumeName" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-5">';
		tmpDiv+='		<label class="control-label">Container Path</label>';
		tmpDiv+='		<input type="text" name="externalVolumeContainerPath" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-2">';
		tmpDiv+='		<label class="control-label"></label>';
		tmpDiv+='		<div onclick="reduceExternalVolume(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 14px 12px;"></span>';
		tmpDiv+='		</div>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='</div>';
		$("#addExternalVolumeDiv").append(tmpDiv);
	});
	//$("#addExternalVolume").click();
	
});

/*Volumes reduceLocalVolume*/
function reduceLocalVolume(obj){
	$(obj).closest(".reduceLocalVolumeDiv").remove();
}

/*Volumes reduceContainerVolume*/
function reduceContainerVolume(obj){
	$(obj).closest(".reduceContainerVolumeDiv").remove();
}

/*Volumes reduceExternalVolume*/
function reduceExternalVolume(obj){
	$(obj).closest(".reduceExternalVolumeDiv").remove();
}

function editVolumes(volumeArray){
	//console.log('volumes:'+volumes);
	//根据persistent和external来判断是哪类volume
	for(var i=0;i<volumeArray.length;i++){
		if(volumeArray[i].external){
			var tmpDiv = "";
			tmpDiv+='<div class="reduceExternalVolumeDiv">';
			tmpDiv+='	<div class="col-sm-5">';
			tmpDiv+='		<label class="control-label">Volume Name</label>';
			tmpDiv+='		<input type="text" name="externalVolumeName" class="form-control" value="'+volumeArray[i].external.name+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-5">';
			tmpDiv+='		<label class="control-label">Container Path</label>';
			tmpDiv+='		<input type="text" name="externalVolumeContainerPath" class="form-control" value="'+volumeArray[i].containerPath+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-2">';
			tmpDiv+='		<label class="control-label"></label>';
			tmpDiv+='		<div onclick="reduceExternalVolume(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 14px 12px;"></span>';
			tmpDiv+='		</div>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='</div>';
			$("#addExternalVolumeDiv").append(tmpDiv);
		}else if(volumeArray[i].persistent){
			var tmpDiv = "";
				tmpDiv+='<div class="reduceLocalVolumeDiv">';
				tmpDiv+='	<div class="col-sm-5">';
				tmpDiv+='		<label class="control-label">Size (MiB)</label>';
				tmpDiv+='		<input type="number" name="localVolumeSize" class="form-control" value="'+volumeArray[i].persistent.size+'" />';
				tmpDiv+='	</div>';
				tmpDiv+='	<div class="col-sm-5">';
				tmpDiv+='		<label class="control-label">Container Path</label>';
				tmpDiv+='		<input type="text" name="localVolumeContainerPath" class="form-control" value="'+volumeArray[i].containerPath+'" />';
				tmpDiv+='	</div>';
				tmpDiv+='	<div class="col-sm-2">';
				tmpDiv+='		<label class="control-label"></label>';
				tmpDiv+='		<div onclick="reduceLocalVolume(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 14px 12px;"></span>';
				tmpDiv+='		</div>';
				tmpDiv+='	</div>';
				tmpDiv+='	<div class="col-sm-12">';
				tmpDiv+='	&nbsp;';
				tmpDiv+='	</div>';
				tmpDiv+='</div>';
			$("#addLocalVolumeDiv").append(tmpDiv);
		}else{
			var tmpDiv = "";
			tmpDiv+='<div class="reduceContainerVolumeDiv">';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<label class="control-label">Container Path</label>';
			tmpDiv+='		<input type="text" name="containerVolumeContainerPath" class="form-control" value="'+volumeArray[i].containerPath+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-4">';
			tmpDiv+='		<label class="control-label">Host Path</label>';
			tmpDiv+='		<input type="text" name="containerVolumeHostPath" class="form-control" value="'+volumeArray[i].hostPath+'" />';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-3">';
			tmpDiv+='		<label class="control-label">Mode</label>';
			tmpDiv+='		<select name="containerVolumeMode" class="form-control">';
			if(volumeArray[i].mode=='RO'){
				tmpDiv+='			<option value="RO" selected="selected">Read Only</option>';
				tmpDiv+='			<option value="RW">Read And Write</option>';
			}else{
				tmpDiv+='			<option value="RO">Read Only</option>';
				tmpDiv+='			<option value="RW" selected="selected">Read And Write</option>';
			}
			tmpDiv+='		</select>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-2">';
			tmpDiv+='		<label class="control-label"></label>';
			tmpDiv+='		<div onclick="reduceContainerVolume(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 14px 12px;"></span>';
			tmpDiv+='		</div>';
			tmpDiv+='	</div>';
			tmpDiv+='	<div class="col-sm-12">';
			tmpDiv+='	&nbsp;';
			tmpDiv+='	</div>';
			tmpDiv+='</div>';
			$("#addContainerVolumeDiv").append(tmpDiv);
		}
	}
}

function formatVolumesArray(data){
	var tmplocalVolumeSizeArray = new Array();
	var i=0;
	$("input[type='number'][name='localVolumeSize']").each(function(){
		tmplocalVolumeSizeArray[i] = $(this).val(); 
		i = i+1;
	});
	data.localVolumeSize = tmplocalVolumeSizeArray;
	var tmplocalVolumeContainerPathArray = new Array();
	var i=0;
	$("input[type='text'][name='localVolumeContainerPath']").each(function(){
		tmplocalVolumeContainerPathArray[i] = $(this).val(); 
		i = i+1;
	});
	data.localVolumeContainerPath = tmplocalVolumeContainerPathArray;
	var tmpcontainerVolumeContainerPathArray = new Array();
	var i=0;
	$("input[type='text'][name='containerVolumeContainerPath']").each(function(){
		tmpcontainerVolumeContainerPathArray[i] = $(this).val(); 
		i = i+1;
	});
	data.containerVolumeContainerPath = tmpcontainerVolumeContainerPathArray;
	var tmpcontainerVolumeHostPathArray = new Array();
	var i=0;
	$("input[type='text'][name='containerVolumeHostPath']").each(function(){
		tmpcontainerVolumeHostPathArray[i] = $(this).val(); 
		i = i+1;
	});
	data.containerVolumeHostPath = tmpcontainerVolumeHostPathArray;
	var tmpcontainerVolumeModeArray = new Array();
	var i=0;
	$("select[name='containerVolumeMode']").each(function(){
		tmpcontainerVolumeModeArray[i] = $(this).val(); 
		i = i+1;
	});
	data.containerVolumeMode = tmpcontainerVolumeModeArray;
	var tmpexternalVolumeNameArray = new Array();
	var i=0;
	$("input[type='text'][name='externalVolumeName']").each(function(){
		tmpexternalVolumeNameArray[i] = $(this).val(); 
		i = i+1;
	});
	data.externalVolumeName = tmpexternalVolumeNameArray;
	var tmpexternalVolumeContainerPathArray = new Array();
	var i=0;
	$("input[type='text'][name='externalVolumeContainerPath']").each(function(){
		tmpexternalVolumeContainerPathArray[i] = $(this).val(); 
		i = i+1;
	});
	data.externalVolumeContainerPath = tmpexternalVolumeContainerPathArray;
}
