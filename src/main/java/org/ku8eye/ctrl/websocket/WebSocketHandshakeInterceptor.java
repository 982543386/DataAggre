package org.ku8eye.ctrl.websocket;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.ku8eye.Constants;
import org.ku8eye.domain.User;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * @author Federico Gu 
 * Email: federico.gu@hpe.com
 * Date: 2016-05 
 * Description: Websocket handshake interceptor Pods
 */
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor
{

	private static final Logger log = Logger.getLogger(HandshakeInterceptor.class);

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception
	{
		if (request instanceof ServletServerHttpRequest)
		{
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
			HttpSession session = servletRequest.getServletRequest().getSession(false);
			if (session != null)
			{
				User user = (User) session.getAttribute(Constants.USER_SESSION_KEY);
				
				if(user == null)
				{
					log.error("User not logged in while Handshaking with ws://");
					return false;
				}
				
				log.info("[USER: " + user.getUserId() + "] Requesting ws:// connection");
				attributes.put(Constants.USER_SESSION_KEY, user);
				return true;
			}
			else
			{
				log.error("Session not found while Handshaking with ws://");
				return false;
			}
		}
		log.error("Unknown request while Handshaking with ws://");
		return false;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception){}
}