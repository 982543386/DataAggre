package org.ku8eye.ctrl;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.apache.log4j.Logger;
import org.ku8eye.domain.User;
import org.ku8eye.service.UserActionSecurity;

/**
 * 安全拦截器
 * 
 * @author guiyunuo
 */
@Configuration
public class Security extends WebMvcConfigurerAdapter
{
	static final Logger log = Logger.getLogger(Security.class);
	@Autowired
	private UserActionSecurity userAction;
	
	/**
	 * 配置拦截器
	 * 
	 * @author
	 * @param registry
	 */
	public void addInterceptors(InterceptorRegistry registry)
	{

		registry.addInterceptor(new UserSecurityInterceptor(userAction)).addPathPatterns("/**");
	}
}

class UserSecurityInterceptor implements HandlerInterceptor
{
	UserActionSecurity userAction;
	public UserSecurityInterceptor(UserActionSecurity _userAction)
	{
		this.userAction=_userAction;
	}
	
	static final Logger log = Logger.getLogger(UserSecurityInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	 {

		User u = (User) request.getSession().getAttribute(
				org.ku8eye.Constants.USER_SESSION_KEY);
		String uri = request.getRequestURI();

		// 登录检查
		if (uri.endsWith("login.html") || uri.endsWith("checklogin"))
			return true;

		log.info("====>>" + uri);
		// 写到一行怕太长....
		// uri访问权限检查
		if (u != null) {
			if (userAction.hasAction(u.getUserType(), uri)) {
				return true;
			}
		}
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		RequestDispatcher rd = request.getRequestDispatcher("login.html");
		rd.forward(request, response);
		return false;

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
	{
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
	{
	}
}