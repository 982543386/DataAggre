$(function() {
	initUserMenu();
	initUserInfo();
})

function initUserMenu() {
	// 先将菜单清理掉
	$('.sidebar-menu > li:eq(0)').nextAll().remove();
	$.ajax({
		url : '/menus?time=' + new Date().getTime(),
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			if (JSON.parse(XMLHttpRequest.status) === 403) {
				window.location.replace('/login.html');
			}
		},
		success : function(menu) {
			setMenu($('.sidebar-menu'), menu);
			$('.smenuitem').bind('click', function() {
				
				if ($(this).attr("data") != '') {
					//window.history.reload();
					//$(".content").reload();
					//window.location.reload();
					//alert(2);
					//$(".content").refresh();
					//location.reload(force);
					$(".content").html("");
					$(".content").load($(this).attr("data")+"?_f="+$.now());
				}
			});
			//$(".content").load("all_info.html");
			$(".content").load("home.html");
		}
	});
}


function setMenu(menuid,menus){
	var menuhtm = [];
	$.each(menus,function(n,menudata) {
		 menuhtm.push("<li id=\"menu");
		 menuhtm.push(menudata.menuId);
		 menuhtm.push("\" class=\"treeview\">");
		 menuhtm.push("<a href=\"#\">");
	     menuhtm.push("<i class=\"fa "+getMenuIcon(menudata.menuType)+"\"></i> <span>");
		 menuhtm.push(menudata.menuName);
		 if(menudata.subMenus.length>0){
			 menuhtm.push("</span><i class=\"fa fa-angle-left pull-right\"></i></a>")
			 menuhtm.push(setSecondLevelMenu(menudata.subMenus));
		 }else{
			 menuhtm.push("</a>")
		 }
		 menuhtm.push("</li>");
	})
	menuid.append('' + menuhtm.join('') + '');
}

function setSecondLevelMenu(menus){
	var menuhtm = [];
	menuhtm.push("<ul class=\"treeview-menu\">");
	$.each(menus,function(n,menudata) {
		 
		 menuhtm.push("<li><a class=\"smenuitem\" href='javascript:void(0)' onclick='setTitle(this)' data=\""+menudata.menuUrl+"\"><i class=\"fa "+getMenuIcon(menudata.menuType)+"\"></i>"+menudata.menuName);
		 if(menudata.subMenus.length>0){
			 menuhtm.push("<i class=\"fa fa-angle-left pull-right\"></i></a>")
			 menuhtm.push(setThirdLevelMenu(menudata.subMenus));
		 }else{
			 menuhtm.push("</a>")
		 }
		 menuhtm.push("</li>");
	})
	menuhtm.push("</ul>");
	//alert(menuhtm);
	return '' + menuhtm.join('') + '';
}

function setThirdLevelMenu(menus){
	var menuhtm = [];
	menuhtm.push("<ul class=\"treeview-menu\">");
	$.each(menus,function(n,menudata) {
		 menuhtm.push("<li><a class=\"smenuitem\" href='javascript:void(0)' onclick='setTitle(this)' data=\""+menudata.menuUrl+"\"><i class=\"fa "+getMenuIcon(menudata.menuType)+"\"></i>"+menudata.menuName+"</a>");
	})
	menuhtm.push("</ul>");
	return '' + menuhtm.join('') + '';
}

function getMenuIcon(menutype){
	var iconClass;
	switch(menutype){
		case "1" : iconClass="fa-cloud"; break;//Zone
		case "2" : iconClass="fa-cubes"; break;//clust group
		case "3" : iconClass="fa-cogs"; break;//clust node
		case "4" : iconClass="fa-codepen"; break; //host group
 		case "5" : iconClass="fa-circle"; break;//host node
		case "6" : iconClass="fa-th"; break; // project group
		case "7" : iconClass="fa-file"; break;//project node
		case "8" : iconClass="fa-cubes"; break;//project node
		case "10" : iconClass="fa-pencil"; break;//marathon node
		case "11" : iconClass="fa-weixin"; break;//marathon node
		case "12" : iconClass="fa-list-ul"; break;//manage
		case "13" : iconClass="fa-check-square-o"; break;// order
		case "14" : iconClass="fa-cloud-upload"; break;//cicd
		case "15" : iconClass="fa-paper-plane-o"; break;//申请
		case "16" : iconClass="fa-hand-o-right"; break;//申请向导
		case "17" : iconClass="fa-cubes"; break;//普通容器管理
		case "18" : iconClass="fa-codepen"; break;//定制容器管理
		case "19" : iconClass="fa-jsfiddle"; break;//devdop详细	
		case "20" : iconClass="fa-calendar-minus-o"; break;//订单详情
		case "21" : iconClass="fa-map-o"; break;//cicd详情
	}
	return iconClass;
}
var loginUser;
function initUserInfo() {
	$.ajax({
		url : '/loginuser?time=' + new Date().getTime(),
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			if (JSON.parse(XMLHttpRequest.status) === 403) {
				window.location.replace('/login.html');
			}
		},
		success: function(user){
			if(user!=null&&user!=''){
				console.debug(user.note);
				loginUser=user;
				$('.username').html(user.userId);
				console.debug("user note :"+user.note);
				$("#user_desc").html(user.note)
				
			}
		}
	});
}
/**
 * 自定义Map对象
 */
function Map() {  
	 var struct = function(key, value) {  
	  this.key = key;  
	  this.value = value;  
	 }  
	   
	 var put = function(key, value){  
	  for (var i = 0; i < this.arr.length; i++) {  
	   if ( this.arr[i].key === key ) {  
	    this.arr[i].value = value;  
	    return;  
	   }  
	  }  
	   this.arr[this.arr.length] = new struct(key, value);  
	 }  
	   
	 var get = function(key) {  
	  for (var i = 0; i < this.arr.length; i++) {  
	   if ( this.arr[i].key === key ) {  
	     return this.arr[i].value;  
	   }  
	  }  
	  return null;  
	 }  
	   
	 var remove = function(key) {  
	  var v;  
	  for (var i = 0; i < this.arr.length; i++) {  
	   v = this.arr.pop();  
	   if ( v.key === key ) {  
	    continue;  
	   }  
	   this.arr.unshift(v);  
	  }  
	 }  
	   
	 var size = function() {  
	  return this.arr.length;  
	 }  
	   
	 var isEmpty = function() {  
	  return this.arr.length <= 0;  
	 }   
	 this.arr = new Array();  
	 this.get = get;  
	 this.put = put;  
	 this.remove = remove;  
	 this.size = size;  
	 this.isEmpty = isEmpty;  
	}


/**
 * 告警model 对话框 
 * func 是点击提交后的回调函数
**/
function showAlertModel(title,body,func)
{
	$('#delAlertModel').find(".modal-title").html(title);
	$('#delAlertModel').find(".modal-body").html(body);
	
	$('#delAlertModel').modal().one('click', '#modal-confirm', function() {
		func();	
	});
}

/**
 *datatable refresh;
 * @param tableId
 * @param urlData

function RefreshTable(tableId, urlData)
{
  $.getJSON(urlData, null, function( json )
  {
    table = $(tableId).dataTable();
    oSettings = table.fnSettings();

    table.fnClearTable(this);

    for (var i=0; i<json.data.length; i++)
    {
      table.oApi._fnAddData(oSettings, json.data[i]);
    }

    oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
    table.fnDraw();
  });
}
 */