package org.ku8eye.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class StringUtil {

	static Logger log = Logger.getLogger(StringUtil.class);
	/**
	 * 获取字符串中的 数字
	 * @param val
	 *    1a3w3  返回 133
	 * @return 成功返回获取数字 失败或字符串异常返回0
	 */
	public static int getStringNum(String  val)
	{
		try{
			if(val!=null&&!"".equals(val.trim()))
			{
				String regEx="[^0-9]";   
				Pattern p = Pattern.compile(regEx);   
				Matcher m = p.matcher(val);
				return Integer.valueOf(m.replaceAll("").trim());	
			}
			else
			{
				log.error(val+" is NULL get number error..return 0..");
				return 0;
			}
		}catch(Exception e)
		{
			log.error(val+" get number error..return 0..",e);
			return 0;
		}
		
	}
	
}
