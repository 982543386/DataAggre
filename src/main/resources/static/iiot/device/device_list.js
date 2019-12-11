var table = null;

$(function () {
	queryAllDevice();
	
	$('#deviceTable tbody').on( 'click', 'button.delete', function (e) {
		var data = table.row($(this).parents('tr')).data();
		e.preventDefault();
		$('#deleteModalMsg').html('确定删除 <strong>' + data.deviceName + '</strong>？');
		$('#deleteModal').modal().one('click', '#modal-btn', function() {
			$(e.target).button('loading');
			removeDevice(data);
			table.ajax.reload();
			//table.ajax.url("/iiot/device/queryAllDevice").load();
		});
	});
	
	$('#deviceTable tbody').on( 'click', 'button.set', function (e) {
		var data = table.row($(this).parents('tr')).data();
		e.preventDefault();
		$("#editForm").show();
		$("#table").hide();
		loadNodeSelect();
		clearForm();
		loadEditForm(data);
	});
	
	$('#deviceTable tbody').on( 'click', 'button.test', function (e) {
		var data = table.row($(this).parents('tr')).data();
		e.preventDefault();
		$('.content').load('iiot/device/test_mqtt.html');
		$.getScript("iiot/device/mqtt_test.js");
		sessionStorage.setItem("ip", data.nodeIp);
		sessionStorage.setItem("topic", data.topicNanme);
	});
	
	$('#submit_btn').on( 'click', function (e) {
		e.preventDefault();
		if (!checkForm()) {
			return false;
		}
		//根据表单ID取值长度判断执行修改或新增
		var id = $('#id').val();
		if (id.length > 0) {
			modifyDevice();
		}else {
			createDevice();
		}
		table.ajax.reload();
		$("#table").show();
		$("#editForm").hide();
	});
	
	$('#add_btn').on( 'click', function (e) {
		e.preventDefault();
		loadNodeSelect();
		clearForm();
		$("#editForm").show();
		$("#table").hide();
	});
	
	$('#return_btn').on( 'click', function (e) {
		e.preventDefault();
		$("#table").show();
		$("#editForm").hide();
	});
	
	$('#deviceTable tbody').on( 'click', 'a.start', function (e) {
		var data = table.row($(this).parents('tr')).data();
		e.preventDefault();
		alert("数据采集开始，Topic=" + data.topicNanme);
		start_mqtt(data.nodeIp,data.topicNanme);
	});
	
	$('#deviceTable tbody').on( 'click', 'a.stop', function (e) {
		var data = table.row($(this).parents('tr')).data();
		e.preventDefault();
		alert("数据采集停止，Topic=" + data.topicNanme);
		stop_mqtt(data.nodeIp,data.topicNanme);
	});
	
})

function checkForm() {
	if ($('#deviceEncode').val().length < 1) {
		$('#modalMsgConfirm').html('请输入设备编码！');
		$('#modalConfirm').modal().one('click', '#modal-btn', function() {
			return;
		});
		return false;
	}
	if ($('#deviceName').val().length < 1) {
		$('#modalMsgConfirm').html('请输入设备名称！');
		$('#modalConfirm').modal().one('click', '#modal-btn', function() {
			return;
		});
		return false;
	}
	if ($('#topicNanme').val().length < 1) {
		$('#modalMsgConfirm').html('请输入Topic名称！');
		$('#modalConfirm').modal().one('click', '#modal-btn', function() {
			return;
		});
		return false;
	}
	return true;
}


function clearForm() {
	$('#id').val("");
	$('#deviceEncode').val("");
	$('#deviceName').val("");
	$('#topicNanme').val("");
	$('#deviceStatusCode').val("0");
	$('#nodeIp').val("");
	$('#description').val("");

	$('#id').attr("readonly",false);
	$('#deviceEncode').attr("readonly",false);
	$('#deviceName').attr("readonly",false);
	$('#topicNanme').attr("readonly",false);
	$("#submit_btn").attr("disabled", false);
}

function createDevice() {
	$("#submit01").attr("disabled", true);
	
	var deviceEncode = $('#deviceEncode').val();
	var deviceName = $('#deviceName').val();
	var topicNanme = $('#topicNanme').val();
	var deviceStatusCode = $('#deviceStatusCode').val();
	var nodeIp = $('#nodeIp').val();
	var description = $('#description').val();

	var params = {};
	params["deviceEncode"] = deviceEncode;
	params["deviceName"] = deviceName;
	params["topicNanme"] = topicNanme;
	params["deviceStatusCode"] = deviceStatusCode;
	params["nodeIp"] = nodeIp;
	params["description"] = description;
	
	$.ajax({
		url : "/iiot/device/create",
		type : "POST",
		data : JSON.stringify(params),
		async: false,
		datatype : "json",
		contentType : "application/json",
		success : function(response) {
			if (response.status > -1) {
				alertSuccess("SUCCESS", response.message);
			} else {
				alertError("ERROR", response.message);
			}
		}
	});
	
}

function modifyDevice() {
	$("#submit_btn").attr("disabled", true);
	
	var id = $('#id').val();
	var deviceEncode = $('#deviceEncode').val();
	var deviceName = $('#deviceName').val();
	var topicNanme = $('#topicNanme').val();
	var deviceStatusCode = $('#deviceStatusCode').val();
	var nodeIp = $('#nodeIp').val();
	var description = $('#description').val();

	var params = {};
	params["id"] = id;
	params["deviceEncode"] = deviceEncode;
	params["deviceName"] = deviceName;
	params["topicNanme"] = topicNanme;
	params["deviceStatusCode"] = deviceStatusCode;
	params["nodeIp"] = nodeIp;
	params["description"] = description;
	
	$.ajax({
		url : "/iiot/device/modify",
		type : "POST",
		async: false,
		data : JSON.stringify(params),
		datatype : "json",
		contentType : "application/json",
		success : function(response) {
			if (response.status > -1) {
				alertSuccess("SUCCESS", response.message);
			} else {
				alertError("ERROR", response.message);
			}
		}
	});
	
}

function loadNodeSelect() {
	$("#nodeIp").empty();
	$.ajax({
		url : "/nodeMonitor/getNodesList",
		async: false,
		type : "GET",
		success : function(data) {
			var list = data.result;
			$("#nodeIp").append("<option value=''></option>");
			for (i = 0; i < list.length; i++) {
				$("#nodeIp").append("<option value='" + list[i].ip + "'>"+ list[i].hostName + "</option>");
			}
		}
	});
}

function loadEditForm(data) {
	var params = {};
	params["id"] = data.id;
	$.ajax({
		url : "/iiot/device/queryDevice",
		type : "POST",
		data : params,
		datatype : "json",
		success : function(response) {
			$('#id').attr("readonly", true);
			$('#id').val(response.data.id);
			
			$('#deviceEncode').attr("readonly", true);
			$('#deviceEncode').val(response.data.deviceEncode);
			
			$('#deviceName').attr("readonly", true);
			$('#deviceName').val(response.data.deviceName);
			
			$('#topicNanme').attr("readonly", true);
			$('#topicNanme').val(response.data.topicNanme);
			
			$('#deviceStatusCode').val(response.data.deviceStatusCode);
			$('#nodeIp').val(response.data.nodeIp);
			$('#description').val(response.data.description);
		}
	});
}

function removeDevice(data) {
	var params = {};
	params["id"] = data.id;
	$.ajax({
		url : "/iiot/device/remove",
		type : "POST",
		async: false,
		data : params,
		datatype : "json",
		success : function(response) {
			if (response.status > -1) {
				alertSuccess("SUCCESS", response.message);
			} else {
				alertError("ERROR", response.message);
			}
		}
	});
}

function queryAllDevice() {
	
	table = $('#deviceTable').DataTable({
		autoWidth : false,
		responsive : true,
		ajax : {
			url : "/iiot/device/queryAllDevice",
			error : function(XMLHttpRequest, textStatus,
					errorThrown) {
				if (JSON.parse(XMLHttpRequest.status) === 403) {
					window.location.replace('login.html');
				}
			}
		},
		"bSort" : false,
		"columns" : [
				{
					targets : 0,
					title : "编码",
					data : "deviceEncode"
				},
				{
					targets : 1,
					title : "名称",
					"data" : "deviceName"
				},
				{
					targets : 2,
					title : "Topic",
					"data" : "topicNanme"
				},
				{
					targets : 3,
					title : "节点IP",
					"data" : "nodeIp"
				},
				{
					targets : 4,
					title : "创建时间",
					"data" : "createTime",
					render : function(createTime) {
                        return new Date(createTime).format("yyyy-MM-dd HH:mm:ss");
                    }

				},
				{
					targets : 5,
					title : "状态",
					"data" : "deviceStatusCode",
					render : function(deviceStatusCode) {
						if (deviceStatusCode == 0) {
							return "<span class='btn-block bg-red btn-sm label-status'>Stopped</span>";
						} else if (deviceStatusCode == 1) {
							return "<span class='btn-block bg-green btn-sm label-status'>Running</span>";
						} else {
							return "<span class='btn-block bg-yellow btn-sm label-status'>Unknown</span>";
						}

					}
				},
				{
					targets : 8,
					title : "数据采集",
					"defaultContent" : "<a class='btn btn-app start' style='height:30px;min-width:40px;padding:5px 5px;margin:0 0 0px 0px;'><i class='fa fa-play'></i></a>" +
							"&nbsp;<a class='btn btn-app stop' style='height:30px;min-width:40px;padding:5px 5px;margin:0 0 0px 0px;'><i class='fa fa-pause'></i></a>"
				},
				{
					targets : 6,
					title : "描述",
					"data" : "description"
				},
				{
					targets : 7,
					title : "操作",
					"defaultContent" : "<button class='btn btn-primary btn-sm set'>设 置</button>&nbsp;<button class='btn btn-info btn-sm test'>测 试</button>&nbsp;<button class='btn btn-danger btn-sm delete' >删 除</button>"
				}
		]
	});
	
}





