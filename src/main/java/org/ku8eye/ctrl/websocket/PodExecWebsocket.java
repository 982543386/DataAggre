package org.ku8eye.ctrl.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.ku8eye.Constants;
import org.ku8eye.domain.User;
import org.ku8eye.service.k8s.K8sAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.squareup.okhttp.Response;

import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;

/**
 * @author Federico Gu 
 * Email: federico.gu@hpe.com
 * Date: 2016-05 
 * Description: Websocket exec por Pods
 */
public class PodExecWebsocket implements WebSocketHandler {
	
	private Logger log = Logger.getLogger(PodExecWebsocket.class);
	private ExecWatch execWatch;
	private Thread sendMsgThread;
	private User user;

	@Autowired
	private K8sAPIService k8sAPIService;
	
	private static class SimpleListener implements ExecListener {

		public void onOpen(Response response)
		{
			 System.out.println("The shell will now open.");
		}

		public void onFailure(IOException e, Response response)
		{
			System.err.println("Shell Failed");
		}

		@Override
		public void onClose(int code, String reason)
		{
			System.out.println("The shell will now close. Reason: " + reason);
		}

		@Override
		public void onOpen(okhttp3.Response response) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFailure(Throwable t, okhttp3.Response response) {
			// TODO Auto-generated method stub
			
		}
    }
	
	@Override
	public void afterConnectionClosed(WebSocketSession paramWebSocketSession, CloseStatus paramCloseStatus) throws Exception {
		log.info("[USER: " + user.getUserId() + "] ws:// connection closed");
		if(sendMsgThread != null)
		{
			sendMsgThread.interrupt();
			sendMsgThread = null;
		}
		
		if(execWatch != null)
		{
			execWatch.getInput().close();
			execWatch.getOutput().close();
			execWatch = null;
		}
		
		user = null;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession paramWebSocketSession) throws Exception
	{
		user = (User) paramWebSocketSession.getAttributes().get(Constants.USER_SESSION_KEY);
		log.info("[USER: " + user.getUserId() + "] ws:// connection established");
	}
	
	private class OutMess implements Runnable
	{
		BufferedReader br;
		WebSocketSession wss;
		
		public OutMess(InputStream pin, WebSocketSession wss) {
			this.br = new BufferedReader(new InputStreamReader(pin));
			this.wss = wss;
		}

		@Override
		public void run() {
			try {
				String line = null;
				while((line = br.readLine()) != null)
				{
					wss.sendMessage(new TextMessage(new String(line.getBytes(), "UTF-8")));
				}
			} catch (IOException e) {
				//Thread closed
			}
		}
	}

	@Override
	public void handleMessage(WebSocketSession paramWebSocketSession, WebSocketMessage<?> paramWebSocketMessage) throws Exception 
	{
		String shellCommand = String.valueOf(paramWebSocketMessage.getPayload());
		
		//Check if Pod has been logged in
		if(shellCommand.startsWith("login;"))
		{
			if(execWatch == null)
			{
				log.info("[USER: " + user.getUserId() + "] Received login request, " + shellCommand);
				
				//Token format = login;clusterID;namespace;serviceName;podName
				String[] token = shellCommand.split(";");
			
				if(token.length >= 5)
				{
					try
					{
						execWatch =  k8sAPIService.execPod(Integer.parseInt(token[1]), token[2], token[4], new SimpleListener());
					}
					catch(KubernetesClientException e)
					{
						log.error("[USER: " + user.getUserId() + "] Error while exec into Pod " + token[4] + ", " + e.getMessage());
						
						// Bad Request, http2 missing
						if(e.getCode() == 400)
							paramWebSocketSession.close(CloseStatus.PROTOCOL_ERROR);
						
						//Pod not Found
						if(e.getCode() == 404)
							paramWebSocketSession.close(CloseStatus.POLICY_VIOLATION);
					}
					sendMsgThread = new Thread(new OutMess(execWatch.getOutput(), paramWebSocketSession));
					sendMsgThread.start();
				}
				else
				{
					log.error("[USER: " + user.getUserId() + "] Got an invalid login request");
					paramWebSocketSession.close(CloseStatus.BAD_DATA);
				}
			}
		}
		else
		{
			log.info("[USER: " + user.getUserId() + "] Received command: " + shellCommand);
			
			if(shellCommand.equalsIgnoreCase(Constants.KU8_CONSOLE_EXIT))
			{
				paramWebSocketSession.close(CloseStatus.NORMAL);
				return;
			}
			
			execWatch.getInput().write((shellCommand + "\n").getBytes());
			execWatch.getInput().flush();
		}
	}

	@Override
	public void handleTransportError(WebSocketSession paramWebSocketSession, Throwable paramThrowable) throws Exception 
	{
		paramWebSocketSession.close(CloseStatus.SERVER_ERROR);
		log.error("ws:// Handle transport Error, " + paramThrowable.getMessage());
	}

	@Override
	public boolean supportsPartialMessages() 
	{
		return false;
	}
}
