var ip;
var topic;
$(document).ready(function() {
	ip = sessionStorage.getItem("ip");
	topic = sessionStorage.getItem("topic");
	var params = {};
	params["ip"] = ip;
	params["topic"] = topic;
	$.ajax({
	  type: 'POST',
	  data: params,
	  url: '/mqtt/testRecv',
	  success : function(data1){
		  alert("测试数据接收中...");
		  setTimeout(function(){
			  alert("测试数据接收完成");
			  $.ajax({
				  type: 'POST',
				  data: params,
				  url: '/mqtt/stopTest',
				  success : function(data){
					  var result = data.result;
					  var size = result.length;
					  $("#appTable tbody").html("");
					  for(var i=0; i<size; i++){
						  var line = result[i].data;
						  var row = "<tr><td>"+line+"</td></tr>";
						  $("#appTable tbody").append(row);
					  }
				  }
			  });
		  }, 5000);
	  }
	});
});

function back() {
	var params = {};
	params["ip"] = ip;
	params["topic"] = topic;
	$.ajax({
		  type: 'POST',
		  data: params,
		  url: '/mqtt/clearDB',
		  success : function(data1){
				$('.content').load('iiot/device/device_list.html');
				$.getScript("iiot/device/device_list.js");
				$.getScript("iiot/device/mqtt.js");
		  }
	});
}