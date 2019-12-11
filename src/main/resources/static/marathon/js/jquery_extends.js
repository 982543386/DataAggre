/*
 * mars 平台基础jquery扩展
 * 说明：依赖于jquery
 * 格式：
 * [序号]-[方法名]-[作者]-[时间]-[功能]-[备注]
 * 
 * [1]-[formToObj]-[zbx]-[2016-08-26]-[form转js对象，异步提交时使用]-[表单必须含有name属性]
 */

(function($){
	$.fn.extend({
		formToObj: function(){
			var data = new Object;
			$(
			/*此处添加form中需要上传的元素*/
			"input[type=text]," +
			"input[type=number]," +
			"input[type=password]," +
			"input[type=radio]:checked," +
			"input[type=checkbox]:checked," +
			"select,"+
			"textarea",
			this).each(function(){
						var name = $(this).attr('name'); 
						data[name] = $(this).val(); 
			});
			return data; 
		} 
	});
})(jQuery);
					
	


