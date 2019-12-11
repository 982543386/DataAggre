var order = {
		
	init : function() {
		
		order.initEvent();
		
		order.initAbility();
	},
	
	initEvent : function(e){
		
		$('#zone').on('change',function(e){
			order.initAbility();
		});
		
	},
	
	initAbility: function(){
		var zoneId = $('#zone').val(); 
//		console.log("zoneId"+zoneId);
		order.listAbility(zoneId,1);
		order.listAbility(zoneId,2);
		order.listAbility(zoneId,3);
		order.listAbility(zoneId,4);
	},
	
	addOrder : function() {
	    if (checkInput('#orderform_cicd')) {
	    	var title_cicd = $('#title').val();
	    	
	    	
	    	
	    	/*var startTime = $('#start').val();
	    	var endTime = $('#end').val();
	        var remark = $('#remark').val();
	        var zoneId = $('#zone').val(); 
	        var ablityIds = new Array();
	        
	        $("#orderform input[type='checkbox']").each(function(i,item){
	        	if($(item).is(':checked')){
	        		
	        		ablityIds.push($(item).val());
	        		
	        	}
			});
	        console.log(ablityIds);
	        if(ablityIds.length < 1){
	        	ablityIds.push("");
	        	console.log(ablityIds);
	        }*/

	        $.ajax({
	            url: "k8order/add",
	            type: "POST",
	            dataType: "json",
	            data: {
	            	startTimeStr: startTime,
	            	endTimeStr: endTime,
	                remark: remark,
	                ablityIds: ablityIds,
	                zoneId:zoneId
	            },
	            success: function(response) {
	                if (response.status > -1) {
						
						$('section.content').load('order/order_list_user.html', function() {
							alertSuccess(response.message, "订单申请成功.");
					    });
						
	                } else {
						alertError("ERROR", response.message);
	                }
	            }
	        });
	    }
	},



	listAbility :function(zoneId, type) {
		$.ajax({
			url: "order/listAblity",
			type: "POST",
			dataType: "json",
			data: {
				zoneId:zoneId,
				type:type
			},
			success: function(response){
				var html='<ul class="todo-list m-t">';
				
				for ( var i =0; i<response.length; i++) {
					var item = response[i];
					
					html+='<li> <input type="checkbox" style="zoom:135%;" value="'+item.id+'" name="" class="i-checks" />'+
                    '<span class="m-l-xs">'+item.ablityName+'</span><p>'+item.abilityDesc+'</p> </li>';
				}
				html+=  '</ul>';
				$('#ablity'+type).html(html);
				
			}
		});
	}

};

$(document).ready(function(){
	order.init();
	
});