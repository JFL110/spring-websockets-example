package org.jfl110.socketcanvas;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

/**
 * Configuration for the websocket.
 * 
 * @author jim
 *
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic");
		config.setApplicationDestinationPrefixes("/app");
	}


	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		DefaultHandshakeHandler handshaker = new DefaultHandshakeHandler() {
			@Override
			protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
				return new SimpleUser(UUID.randomUUID().toString());
			}
		};

		registry.addEndpoint("/canvas").setAllowedOrigins("*").setHandshakeHandler(handshaker);
		registry.addEndpoint("/canvas").setAllowedOrigins("*").setHandshakeHandler(handshaker).withSockJS();
	}

	static class SimpleUser implements Principal {

		private final String id;

		SimpleUser(String id) {
			this.id = id;
		}


		@Override
		public String getName() {
			return id;
		}
	}
}