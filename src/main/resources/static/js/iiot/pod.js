$(document).ready(function() {
	var appName = sessionStorage.getItem("appName");
	var params = {};
	params["appName"] = appName;
	$.ajax({
	  type: 'POST',
	  data: params,
	  url: '/podMonitor/getPodsList',
	  success : function(data){
		  if(data.flag & data.type == "1"){
			  var podlist = data.result;
			  $("#podname").empty();
			  for(var i=0; i<podlist.length; i++){
				  $("#podname").append("<option value=\""+podlist[i]+"\">"+podlist[i]+"</option>");
			  }
			  var curpod = podlist[0];
			  var timeGap = $("#timeGap option:checked").val();
			  toDetailChart(curpod,timeGap);
		  }else{
			  setTimeout(function(){
	              alert("未查询到有效数据!");
	              $('.content').load('iiot/app/edge_app_monitor.html')
	    	  }, 1000);
		  }
	  },
	  error : function(errorMsg){
		  alert("图表请求数据失败!");
	  }
    });
	$("#timeGap").change(function(){
  	    var gap = $("#timeGap option:checked").val();
  	    var pod = $("#podname option:checked").val();
  	    toDetailChart(pod,gap);
    });
	$("#podname").change(function(){
  	    var gap = $("#timeGap option:checked").val();
  	    var pod = $("#podname option:checked").val();
  	    toDetailChart(pod,gap);
    });
});

function toDetailChart(curpod,timeGap){
	var cpuChart = echarts.init(document.getElementById('cpu_pod_chart'));
    cpuChart.clear();
    var memChart = echarts.init(document.getElementById('memory_pod_chart'));
    memChart.clear();
    var ioChart = echarts.init(document.getElementById('io_pod_chart'));
    ioChart.clear();
	  var params1 = {};
	  params1["curpod"] = curpod;
	  params1["timeGap"] = timeGap;
	  $.ajax({
		  type: 'POST',
		  data: params1,
		  url: '/podMonitor/podCPUDetail',
		  success : function(data){
			  if(data.flag){
				  var result = data.result;
  			  var xtime = [];
  			  var yvalue = [];
  			  for (var i = 0; i < result.length; i++) {
  				  xtime.push(result[i].time);
  				  yvalue.push(result[i].res);
  			  }
  			  
  			  cpuChart.setOption({
  				  title: {
  				  	  text : curpod+" CPU Usage (%)",
  				  	  x:'center',
  			          y:'top'
  				  },
 				      xAxis: {
 				          type: 'category',
 				          data: xtime
 				      },
 				      yAxis: {
 				          type: 'value',
	   				      axisLabel : {
	   				          formatter : '{value}%'
	   				      }
 				      },
 				      series: [{
 				          data: yvalue,
 				          type: 'line',
 				          itemStyle : { 
 				        	  normal: {
 				        		  label : {
 				        			  show: true
 				        		  }
 				      		  }
 				      	  }
 				      }]
  			  });
			  }
		  }
	  });
	  $.ajax({
		  type: 'POST',
		  data: params1,
		  url: '/podMonitor/podMemoryDetail',
		  success : function(data){
			  if(data.flag){
				  var result = data.result;
  			  var xtime = [];
  			  var yvalue = [];
  			  for (var i = 0; i < result.length; i++) {
  				  xtime.push(result[i].time);
  				  yvalue.push(result[i].res);
  			  }
  			  
  			  memChart.setOption({
  				  title: {
  				  	  text : curpod+" Memory Usage (MB)",
  				  	  x:'center',
  			          y:'top'
  				  },
 				      xAxis: {
 				          type: 'category',
 				          data: xtime
 				      },
 				      yAxis: {
 				          type: 'value',
	   				      axisLabel : {
	   				          formatter : '{value}MB'
	   				      }
 				      },
 				      series: [{
 				          data: yvalue,
 				          type: 'line',
 				          itemStyle : { 
 				        	  normal: {
 				        		  label : {
 				        			  show: true
 				        		  }
 				      		  }
 				      	  }
 				      }]
  			  });
			  }
		  }
	  });
	  $.ajax({
		  type: 'POST',
		  data: params1,
		  url: '/podMonitor/podIODetail',
		  success : function(data){
			  if(data.flag){
				  var result = data.result;
  			  var xtime = [];
  			  var yvalue = [];
  			  for (var i = 0; i < result.length; i++) {
  				  xtime.push(result[i].time);
  				  yvalue.push(result[i].res);
  			  }
  			  
  			  ioChart.setOption({
  				  title: {
  				  	  text : curpod+" Network I/O (Byte)",
  				  	  x:'center',
  			          y:'top'
  				  },
 				      xAxis: {
 				          type: 'category',
 				          data: xtime
 				      },
 				      yAxis: {
 				          type: 'value',
	   				      axisLabel : {
	   				          formatter : '{value}B'
	   				      }
 				      },
 				      series: [{
 				          data: yvalue,
 				          type: 'line',
 				          itemStyle : { 
 				        	  normal: {
 				        		  label : {
 				        			  show: true
 				        		  }
 				      		  }
 				      	  }
 				      }]
  			  });
			  }
		  }
	  });
}