package org.ku8eye.ctrl.manage;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ku8eye.Constants;
import org.ku8eye.domain.User;
import org.ku8eye.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

//@EnableAutoConfiguration  
@RestController
public class ManageLogin {
	@Value("${application.hellowmsg:Hello World}")
	private String message = "Hello World";

	@Autowired
	private UserService userService;

	@RequestMapping("/manage/user/{id}")
	public User view(@PathVariable("id") Long id) {
		User user = new User();
		user.setUserId("guest" + id);
		user.setAlias("zhang");
		return user;
	}

	/**
	 * check login username and password
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@RequestMapping(value = "/manage/checklogin")
	public User checkLogin(HttpServletRequest request,
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
		User user = userService.getUserByUserId(username);
		if (user != null && password.equals(user.getPassword())) {
			// success put user into Session
			request.getSession().setAttribute("user", user);
			return user;
		} else {
			return null;
		}
	}

	@RequestMapping(value = "/manage/loginuser")
	public User loginuser(HttpServletRequest request) {
		User user = null;
		Object obj = request.getSession().getAttribute("user");
		if (obj != null) {
			user = (User) obj;
		}
		return user;
	}

	@RequestMapping("/manage/index")
	public ModelAndView main(HttpServletRequest request) {
		return new ModelAndView("/manage/main.html");
	}

	@RequestMapping(value = "/manage/sign")
	public ModelAndView example(HttpServletRequest request) {

		return new ModelAndView("/manage/login.html");

	}

	@RequestMapping(value = "/manage/signout")
	public ModelAndView signOut(HttpServletRequest request) {
		request.getSession().removeAttribute(Constants.USER_SESSION_KEY);
		
		return new ModelAndView("/manage/login.html");

	}

	@RequestMapping("/manage")
	public ModelAndView index(Map<String, Object> model) {

		model.put("time", new Date());

		model.put("message", this.message);

		return new ModelAndView("/manage/login.html");

		/*** 当返回index字符串，会自动 路径寻找index.jsp */

	}
}