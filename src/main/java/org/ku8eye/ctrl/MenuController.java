package org.ku8eye.ctrl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ku8eye.Constants;
import org.ku8eye.bean.ui.Menu;
import org.ku8eye.domain.User;
import org.ku8eye.service.UIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * resetFul menu
 * 
 * @author jackChen
 *
 */
@RestController

public class MenuController {
	@Autowired
	private UIService uiService;

	/**
	 * get menus
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/menus", method = RequestMethod.GET)
	public List<Menu> getUserMenus(HttpServletRequest request) {
		User  user=(User)request.getSession().getAttribute(Constants.USER_SESSION_KEY);
		return uiService.generateMenus(user);
	}

}
