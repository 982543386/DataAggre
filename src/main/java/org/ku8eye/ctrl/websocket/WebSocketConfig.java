package org.ku8eye.ctrl.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer
{

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry arg0)
	{
		arg0.addHandler(PodExecWebsocketHandler(), "/wswc").addInterceptors(new WebSocketHandshakeInterceptor());
	}

	@Bean
	public WebSocketHandler PodExecWebsocketHandler()
	{
		return new PodExecWebsocket();
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer)
	{
		configurer.enable();
	}

}
