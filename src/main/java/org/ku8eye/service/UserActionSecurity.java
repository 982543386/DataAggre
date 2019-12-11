package org.ku8eye.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.ku8eye.Constants;
import org.ku8eye.ctrl.Security;
import org.ku8eye.domain.UserAction;
import org.ku8eye.mapping.UserActionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserActionSecurity {
	static final Logger log = Logger.getLogger(UserActionSecurity.class);

	@Autowired
	private UserActionMapper dao;

	private static List<UserAction> users;
	private static List<UserAction> super_admin;
	private static List<UserAction> tenant_admin;

	public void add(UserAction ua) {
		dao.insertUserAction(ua);
	}

	public void del(UserAction ua) {
		dao.deleteByPrimaryKey( ua.getUri());
	}

	public List<UserAction> listAll() {
		return dao.selectALL();
	}

	public List<UserAction> listByUserType(String userType) {
		return dao.selectByUserType(userType);
	}

	public boolean hasAction(String userType, String requri) {
		return true;
//		if (Constants.USERTYPE_ADMIN.equals(userType)) {
//			if (super_admin == null)
//				super_admin = listByUserType(Constants.USERTYPE_ADMIN);
//
//			return hasAction(super_admin, requri);
//
//		} else if (String.valueOf(Constants.USERTYPE_GROUP_ADMIN).equals(userType)) {
//			if (tenant_admin == null)
//				tenant_admin = listByUserType(String.valueOf(Constants.USERTYPE_GROUP_ADMIN));
//
//			return hasAction(tenant_admin, requri);
//		} else if (Constants.USERTYPE_TENANT.equals(userType)) {
//			if (users == null)
//				users = listByUserType(Constants.USERTYPE_TENANT);
//
//			return hasAction(users, requri);
//
//		} else {
//			return false;
//		}
	}

	public boolean hasAction(List<UserAction> confUris, String reqUri) {
		for (UserAction confUri : confUris) {
		//	log.info("confUri ===>>"+confUri.getUri()+"    reqUri====>"+reqUri);
			if(isEqualUri(confUri.getUri(), reqUri))
			{
				return true;
			}
		}
		return false;
	}

	private boolean isEqualUri(String confUri, String reqUri) {
		String conf[] = confUri.split("/");
		String req[] = reqUri.split("/");
		if (conf.length != req.length) {
			return false;
		}
		for (int i = 0; i < conf.length; i++) {

			if (isCommentUri(conf[i])) {
				continue;
			}
			if (!conf[i].equals(req[i]))
				return false;
		}
		return true;
	}

	private boolean isCommentUri(String str) {
		if (str.length() < 3)
			return false;
		String b = str.substring(0, 1);
		String e = str.substring(str.length() - 1, str.length());
		if (b.equals("{") && e.equals("}"))
			return true;
		else
			return false;
	}
}
