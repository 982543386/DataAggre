var order = {
		
	init : function() {
		console.log("hello");
		
		/*order.initEvent();
		
		order.initAbility();*/
	},
	
/*	initEvent : function(e){
		
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
	},*/
	
	addOrder : function() {
	    if (checkInput('#orderform')) {
	    	var cdOption=$('input:radio[name="cdOption"]:checked').val();
	    	var zoneId = $('#cd_type').val(); 
	    	var repo_name = $('#repo_name').val();
	    	var inputSvnUserName = $('#inputSvnUserName').val(); 
	    	var inputSvnUserPassword = $('#inputSvnUserPassword').val(); 
	    	 

	        $.ajax({
	            url: "/k8order/cicd/add",
	            type: "POST",
	            dataType: "json",
	            data: {
	            	cdOption: cdOption,
	            	zoneId: zoneId,
	            	repo_name: repo_name  ,
	            	inputSvnUserName:inputSvnUserName,
	            	inputSvnUserPassword:inputSvnUserPassword
	            
	            },
	            success: function(response) {
	                /*if (response.status > -1) {
						
						$('section.content').load('devops/devops_check_user.html', function() {
							alertSuccess(response.message, "订单申请成功.");
					    });
						
	                } else {
						alertError("ERROR", response.message);
	                }*/
	            }
	        });
	    }
	}

	/*listAbility :function(zoneId, type) {
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
	}*/

};

$(document).ready(function(){
	order.init();
	
});