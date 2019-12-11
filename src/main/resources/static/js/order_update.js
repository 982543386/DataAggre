var orderUpdate = {
	orderId:null,
	init : function() {
		var orderId = sessionStorage.getItem("id");
		orderUpdate.orderId = orderId;
		sessionStorage.removeItem("id");
		
		if(orderId !== null)
		{
			orderUpdate.getOrder(orderId);
			
			orderUpdate.listOrderAblity(orderId);
			
		}
		
		orderUpdate.initEvent();
		
	},
	
	initEvent : function(e){
		
		
	},
	
	getOrder :function(orderId) {
		$.ajax({
			url: "order/view",
			type: "POST",
			dataType: "json",
			data: {
				id:orderId
			},
			success: function(order){
				console.log(order);
				$('#orderNo').val(order.orderNo);
				$('#start').val(new Date(order.startTime).format("yyyy-MM-dd HH:mm:ss"));
				$('#end').val(new Date(order.endTime).format("yyyy-MM-dd HH:mm:ss"));
				$('#remark').val(order.remark);
				$('#zone').val(order.zoneId);
				
			}
		});
	},
	
	



	listOrderAblity :function(orderId) {
		$.ajax({
			url: "order/listOrderAblity",
			type: "POST",
			dataType: "json",
			data: {
				orderId:orderId
			},
			success: function(response){
				var html='<ul class="todo-list m-t" >';
				
				for ( var i =0; i<response.length; i++) {
					var item = response[i];
					if(item.deliverDescribe == null){
						item.deliverDescribe = '';
					}
					html+='<li>'+
                    '<span class="m-l-xs"><strong>'+item.abilityName+'</strong></span> <small class="label label-primary pull-right" onclick="return orderUpdate.toService(\''+item.createUrl+'\')"><i class="fa fa-clock-o"></i> 开通</small><p>实例ID：'+item.abilityInstance+'</p><p>服务描述：'+item.abilityDesc+'</p> <p><div class="input-group"><input id="deliver'+item.id+'" type="text" value="'+item.deliverDescribe+'"  class="form-control allow-special validation"  placeholder="填写交付方式"  /> <span class="input-group-btn"> <button type="button" onclick="return orderUpdate.updateInstanceDeliver(\''+item.id+'\')" class="btn btn-primary">确认</button> </span></div></p></li>';
				}
				html+=  '</ul>';
				$('#ablity').html(html);
				
			}
		});
	},
	
	toService : function(url) {
		
		var myDate = new Date();
//		location.href=url+"?now="+myDate.toLocaleTimeString();
		$(".content").load(url+"?h="+myDate.toLocaleTimeString());
	},
	
	
	
	updateInstanceDeliver : function(id) {
		var deliverDescribe = $('#deliver'+id).val();
        $.ajax({
            url: "order/updateInstanceDeliver",
            type: "POST",
            dataType: "json",
            data: {
            	id: id,
            	deliverDescribe:deliverDescribe
            },
            success: function(response) {
                if (response.status > -1) {
					
                	orderUpdate.listOrderAblity(orderUpdate.orderId);
                } else {
					alertError("ERROR", response.message);
                }
            }
        });
	},
	
	
	refuse : function() {
		$('#refuseModalMsg').html('您确定要拒绝这个订单吗?');
		$('#refuseModal').modal().one('click', '#modal-refuse', function() {
			//$(e.target).button('loading');
			//confirmRefuse();
			var remark = $('#remark').val();
	        $.ajax({
	            url: "order/refuse",
	            type: "POST",
	            dataType: "json",
	            data: {
	            	id: orderUpdate.orderId,
	            	remark:remark
	            },
	            success: function(response) {
	                if (response.status > -1) {
	                	
	                	setTimeout(function () { 
	                		$('section.content').load('order/order_list.html', function() {
								alertSuccess(response.message, "订单已拒绝.");
						    });
	                    }, 500);
	                	
						
						
	                } else {
						alertError("ERROR", response.message);
	                }
	            }
	        });
		});
	},
	
	finish : function() {
		$('#refuseModalMsg').html('您确定要处理这个订单吗?');
		$('#refuseModal').modal().one('click', '#modal-refuse', function() {
			var remark = $('#remark').val();
	        $.ajax({
	            url: "order/finish",
	            type: "POST",
	            dataType: "json",
	            data: {
	            	id: orderUpdate.orderId,
	            	remark:remark
	            },
	            success: function(response) {
	                if (response.status > -1) {
	                	setTimeout(function () { 
	                		$('section.content').load('order/order_list.html', function() {
								alertSuccess(response.message, "订单已处理完成.");
						    });
	                    }, 500);
//						$('section.content').load('order/order_list.html', function() {
//							alertSuccess(response.message, "订单已处理完成.");
//					    });
						
	                } else {
						alertError("ERROR", response.message);
	                }
	            }
	        });
		});   
	}

};

$(document).ready(function(){
	orderUpdate.init();
	
});