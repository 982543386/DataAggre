$(document).ready(function(){ 
	
	/*Labels addLabel*/
	$("#addLabel").on("click",function(){
		var tmpDiv = "";
		tmpDiv+='<div class="reduceLabelDiv">';
		tmpDiv+='	<div class="col-sm-5">';
		tmpDiv+='		<label class="control-label">Label Name</label>';
		tmpDiv+='		<input type="text" name="labelsKey" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-5">';
		tmpDiv+='		<label class="control-label">Label Value</label>';
		tmpDiv+='		<input type="text" name="labelsValue" class="form-control" value="" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-2">';
		tmpDiv+='		<label class="control-label"></label>';
		tmpDiv+='		<div onclick="reduceLabel(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 14px 12px;"></span>';
		tmpDiv+='		</div>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='</div>';
		$("#addLabelDiv").append(tmpDiv);
	});
	//$("#addLabel").click();
	
});

/*Labels reduceLabel*/
function reduceLabel(obj){
	$(obj).closest(".reduceLabelDiv").remove();
}

function editLables(labelsArray){
	var tmpDiv = "";
	jQuery.each(labelsArray, function(key, val) {  
		tmpDiv+='<div class="reduceLabelDiv">';
		tmpDiv+='	<div class="col-sm-5">';
		tmpDiv+='		<label class="control-label">Label Name</label>';
		tmpDiv+='		<input type="text" name="labelsKey" class="form-control" value="'+key+'" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-5">';
		tmpDiv+='		<label class="control-label">Label Value</label>';
		tmpDiv+='		<input type="text" name="labelsValue" class="form-control" value="'+val+'" />';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-2">';
		tmpDiv+='		<label class="control-label"></label>';
		tmpDiv+='		<div onclick="reduceLabel(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true" style="display: block;width: 100%;height: 34px;padding: 14px 12px;"></span>';
		tmpDiv+='		</div>';
		tmpDiv+='	</div>';
		tmpDiv+='	<div class="col-sm-12">';
		tmpDiv+='	&nbsp;';
		tmpDiv+='	</div>';
		tmpDiv+='</div>';
	});  
	$("#addLabelDiv").append(tmpDiv);
}

function formatLabelsArray(data){
	var tmplabelsKeyArray = new Array();
	var i=0;
	$("input[type='text'][name='labelsKey']").each(function(){
		tmplabelsKeyArray[i] = $(this).val(); 
		i = i+1;
	});
	data.labelsKey = tmplabelsKeyArray;
	var tmplabelsValueArray = new Array();
	var i=0;
	$("input[type='text'][name='labelsValue']").each(function(){
		tmplabelsValueArray[i] = $(this).val(); 
		i = i+1;
	});
	data.labelsValue = tmplabelsValueArray;
}