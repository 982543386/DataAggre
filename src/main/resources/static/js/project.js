var ku8Regex = /^[a-zA-Z]{1,}[a-zA-Z0-9\-]*$/;
var ku8App = "application";
var ku8Srv = "publicservice";
var ku8Job = "job";

function checkInput(form) {
	var scroll = true;
    var ret = true;
    $(form + ' input.form-control').each(function() {

		if (!$(this).hasClass('allow-uppercase'))
            this.value = this.value.toLowerCase();

		ret = setFormTips(this);

		if (!ret && scroll) {
            $('html, body').animate({
                scrollTop: $(this).offset().top
			}, 1000);
			scroll = false;
        }
    });
    return scroll;
}

function setFormTips(e) {
	var $target = $(e);
	var hasOtherValidation = false;
	$target.parent().find('.formtips').remove().end();
	$target.closest('.form-group').removeClass('has-error has-success has-warning');

	if($target.hasClass('validation'))
	{
		if($target.val() === '')
		{
			$target.closest('.form-group').addClass('has-error');
			$target.parent().append('<span class="formtips text-red">不能为空!</span>');
			return false;
		}
		else if (!$target.val().match(ku8Regex) && !$target.hasClass('allow-special') && $target.attr('type') !== 'number')
		{
			$target.closest('.form-group').addClass('has-error');
			$target.parent().append('<span class="formtips text-red">First character must be a-z, only alphanumeric allowed.</span>');
			return false;
		}
	}

	if($target.hasClass('sql-sname-validation'))
	{
		var ret = true;
		hasOtherValidation = true;

		$('input.sql-sname-validation').not($target).each(function() {
			if($(this).val() === $target.val() && $(this).closest('.form-group').hasClass('has-success'))
			{
				ret = false;
				return;
			}
		});

		if(ret)
		{
			$.when(existServiceName($target.val())).then(function(res) {
				if (res.data.length === 0) {
					$target.closest('.form-group').addClass('has-success');
					return;
				}
				else
				{
					$target.closest('.form-group').removeClass('has-success').addClass('has-warning');
					$target.parent().append('<span class="formtips text-yellow">Service exists in namespace: ' + res.data + '</span>');
					return;
				}
			});
		}
	}

	if($target.hasClass('sql-jname-validation'))
	{
		var ret = true;
		hasOtherValidation = true;

		$('input.sql-jname-validation').not($target).each(function() {
			if($(this).val() === $target.val() && $(this).closest('.form-group').hasClass('has-success'))
			{
				ret = false;
				return;
			}
		});

		if(ret)
		{
			$.when(existJobName($target.val())).then(function(res) {
				if (res.data.length === 0) {
					$target.closest('.form-group').addClass('has-success');
					return;
				}
				else
				{
					$target.closest('.form-group').removeClass('has-success').addClass('has-warning');
					$target.parent().append('<span class="formtips text-yellow">Job exists in namespace: ' + res.data + '</span>');
					return;
				}
			});
		}
	}

	if($target.hasClass('port-validation'))
	{
		hasOtherValidation = true;

		if (($target.val() < 1 || $target.val() > 65536) && $target.val() !== '')
		{
			$target.closest('.form-group').addClass('has-error');
			$target.parent().append('<span class="formtips text-red">The allowed Port range is 1-65536</span>');
			return false;
		}
		else if ($target.val() !== '')
		{
			hasOtherValidation = false;
		}
	}

	if($target.hasClass('sql-port-validation') && $target.val() !== '')
	{
		var ret = true;
		hasOtherValidation = true;

		$('input.sql-port-validation').not($target).each(function() {
			if($(this).val() === $target.val() && $(this).closest('.form-group').hasClass('has-success'))
			{
				ret = false;
				return;
			}
		});

		if(ret)
		{
			$.when(existNodePort($target.val())).then(function(res) {
				if (res.data.length === 0) {
					$target.closest('.form-group').addClass('has-success');
					return;
				}
				else{
					$target.closest('.form-group').removeClass('has-success').addClass('has-warning');
					$target.parent().append('<span class="formtips text-yellow">This Node Port exists in namespace: ' + res.data + '.</span>');
					return ;
				}
			});
		}

		if(!ret)
		{
			console.info("This Node Port is being used by another container.");
			$target.closest('.form-group').removeClass('has-success').addClass('has-error');
			$target.parent().append('<span class="formtips text-red">This Node Port is being used by another container.</span>');
			return ;
		}
	}

	if($target.hasClass('number-validation'))
	{
		hasOtherValidation = true;

		if ($target.val() < 0)
		{
			$target.closest('.form-group').addClass('has-error');
			$target.parent().append('<span class="formtips text-red">Negative numbers not allowed.</span>');
			return false;
		}
		else
		{
			$target.closest('.form-group').addClass('has-success');
		}
	}

	if($target.hasClass('partial-validation'))
	{
		hasOtherValidation = true;

		if (!$target.val().match(ku8Regex) && !$target.hasClass('allow-special') && $target.val() !== '')
		{
			$target.closest('.form-group').addClass('has-error');
			$target.parent().append('<span class="formtips text-red">First character must be a-z, only alphanumeric allowed.</span>');
			return false;
		}
		else if ($target.val() !== '')
		{
			$target.closest('.form-group').addClass('has-success');
		}
	}

	if($target.hasClass('no-validation'))
	{
		hasOtherValidation = true;
		if($target.val() !== '')
		{
			$target.closest('.form-group').addClass('has-success');
		}
	}

	if(!hasOtherValidation)
		$target.closest('.form-group').addClass('has-success');

	return true;
}

$('#ku8form').on('focusout', 'input.form-control',function (e) {
	setFormTips($(e.target));
});

function existServiceName(v) {
	 return $.ajax({
		url: "/publicservice/hasServiceName",
		type: "POST",
		async: false,
		dataType: "json",
		data: {
			serviceName:v
		}
	});
}

function existJobName(v) {
	 return $.ajax({
		url: "/job/hasJobName",
		type: "POST",
		async: false,
		dataType: "json",
		data: {
			jobName:v
		}
	});
}

function existNodePort(v) {
	 return $.ajax({
		url: "/publicservice/hasNodePort",
		type: "POST",
		async: false,
		dataType: "json",
		data: {
			nodePort:v
		}
	});
}

function addApplication() {
    if (checkInput('#ku8form')) {
        var _name = $('#name').val();
        var _version = $('#version').val();
        var _note = $('#note').val();
        var _json = {};
        _json.projectName = _name;
        _json.version = _version;
        _json.notes = _note;
        _json.services = createServiceJson();

        $.ajax({
            url: "/application/addApplication",
            type: "POST",
            dataType: "json",
            data: {
                name: _name,
                version: _version,
                note: _note,
                jsonStr: JSON.stringify(_json)
            },
            success: function(response) {
                if (response.status > -1) {
					goback(response.message, "App <strong>" + _name + "</strong> added successfully.", 'application_main.html');
                } else {
					alertError("ERROR", response.message);
                }
            }
        });
    }
}

function addServiceAndRC() {
    if (checkInput('#ku8form')) {
        var _name = $('#servName').val();
        var _json = createServiceJson();

        $.ajax({
            url: "/publicservice/addServiceAndRC",
            type: "POST",
            dataType: "json",
            data: {
                jsonStr: JSON.stringify(_json[0])
            },
            success: function(response) {
                if (response.status > -1) {
					goback(response.message, "Service <strong>" + _name + "</strong> added successfully.", 'service_main.html');
                } else {
					alertError("ERROR", response.message);
                }
            }
        });
    }
}

function addJobList() {
    if (checkInput('#ku8form')) {
        var _name = $('#name').val();
        var _version = $('#version').val();
        var _note = $('#note').val();
        var _json = {};
		_json.jobName = _name;
        _json.version = _version;
        _json.notes = _note;
        _json.jobs = createJobJson();

        $.ajax({
            url: "/job/addJob",
            type: "POST",
            dataType: "json",
            data: {
                name: _name,
                version: _version,
                note: _note,
                jsonStr: JSON.stringify(_json)
            },
            success: function(response) {
                if (response.status > -1) {
					goback(response.message, "Job <strong>" + _name + "</strong> added successfully.", 'job_main.html');
                } else {
					alertError("ERROR", response.message);
                }
            }
        });
    }
}

function hide_show(obj) {
    $($(obj).data('target')).toggle(500);
}

function addService(obj) {
    var $uid = new Date().getUTCMilliseconds();
    var $service = $(obj).closest(".form-group[name='service']");
    var $new_service = $service.clone();
    $new_service.find("button[name='expand']").attr("data-target", "#service_panel_" + $uid);
    $new_service.find(".panel-body").attr("id", "service_panel_" + $uid).html('');
    var _div = $('<div>');
    _div.load('service_template.html?h=' + new Date().getTime(), function() {
        $(this).find("#servVersion").closest('.form-group').remove();
        $(this).find("#servDescribe").closest('.form-group').remove();
        getDockerImage('', $(this).find('#conImage'));
		getConfigMaps($(this));
        $new_service.find(".panel-body").append($(this).children()).show(200);
    });
    $service.after($new_service);
}

function delService(obj) {
    if ($(".form-group[name='service']").length > 1) {
        $(obj).closest(".form-group[name='service']").slideUp("normal", function() {
            $(this).remove();
        });
    }
}

function cloneService(json, servicePanel, ku8type) {
    var _div = $('<div>');
    _div.load('service_template.html?h=' + new Date().getTime(), function() {
        $(this).find("#servDescribe").val(json.describe);
        $(this).find("#servVersion").val(json.version);
        $(this).find("#servReplica").val(json.replica);
        $(this).find("#servVersion").val(json.version);
        $(this).find("#servProxyMode option[value=" + json.proxyMode + "]").prop('selected', true);

        if (ku8type === ku8App) {
            $(this).find("#servVersion").closest('.form-group').remove().end();
            $(this).find("#servDescribe").closest('.form-group').remove().end();
        }

		//Loop Labels
		var index_o = 0;
		if(json.labels !== undefined && json.labels !== null)
		{
			$.each(json.labels, function(lkey, lval) {
				if(lkey === 'appname' || lkey === 'apptype')
					return true;
				var labelDiv = _div.find("div.form-group[name='label']:last");

				if (index_o > 0) {
					var $temp = labelDiv.clone();
					labelDiv.after($temp);
					labelDiv = $temp;
				}

				labelDiv.find("#labelKey").val(lkey);
				labelDiv.find("#labelValue").val(lval);
				index_o++;
			});
		}

        //Loop Volumes
        var containerDiv = _div.find("div.panel.panel-info[name='container']:last");
		if(json.volumes !== undefined && json.volumes !== null)
		{
			$.each(json.volumes, function(index_i, v) {
				//Clone Volume
				var volumeDiv = _div.find("div.form-group[name='volume']:last");

				//Clone VM in Container-0
				containerDiv.find("div.panel.volMountPanel").show();
				var volMountDiv = containerDiv.find("div.form-group[name='volMount']:last");
				if (index_i > 0) {
					var $temp;
					$temp = volumeDiv.clone();
					volumeDiv.after($temp);
					volumeDiv = $temp;

					$temp = volMountDiv.clone();
					volMountDiv.after($temp);
					volMountDiv = $temp;
				}
				volumeDiv.attr('data-vmtarget', 'vol_vm' + v.name);
				volumeDiv.find('#volName').val(v.name);
				volumeDiv.find('#volPath').val(v.path);

				volMountDiv.removeClass().addClass('form-group no-margin-bottom vol_vm' + v.name);
				volMountDiv.find('#volmountName').html(v.name);
			});
		}

		//Loop Config Maps
		getConfigMaps(_div, json.confmaps);

        //Loop containers
        $.each(json.containers, function(index_j, con) {
            if (index_j > 0) {
                var $temp = containerDiv.clone(true).off();
                containerDiv.after($temp);
                containerDiv = $temp;
				//Delete extra Ports
                containerDiv.find("div.form-group[name='port']:not(:first)").remove();
                //Delete extra EnVars
                containerDiv.find("div.form-group[name='envVariable']:not(:first)").remove();
                //Delete extra VM Path Vals
                containerDiv.find("div.form-group[name='volMount'] input").val('');
            }
            containerDiv.find("#conPort").val(con.containerPort);
            containerDiv.find("#conNodePort").val(con.nodePort);
            containerDiv.find("#conCPU").val(con.quotas_cpu);
            containerDiv.find("#conMemory").val(con.quotas_memory);
            containerDiv.find("#conImage").data('imgId', con.imageId);

            //Loop Volume Mount
            $.each(con.volumes, function(index_k, vm) {
                var volMountDiv = containerDiv.find("div.vol_vm" + vm.name);
                volMountDiv.find('#volmountPath').val(vm.path);
            });

			//Loop Ports
			if(con.containerPorts !== undefined && con.containerPorts !== null)
			{
				$.each(con.containerPorts, function(index_m, cp) {
					var portDiv = containerDiv.find("div.form-group[name='port']:last");

					if (index_m > 0) {
						var $temp = portDiv.clone();
						portDiv.after($temp);
						portDiv = $temp;
					}

					portDiv.attr('id', cp.name);
					portDiv.find("#conPort").val(cp.containerPort);
				});
			}

            //Loop EnvVars
			if(con.envVariables !== undefined && con.envVariables !== null)
			{
				$.each(con.envVariables, function(index_l, env) {
					var enVarsDiv = containerDiv.find("div.form-group[name='envVariable']:last");

					if (index_l > 0) {
						var $temp = enVarsDiv.clone();
						enVarsDiv.after($temp);
						enVarsDiv = $temp;
					}

					enVarsDiv.find("#envName").val(env.name);
					enVarsDiv.find("#envValue").val(env.value);
				});
			}

            //Add Liveness Probe
            if (con.livenessProbe !== undefined && con.livenessProbe !== null) {
                var newTemplate = _div.find('#' + con.livenessProbe.type + 'Template').clone().removeAttr('id');
                newTemplate.find('input').removeClass('no-validation').addClass('validation');
                newTemplate.show();

                containerDiv.find("select.liveProbeType option[value='" + con.livenessProbe.type + "']").prop('selected', true);
                containerDiv.find(".liveProbeDiv").html(newTemplate);
                switch (con.livenessProbe.type) {
                    case 'httpGet':
                        newTemplate.find('#liveProbePath').val(con.livenessProbe.path);
                        newTemplate.find('#liveProbePort').val(con.livenessProbe.port);
                        break;
                    case 'tcpSocket':
                        newTemplate.find('#liveProbePort').val(con.livenessProbe.port);
                        break;
                    case 'exec':
                        newTemplate.find('#liveProbeCommand').val(con.livenessProbe.command);
                        break;
                    default:
                        break;
                }
                newTemplate.find('#liveProbeDelay').val(con.livenessProbe.initialDelaySeconds);
                newTemplate.find('#liveProbeTimeout').val(con.livenessProbe.timeoutSeconds);
            } else {
                containerDiv.find("select.liveProbeType option[value='']").prop('selected', true);
                containerDiv.find(".liveProbeDiv").html('');
            }
        });

		//Set Service Ports
		if(json.servicePorts !== undefined && json.servicePorts !== null)
		{
			$.each(json.servicePorts, function(n, sp) {
				var portDiv = _div.find('#' + sp.name);
				portDiv.find("#nodePort").val(sp.nodePort);
			});
		}

        servicePanel.append($(this).children());

        //Select Images
        servicePanel.find("div[name='container']").each(function(i) {
            getDockerImage('', $(this).find("#conImage"));
            return;
        });
    });
}

function addJob(obj) {
    var $uid = new Date().getUTCMilliseconds();
    var $job = $(obj).closest(".form-group[name='job']");
    var $new_job = $job.clone();
    $new_job.find("button[name='expand']").attr("data-target", "#job_panel_" + $uid);
    $new_job.find(".panel-body").attr("id", "job_panel_" + $uid).html('');
    var _div = $('<div>');
    _div.load('job_template.html?h=' + new Date().getTime(), function() {
        getDockerImage('', $(this).find('#conImage'));
        $new_job.find(".panel-body").append($(this).children()).show(200);
    });
    $job.after($new_job);
}

function delJob(obj) {
    if ($(".form-group[name='job']").length > 1) {
        $(obj).closest(".form-group[name='job']").slideUp("normal", function() {
            $(this).remove();
        });
    }
}

function cloneJob(json, jobPanel) {
    var _div = $('<div>');
    _div.load('job_template.html?h=' + new Date().getTime(), function() {
        $(this).find("#jobName").val(json.name);
		$(this).find("#jobParallelism").val(json.parallelism);

		//Loop Labels
		var index_o = 0;
		if(json.labels !== undefined && json.labels !== null)
		{
			$.each(json.labels, function(lkey, lval) {
				if(lkey === 'jobgroup')
					return true;
				var labelDiv = _div.find("div.form-group[name='label']:last");

				if (index_o > 0) {
					var $temp = labelDiv.clone();
					labelDiv.after($temp);
					labelDiv = $temp;
				}

				labelDiv.find("#labelKey").val(lkey);
				labelDiv.find("#labelValue").val(lval);
				index_o++;
			});
		}

		var containerDiv = _div.find("div.panel.panel-info[name='container']:last");

        //Loop containers
        $.each(json.containers, function(index_j, con) {
            if (index_j > 0) {
                var $temp = containerDiv.clone(true).off();
                containerDiv.after($temp);
                containerDiv = $temp;
				 //Delete extra Commands
                containerDiv.find("div.form-group[name='command']:not(:first)").remove();
            }

			var _imgData = {};
			_imgData.imageName = con.selectedImageName;
			_imgData.version = con.selectedImageVersion;

            containerDiv.find("#conImage").data('imgData', _imgData);

            //Loop Commands
			if(con.command !== undefined && con.command !== null)
			{
				containerDiv.find("#cmdText").val(con.command);
			}
        });

        jobPanel.append($(this).children());

        //Select Images
        jobPanel.find("div[name='container']").each(function(i) {
            getDockerImage('', $(this).find("#conImage"));
            return;
        });
    });
}

function buildLabelTable(table, key, value) {
    $(table).find('tbody')
        .append($('<tr/>')
            .append($('<td/>')
                .html(key)
            )
            .append($('<td/>')
                .html(value)
            )
        );
}

function buildVolumeTable(table, data) {
    $(table).find('tbody')
        .append($('<tr/>')
            .append($('<td/>')
                .html(data.name)
            )
            .append($('<td/>')
                .html(data.path)
            )
        );
}

function buildConfMapTable(table, data) {
    $(table).find('tbody')
        .append($('<tr/>')
            .append($('<td/>')
                .html(data.name)
            )
            .append($('<td/>')
                .html(data.path)
            )
        );
}

function buildContainerTable(table, cdata, kID, cIndex, name, ku8type) {
    var $tr = $('<tr/>').append($('<td/>').html(cdata.name))
            .append($('<td/>').html(cdata.imageName));

	if(ku8type !== ku8Job)
	{
		var btn = $('<button/>').attr({
			'class': 'btn btn-sm btn-warning con_updbtn',
			'onclick': 'return updateContainer(this);',
			'data-loading-text': "<i class='fa fa-spin fa-refresh'></i>"
		});
		btn.data('data', {
			'ku8ID': kID,
			'cIndex': cIndex,
			'name': name,
			'ku8type': ku8type
		});
		btn.text('Update');

        $tr.append($('<td/>').html(cdata.quotas_cpu))
            .append($('<td/>').html(cdata.quotas_memory))
            .append($('<td/>').html(cdata.portString))
            .append($('<td/>').html(btn));
	}

    $(table).find('tbody').append($tr);
}

function filterImages(e){
	var _conImage = $(e).closest('.imgModal').data('conImage');
    getDockerImage(e.value, _conImage);
}

$('#ku8form').on('keyup paste', 'input.vol_vm', function() {
	//Toggle VolumeMount
    var display = true;
    var volumeDiv = $(this).closest("div[name='volume']");
    volumeDiv.find('input').each(function() {
        if (this.value === '') {
            display = false;
            return;
        }
    });

    //Change Val on VolumeMount
    var servPanel = $(this).closest('div[name=servPanel]');
    var $target = servPanel.find('div.' + volumeDiv.attr('data-vmtarget'));
    $target.find('#volmountName').html(volumeDiv.find('#volName').val().toLowerCase());

    display ? $target.fadeIn(200) : $target.fadeOut(200);

    //Toggle VolumeMount Form
    $target.promise().done(function() {
        display = false;
        servPanel.find("div[name='volMount']").each(function() {
            if ($(this).css("display") !== "none") {
                display = true;
                return;
            }
        });
        display ? servPanel.find('.volMountPanel').fadeIn(500) : servPanel.find('.volMountPanel').fadeOut(500);
    });
});

function changeLiveProbeType(sel) {
    var newTemplate = $('#' + sel.value + 'Template').clone().removeAttr('id');
    $(sel).closest('div[name=liveProbeForm]').find('div.liveProbeDiv').html(newTemplate);
    newTemplate.find('input').removeClass('no-validation').addClass('validation');
    newTemplate.fadeIn(750);
}

$('.imgModal').on('hidden.bs.modal', function() {
    $(this).find('#dockerImageFilter').val('');
});

$('.imgModal').on('shown.bs.modal', function() {
    $(this).find('#dockerImageFilter').focus();
});

function getDockerImage(filter, caller) {
    $.ajax({
        type: "GET",
        data: {
            key: filter
        },
       url: '/dockerimg/search',
        dataType: 'json',
        success: function(img) {
            if (img.data === null)
			{
				alertError('ERROR', 'No Docker images found.');
				$('#ku8form :input').attr('disabled','disabled');
                return;
			}


			$(".saveBtn").removeAttr("disabled");
            $(".allImages").html('');
            var _imgId = $(caller).data('imgId');

            var row = $('<div/>').attr({
                'class': 'row'
            });
            var len = img.data.length;

            $.each(img.data, function(index_i, item) {
                var date = new Date(item.lastUpdated);
                var imgDiv = $('<div/>').attr({
                    'class': 'col-sm-4',
                    'onclick': 'return onSelectImg(this);'
                }).data('imgData', item);
                var dockerDiv = $('<div/>').attr({
                    'class': 'dockerImg'
                });
                var inner = "<div style='background:#FFF;width:100%;'>";
                var imgSuccess = $('<image/>').attr({
                    'src': '../img/success.png',
                    'class': 'check',
                    'style': 'width:15%;'
                });

                if (_imgId === undefined || filter !== '' || _imgId === -1) {
                    if (index_i === 0) {
                        dockerDiv.addClass('selected');
                        imgSuccess.addClass('selected');
                        $(caller).html(item.title).data('imgId', item.id);
                    }
                } else {
                    if (item.id === _imgId) {
                        dockerDiv.addClass('selected');
                        imgSuccess.addClass('selected');
                        $(caller).html(item.title);
                    }
                }

                inner += imgSuccess.prop('outerHTML');
                inner += "<image style='height:30px;margin-left:15%;' src='/external/" + item.imageIconUrl + "'/></div>";
                inner += "<span>" + item.imageName + "</span>";
                inner += "<span>" + (item.versionType === 1 ? "生产版, " : "开发版, ");
                inner += (item.publicImage === 1 ? "公共镜像" : "私有镜像") + "</span>";
                inner += "<span>版本：" + item.version + "</span>";
                inner += "<span>更新时间：" + date.toLocaleDateString('zh') + "</span></div>";

                row.append(imgDiv.html(dockerDiv.html(inner)));

                if (((index_i + 1) % 3 === 0 && index_i > 0) || index_i === len - 1) {
                    $(".allImages").append(row);
                    row = $('<div/>').attr({
                        'class': 'row'
                    });
                }
            });
        }
    });
}

function chooseImage(e) {
    var _conImage = $(e).parent().find('#conImage');
    var _servPanel = $(e).closest("div[name='servPanel']");
    getDockerImage('', _conImage);
    _servPanel.find('.imgModal').data('conImage', _conImage).modal('toggle');
}

function onSelectImg(e) {
    var _conImage = $(e).closest("div[name='servPanel']").find('.imgModal').data('conImage');
    var _imgData = $(e).data('imgData');
    $('.dockerImg, .check').removeClass('selected');
    $(e).find('div.dockerImg').addClass('selected').find('.check').addClass('selected');

    _conImage.html(_imgData.title).data('imgId', _imgData.id);
}

function getConfigMaps(_div, e) {
	var $confmapForm = $(_div).find("div[name='confmapForm']");
	$.ajax({
		url: "/configmaps/distinct",
		type: "GET",
		dataType: "json",
		success: function(cm) {
			if(cm.data.length > 0)
				$confmapForm.closest('.panel-info').show();

			$.each(cm.data, function(index_i, c) {
				var new_confMap = $('#confMapTemplate').clone().show().attr('id', 'cm_' + c);
				$(new_confMap).find('span').text(c);
				$(new_confMap).find('input:checkbox').val(c);
				$confmapForm.append(new_confMap);
			});
			$confmapForm.iCheck({
				checkboxClass: 'icheckbox_square-blue',
				increaseArea: '20%'
			}).on('ifChecked', function(event){
				$(this).closest(".form-group").find('#confmapPath, .formtips').show().addClass("validation");
			}).on('ifUnchecked', function(event){
				$(this).closest(".form-group").find('#confmapPath, .formtips').hide().removeClass("validation");
			});

			if(e!== undefined && e !== null)
			{
				$.each(e, function(index_i, confmap) {
					var confMap = $confmapForm.find('#cm_' + confmap.name);
					$(confMap).iCheck('check');
					$(confMap).find('#confmapPath').val(confmap.path);
				});
			}
		}
	});
}

function addContainer(obj) {
    var $container = $(obj).closest("div[name='container']");
    var $new_container = $container.clone(true).off();
	$new_container.find("#conCPU, #conMemory").removeData().tooltip({placement : 'right'});
    $new_container.find('.liveProbeDiv').html('');
    $container.after($new_container);
}

function delContainer(obj) {
    if ($(obj).closest("div[name='servPanel']").find("div[name='container']").length > 1) {
        $(obj).closest("div[name='container']").slideUp("normal", function() {
            $(this).remove();
        });
    }
}

function addPort(obj) {
    var $port = $(obj).closest("div[name='port']");
    $port.after($port.clone());
}

function delPort(obj) {
    var $portForm = $(obj).closest("div[name='portForm']");

    if ($($portForm).find("div[name='port']").length > 1) {
        $(obj).closest("div[name='port']").slideUp("normal", function() {
            $(this).remove();
        });
    }
}

function addLabel(obj) {
    var $label = $(obj).closest("div[name='label']");
    $label.after($label.clone());
}

function delLabel(obj) {
    var $labelForm = $(obj).closest("div[name='labelForm']");

    if ($($labelForm).find("div[name='label']").length > 1) {
        $(obj).closest("div[name='label']").slideUp("normal", function() {
            $(this).remove();
        });
    }
}

function addEnvVariable(obj) {
    var $env = $(obj).closest("div[name='envVariable']");
    $env.after($env.clone());
}

function delEnvVariable(obj) {
    var $envForm = $(obj).closest("div[name='envVariableForm']");

    if ($($envForm).find("div[name='envVariable']").length > 1) {
        $(obj).closest("div[name='envVariable']").slideUp("normal", function() {
            $(this).remove();
        });
    }
}

function addVolume(obj) {
    var $vol = $(obj).closest("div[name='volume']");
    var $uid = new Date().getUTCMilliseconds();
    var $new_vol = $vol.clone(true);
    $vol.after($new_vol.attr('data-vmtarget', 'vol_vm' + $uid));

    var content = $(obj).closest('div[name=servPanel]').find('div.volMountDiv');
    var newTemplate = $('#volumeMountTemplate').clone().removeAttr('id').addClass('vol_vm' + $uid);
    newTemplate.find('#volmountName').html($new_vol.find('#volName').val());
    content.append(newTemplate.fadeIn(500));
}

function delVolume(obj) {
    var $volForm = $(obj).closest("div[name='volumeForm']");

    if ($($volForm).find("div[name='volume']").length > 1) {
        var volumeDiv = $(obj).closest("div[name='volume']");
        $(obj).closest("div[name='servPanel']").find('div.' + volumeDiv.attr('data-vmtarget')).remove();
        volumeDiv.slideUp("normal", function() {
            $(this).remove();
        });
    }
}

function startService(e)
{
	var data = $(e).data('sdata');
	var _message = 'Service <strong>' + data.name + '</strong> started';

	$('#replicaModalMsg').html('Do you want start application <strong>' + data.name + '</strong>?');
	$('#replicaModal').modal().one('click', '#modal-confirm', function() {
		$(e.target).button('loading');
		confirmReplica(data, 1, e, _message);
	});
}

function stopService(e)
{
	var data = $(e).data('sdata');
	var _message = 'Service <strong>' + data.name + '</strong> stopped';

	$('#replicaModalMsg').html('Do you want stop application <strong>' + data.name + '</strong>?');
	$('#replicaModal').modal().one('click', '#modal-confirm', function() {
		$(e.target).button('loading');
		confirmReplica(data, 0, e, _message);
	});
}

function getClusters(ku8type)
{
	$.ajax({
		url: "/" + ku8type + "/getClusters",
		type: "GET",
		dataType: "json",
		data: {ku8ID:_id},
		success: function(c){
			if(c.data === null)
			{
				$('#c_error_server').show();
				$('#cluster-div').hide();
				$('#service-div').hide();
				return;
			}
			if(c.data.length === 0)
			{
				$('#c_error_app').show();
				$('#cluster-div').hide();
				$('#service-div').hide();
				return;
			}

			$.each(c.data, function(i, _c) {
				var tab = $('<li>').html("<a style='font-weight: 600;' onclick='return set_cIndex(this);' href='#cluster_" + i + "' data-toggle='pill' data-tabindex='" + i + "'>" + _c.clusterName + "</a>").data('clusterID', _c.clusterID);
				if(i === c_tabIndex)
				{
					tab.addClass("active");
					reloadServices(_c.clusterID);
				}

				$('#cluster-ul').append(tab);
			});
		},
		error:function()
		{
			$('#c_error_server').show();
			$('#cluster-div').hide();
			$('#service-div').hide();
			return;
		}
	});
}

function stopPod(e, ku8type) {
	var table = $(e).closest('table');
	var data = table.DataTable().row($(e).parents('tr')).data();

	$.ajax({
		url: "/" + ku8type + "/deletePod",
		type: "POST",
		dataType: "json",
		data: {
			ku8ID:_id,
			name:data.name
		},
		success: function(response){
			var _div = $('<div>');
			if(response.status > -1)
			{
				alertSuccess(response.message, "Pod <strong>" + data.name + "</strong> stopped successfully.");
			}
			else
			{
				alertError("ERROR", response.message);
			}
			table.DataTable().ajax.reload();
		}
	});
}

function showPodStatus(e) {
	$('#statusText').html($(e).data('btndata').statusDetails);
	$('#statusModal').modal('toggle');
}

function getPartitions(data, caller) {
    $.ajax({
        url: "/application/getNamespacesByUser",
        type: "GET",
        dataType: "json",
        success: function(d) {
            if (d.data === null) {
				alertError("ERROR", 'Error while retrieving partitions<br/>Please verify User Groups');
                return;
            }
            if (d.data.length === 0) {
				alertError("ERROR", 'No partitions found<br/>Please verify User Groups');
                return;
            }

            var partContent = $('#partModalContent').html('');
            $.each(d.data, function(i, c) {
                var clusterDiv = $('<li>').attr({
                    'class': 'item'
                }).html('<strong>' + c.clusterName + '</strong>');
                $.each(c.namespace, function(j, n) {
                    var namespaceDiv = $('<div>').attr({
                        'class': 'radio'
                    }).html('<label><input type="radio" name="clusterRadio' + i + '" value="' + n +'"> ' + n + '</label>');
                    clusterDiv.append(namespaceDiv);
                });
                partContent.append(clusterDiv);
            });

            set_iCheck();
			set_iCheck_Job();
            $('#partModal').modal('toggle').removeData('appData').data('appData', {
                id: data.id,
                caller: caller
            });
            $('#partModalDeploy').attr('disabled', 'disabled');
        }
    }).always(function() {
        caller.button("reset");
        return;
    }).fail(function() {
		alertError("ERROR", 'Error while retrieving partitions<br/>Please verify User Groups');
		return;
	});
}

function set_iCheck()
{
	$('#partModalContent input').iCheck({
		radioClass: 'iradio_square-red',
		increaseArea: '20%'
	}).on('ifClicked', function(event){
		if(this.checked)
		{
			$(this).iCheck('uncheck');
			if(!$('#partModalContent input:checked').not($(this)).val())
			{
				$('#partModalDeploy').attr('disabled', 'disabled');
			}
		}
		else
		{
			$('#partModalDeploy').removeAttr('disabled');
		}
	});
}

function set_iCheck_Job()
{
	$('#partModalJobType input').iCheck({
		radioClass: 'iradio_square-red',
		increaseArea: '20%'
	}).on('ifClicked', function(event){
		if(this.id === 'jobmulti')
		{
			$('#partModalJob').fadeIn('normal');
		}
		else
		{
			$('#partModalJob').fadeOut('normal');
		}
	});
}

function goback(title, message, url) {
    $('section.content').load(url, function() {
		alertSuccess(title, message);
    });
}

function updateLabels(e, ku8type)
{
	var data = $(e).data('sdata');
	var content = $('#lblModalContent').html('');

	$.each(data.labels, function(lbKey, lbValue) {
		var template = $('#lblModalTemplate').clone().removeAttr('id');
		template.find('#lbkey').val(lbKey);
		template.find('#lbval').val(lbValue);
		content.append(template.show());
	});

	//For empty labels
	template = $('#lblModalTemplate').clone().removeAttr('id');
	content.append(template.show());

	$('#lblModal').modal().one('click', '#modal-confirm', function() {
		$(e.target).button('loading');

		var lbls = {};
		content.find("div[name='label']").each(function() {
			var lkey = $(this).find("input[id='lbkey']").val();
			var lval = $(this).find("input[id='lbval']").val();
			if (lkey !== "" && lval !== "") {
				lbls[lkey] = lval;
			}
		});

		$.ajax({
			url: "/" + ku8type + "/updateLabels",
			type: "POST",
			dataType: "json",
			data: {
				ku8ID:_id,
				name:data.name,
				jsonStr:JSON.stringify(lbls)
			},
			success: function(response){
				if(response.status > -1)
				{
					alertSuccess('SUCCESS', response.message);
				}
				else
				{
					alertError("ERROR", response.message);
				}
				reloadServices();
				$(e.target).button('reset');
			}
		});
	});
}

function updateContainer(e) {
    var data = $(e).data('data');
    $('section.content').load('update_container.html?h=' + new Date().getTime());
    sessionStorage.setItem("ku8ID", data.ku8ID);
    sessionStorage.setItem("clusterID", data.clusterID);
    sessionStorage.setItem("cIndex", data.cIndex);
    sessionStorage.setItem("name", data.name);
    sessionStorage.setItem("ku8type", data.ku8type);
}

function saveContainer() {
    var $con = {};
    var ens = [];

	$con.imageId = $("#conImage").data('imgId');

    $("div[name='envVariable']").each(function() {
        var values = $(this).find('input');
        var $en = {};
        $en.name = values.first().val();
        $en.value = values.last().val();
        if ($en.name !== "" && $en.value !== "") {
            ens.push($en);
        }
    });
    $con.envVariables = ens;

    var vms = [];
    $("div[name='volMount']").each(function() {
        var $vm = {};
        $vm.name = $(this).find('label').text();
        $vm.path = $(this).find('input').val();
        if ($vm.name !== "" && $vm.path !== "") {
            vms.push($vm);
        }
    });
    $con.volumes = vms;

    var lpType = $(".liveProbeType").val();
    if (lpType !== '') {
        var $lp = {};
        $lp.type = lpType;
        var $lpDiv = $(".liveProbeDiv");
        switch (lpType) {
            case 'httpGet':
                $lp.path = $lpDiv.find('#liveProbePath').val();
                $lp.port = $lpDiv.find('#liveProbePort').val();
                break;
            case 'tcpSocket':
                $lp.port = $lpDiv.find('#liveProbePort').val();
                break;
            case 'exec':
                $lp.command = $lpDiv.find('#liveProbeCommand').val();
                break;
            default:
                break;
        }
        $lp.initialDelaySeconds = $lpDiv.find('#liveProbeDelay').val();
        $lp.timeoutSeconds = $lpDiv.find('#liveProbeTimeout').val();
        $con.livenessProbe = $lp;
    }

    $('#confirmModal').modal().one('click', '#modal-confirm', function() {
		//Disable Input
		var $item1 = $(this).closest('.row').find('#ku8form :input').attr('disabled', true);
		var $item2 = $(this).closest('.row').find('.saveBtn').button('loading');

		$.ajax({
            url: "/" + _ku8type + "/updateContainer",
            type: "POST",
            dataType: "json",
            data: {
                ku8ID: _id,
                serviceName: _name,
                containerIndex: _cIndex,
                jsonStr: JSON.stringify($con)
            },
            success: function(response) {
                if (response.status > -1) {
					if (_ku8type == ku8App)
						goback(response.message, "Container updated successfully.", 'application_main.html');
					if (_ku8type == ku8Srv)
						goback(response.message, "Container updated successfully.", 'service_main.html');
					if (_ku8type == ku8Job)
						goback(response.message, "Container updated successfully.", 'job_main.html');
                } else {
                    alertError("ERROR", response.message);
					$item1.removeAttr('disabled');
					$item2.button('reset');
                }
            }
        });
    });
}

function createServiceJson() {
    var $json = [];
    var $serviceDivPanel = $("div[name='servPanel']");
    $.each($serviceDivPanel, function(index_i, serviceDiv) {
        var $ser = {};
        $ser.name = $(serviceDiv).find("input[id='servName']").val();
        $ser.describe = $(serviceDiv).find("input[id='servDescribe']").val();
        $ser.replica = $(serviceDiv).find("input[id='servReplica']").val();
        $ser.version = $(serviceDiv).find("input[id='servVersion']").val();
        $ser.proxyMode = $(serviceDiv).find("select[id='servProxyMode']").val();
		var $labelDivPanel = $(serviceDiv).find("div[name='label']");
        var lbls = {};
        $.each($labelDivPanel, function(index_j, label) {
            var lkey = $(label).find("input[id='labelKey']").val();
			var lval = $(label).find("input[id='labelValue']").val();
            if (lkey !== "" && lval !== "") {
				lbls[lkey] = lval;
            }
        });
        //2019-08-08 注释label，暂时不用
        $ser.labels = lbls;
        var $volumeDivPanel = $(serviceDiv).find("div[name='volume']");
        var vols = [];
        $.each($volumeDivPanel, function(index_k, volume) {
            var $vol = {};
            $vol.name = $(volume).find("input[id='volName']").val();
            $vol.path = $(volume).find("input[id='volPath']").val();
            if ($vol.name !== "" && $vol.path !== "") {
                vols.push($vol);
            }
        });
        $ser.volumes = vols;
		var $confmapDivPanel = $(serviceDiv).find("div[name='confmap']");
        var confmaps = [];
		$.each($confmapDivPanel, function(index_p, cm) {
			var $confmap = {};
			var $confmapchk = $(cm).find("input:checkbox");
            $confmap.name = $confmapchk.val();
            $confmap.path = $(cm).find('#confmapPath').val();
			if ($confmapchk.is(':checked') && $confmap.path !== "") {
                confmaps.push($confmap);
            }
        });
        $ser.confmaps = confmaps;
        var $containerDivPanel = $(serviceDiv).find("div[name='container']");
        var cons = [];
		var sps = [];
        $.each($containerDivPanel, function(index_l, container) {
            var $con = {};
			var cps = [];
            $con.imageId = $(container).find("#conImage").data('imgId');
            //alert($con.imageId);
			var $portDivPanel = $(container).find("div[name='port']");
			$.each($portDivPanel, function(index_m, port) {
				var portName = "c-" + index_l + "-p-" + index_m;
				var np = Number($(port).find("input[id='nodePort']").val());
				var cp = Number($(port).find("input[id='conPort']").val());

				var $cp = {};
				$cp.containerPort = cp;
				$cp.name = portName;
				$cp.protocol = "TCP";
				cps.push($cp);

				var $sp = {};
				$sp.port = cp;
				$sp.targetPort = cp;
				$sp.name = portName + "-auto";
				$sp.protocol = "TCP";

				if(np >= 1000)
				{
					$sp.name = portName;
					$sp.nodePort = np ;
				}
				sps.push($sp);
			});
			$con.containerPorts = cps;
            $con.quotas_cpu = $(container).find("input[id='conCPU']").val();
            $con.quotas_memory = $(container).find("input[id='conMemory']").val();
            var $volMountDivPanel = $(container).find("div[name='volMount']");
            var vms = [];
            $.each($volMountDivPanel, function(index_n, vm) {
                var $vm = {};
                //$vm.name = $(vm).find("label[id='volmountName']").html();
                $vm.name = $(vm).find("input[id='volmountName']").val();
                $vm.path = $(vm).find("input[id='volmountPath']").val();
                if ($vm.name !== "" && $vm.path !== "") {
                    vms.push($vm);
                }
            });
            $con.volumes = vms;
            var $envDivPanel = $(container).find("div[name='envVariable']");
            var ens = [];
            $.each($envDivPanel, function(index_o, env) {
                var $en = {};
                $en.name = $(env).find("input[id='envName']").val();
                $en.value = $(env).find("input[id='envValue']").val();
                if ($en.name !== "" && $en.value !== "") {
                    ens.push($en);
                }
            });
            $con.envVariables = ens;
            var lpType = $(container).find(".liveProbeType").val();
            if (lpType !== '') {
                var $lp = {};
                $lp.type = lpType;
                var $lpDiv = $(container).find(".liveProbeDiv");
                switch (lpType) {
                    case 'httpGet':
                        $lp.path = $lpDiv.find('#liveProbePath').val();
                        $lp.port = $lpDiv.find('#liveProbePort').val();
                        break;
                    case 'tcpSocket':
                        $lp.port = $lpDiv.find('#liveProbePort').val();
                        break;
                    case 'exec':
                        $lp.command = $lpDiv.find('#liveProbeCommand').val();
                        break;
                    default:
                        break;
                }
                $lp.initialDelaySeconds = $lpDiv.find('#liveProbeDelay').val();
                $lp.timeoutSeconds = $lpDiv.find('#liveProbeTimeout').val();
                $con.livenessProbe = $lp;
            }
            cons.push($con);
        });
		$ser.servicePorts = sps;
        $ser.containers = cons;
        $json.push($ser);
    });
    return $json;
}

function createJobJson() {
    var $json = [];
    var $jobDivPanel = $("div[name='servPanel']");
    $.each($jobDivPanel, function(index_i, jobDiv) {
        var $job = {};
        $job.name = $(jobDiv).find("input[id='jobName']").val();
		$job.parallelism = $(jobDiv).find("input[id='jobParallelism']").val();
		var $labelDivPanel = $(jobDiv).find("div[name='label']");
        var lbls = {};
        $.each($labelDivPanel, function(index_j, label) {
            var lkey = $(label).find("input[id='labelKey']").val();
			var lval = $(label).find("input[id='labelValue']").val();
            if (lkey !== "" && lval !== "") {
				lbls[lkey] = lval;
            }
        });
        $job.labels = lbls;
        var $containerDivPanel = $(jobDiv).find("div[name='container']");
        var cons = [];
		$.each($containerDivPanel, function(index_l, container) {
            var $con = {};
			$con.imageId = $(container).find("#conImage").data('imgId');
			$con.command = $(container).find('#cmdText').val();
            cons.push($con);
        });
        $job.containers = cons;
        $json.push($job);
    });
    return $json;
}

//加载websocket web console
var ws;
function openConsole(namespace, serviceName, podName) {
	$('#consModalContent').removeData().unbind().html('').css({"height":"500px"});
	createWsConnection(0, namespace, serviceName, podName);

	$('#consModal').on('hide.bs.modal', function() {
		closeCon();
	});

	$('#consModal').modal('show');
}

function createWsConnection(clusterID, namespace, serviceName, podName)
{
	
	ws = new WebSocket("ws://" + window.location.host + "/wswc");
	ws.onopen = function(event) {
		//Socket opened, sending login credentials
		ws.send("login;" + clusterID + ";" + namespace + ";" + serviceName + ";" + podName);

		$('#consModalContent').terminal(function(command, term) {
			if (command !== '')
				ws.send(command);
		}, {
			greetings : 'Web Console - ' + podName,
			height :470,
			prompt : '$> ',
			exit : false,
			keypress: function(e, term) {
				console.log('key   ' + e.which);
				if (e.ctrlKey && e.which === 99) {
					console.log('ctrl-c');
				}
			}
		});
	}
	ws.onmessage = function(event) {
		if (event.data.length > 0) {
			$('#consModalContent').terminal().echo(event.data);
		}
	};
	ws.onerror = function(event) {
		$('#consModalContent').terminal().error("On Error... Connection Closed...");
		closeCon();
	};
	ws.onclose = function(event) {
		var showErr = false;
		if(event !== null)
		{
			switch (event.code)
			{
				case 1000:
					$('#consModal').modal('hide');
					break;
				case 1001:
					alertError("Disconnected", "Console Timeout");
					break;
				case 1002:
					alertError("ERROR", "HTTP2 not compatible, please verify your browser and server");
					showErr = true;
					break;
				case 1007:
					alertError("ERROR", "Authentication Error");
					showErr = true;
					break;
				case 1008:
					alertError("ERROR", "Console for Pod <strong>" + podName + "</strong> not available");
					showErr = true;
					break;
				case 1006:
				case 1011:
					alertError("ERROR", "Server Console Error");
					showErr = true;
					break;
				default:
					console.log(event.code);
					break;
			}
		}
		if(showErr)
			$('#consModalContent').terminal().error('Console Error');

		closeCon();
	};
}

function closeCon()
{
	if (ws) {
		ws.close();
		ws = null;
	}
	$('#consModalContent').html('');
}

var logTaskStopped;
function openLogs(_id, _podName, _ku8type, _caller) {
	var data = $(_caller).data('btndata');
	var cdata = data.containers;
	var containerName;
	if(cdata.length > 1)
	{
		var radio = "";
		$.each(cdata, function(i_pos, c) {
			radio += '<label><input type="radio" name="cradio" value="' + c.name +'"> ' + c.name + ' (' + c.imageName + ')' + '</label><br/>';
		});
		$(logsHelperBody).html(radio).iCheck({
			radioClass: 'iradio_square-blue',
			increaseArea: '20%'
		});
		$(logsHelperModal).modal().one('click', '#modal-view', function() {
			setLogs(_id, _podName, $(logsHelperBody).find('input:checked').val(), _ku8type)
		});
	}
	else
	{
		setLogs(_id, _podName, cdata[0].name, _ku8type)
	}
}

$('#logsModal').on('hide.bs.modal', function() {
    logTaskStopped = true;
});

function setLogs(_id, _podName, _containerName, _ku8type)
{
	$('#autorefresh').iCheck({
		radioClass: 'iradio_square-blue',
		increaseArea: '20%'
	}).iCheck('check').on('ifClicked', function(event){
		if(this.checked)
		{
			$(this).iCheck('uncheck');
			logTaskStopped = true;
		}
		else
		{
			logTaskStopped = false;
			getLogs(_id, _podName, _containerName, _ku8type);
		}
	});

	logTaskStopped = false;
	getLogs(_id, _podName, _containerName, _ku8type);
}

function getLogs(_id, _podName, _containerName, _ku8type)
{
    $.ajax({
        url: "/" + _ku8type + "/getPodLogs",
        type: "GET",
        dataType: "json",
		data: {
			ku8ID:_id,
			podName:_podName,
			containerName:_containerName
		},
        success: function(response) {
			if(response.status > -1)
			{
				if(!logTaskStopped)
				{
					$('#logsText').val(response.message);

					if(!$('#logsModal').hasClass('in'))
					{
						$('#logsModal').modal('toggle');
						$('#autorefreshlbl').show();
					}

					setTimeout(function() {
						getLogs(_id, _podName, _containerName, _ku8type);
					}, 2000);
				}
			}
			else
			{
				logTaskStopped = true;
				alertError("ERROR", response.message);
			}
		}
	});
}
