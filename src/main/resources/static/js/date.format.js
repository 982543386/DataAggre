/**
* 时间对象的格式化
*/
Date.prototype.format = function(format){
var y=this.getFullYear(),
    M=this.getMonth(),
    d=this.getDate(),
    h=this.getHours(),
    m=this.getMinutes(),
    s=this.getSeconds(),
    ms=this.getMilliseconds(),
    z= this.getTimezoneOffset(),
    wd=this.getDay(),
    me=new Date(y,M,0).getDate(),
    w=["\u65E5","\u4E00","\u4E8C","\u4E09","\u56DB","\u4E94","\u516D"];
var h12=h>12?h-12:h;
var o = {
"y+" : y,                        //年份
"M+" : M+1,                        //月份
"d+" : d,                        //月份中的天数 
"H+" : h,                        //小时24H制            
"h+" : h12==0?12:h12,            //小时12H制
"m+" : m,                        //分钟
"s+" : s,                        //秒
"ms" : ms,                        //毫秒
"a+" : h>12||h==0?"PM":"AM",    //AM/PM 标记 
"w+" : wd,                        //星期 数字格式
"W+" : w[wd],                    //星期 中文格式
"q+" : Math.floor((m+3)/3),        //一月中的第几周
"e+" : me,                        //月份中的最大天数,如1月有31天,返回31 
"z+" : z                        //时区
}
if (/(y+)/.test(format))
    format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4- RegExp.$1.length));
for (var i in o)
if (new RegExp("(" + i + ")").test(format))
    format = format.replace(RegExp.$1, RegExp.$1.length == 1? o[i]: ("00" + o[i]).substr(("" + o[i]).length));
return format;
};
