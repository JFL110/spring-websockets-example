package dev.jamesleach.socketcanvas;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/**
 * Configuration for the websocket.
 *
 * @author jim
 */
@Configuration
@EnableWebSocketMessageBroker
class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }


  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    DefaultHandshakeHandler handshaker = new DefaultHandshakeHandler() {
      @Override
      protected Principal determineUser(@NonNull ServerHttpRequest request,
                                        @NonNull WebSocketHandler wsHandler,
                                        @NonNull Map<String, Object> attributes) {
        return new SimpleUser(UUID.randomUUID().toString());
      }
    };

    registry.addEndpoint("/canvas")
      .setAllowedOriginPatterns("http://*", "https://*")
      .setHandshakeHandler(handshaker);
    registry.addEndpoint("/canvas")
      .setAllowedOriginPatterns("http://*", "https://*")
      .setHandshakeHandler(handshaker)
      .withSockJS();
  }

  @Data
  static class SimpleUser implements Principal {
    private final String id;
    @Override
    public String getName() {
      return id;
    }
  }
}