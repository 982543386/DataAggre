
/*datatable处理 start*/
function getReqMap(aDataSet){
	if(aDataSet.iDisplayLength){//服务端分页
		aDataSet.searchText=aDataSet.sSearch;//页面搜索内容
		aDataSet.page=((aDataSet.iDisplayStart/aDataSet.iDisplayLength)+1);//开始序列
		aDataSet.rows=aDataSet.iDisplayLength//每页条数
	}
	return aDataSet;
}

function successCallback(ret, aoData, fnCallback){
	// 构造返回数据
	dtData = getDataFromRet(ret, aoData);
	fnCallback(dtData);
};

function getDataFromRet(ret, aoData){
	if(ret){
		// 构造返回数据
		var dtData = {};
		dtData.iTotalRecords = 0;
		dtData.iTotalDisplayRecords = dtData.iTotalRecords;
		dtData.aaData = [];
		
		dtData.sEcho = aoData.sEcho;
		if(ret.data){
			dtData.aaData = ret.data;
			// 如果是分页查询，返回数据中有totalItemNum字段
			if(ret.total){
				dtData.iTotalRecords = ret.total;
				dtData.iTotalDisplayRecords = dtData.iTotalRecords;
			}else if(ret.data.length){
				dtData.iTotalRecords = ret.data.length;
				dtData.iTotalDisplayRecords = dtData.iTotalRecords;
			}
		}
		return dtData;
	}
	return null;
};

/*datatable处理 end*/

/******************************************************************************/
// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
// 例子： 
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
Date.prototype.Format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "h+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

/**************************/
/**
 * 四舍五入
 * 
 * num 需要格式化的数字
 * prex 保留的小数个数
 * 
 */
function fixNum(num,prex){
	if(num==null || num==""){
		console.log("需格式化的数值格式不正确");
		return 0;
	}
	if(isNaN(num)){
		console.log(num+" 不是数字");
		return 0;
	}
	if(prex==null || prex==""){
		prex = 2;
	}
	if(isNaN(prex)){
		console.log("需保留的小数位数格式不正确");
		prex = 2;
	}
	/**
	 * 格式化为数字
	 */
	num = parseFloat(num)
	
	return num.toFixed(prex);
}

