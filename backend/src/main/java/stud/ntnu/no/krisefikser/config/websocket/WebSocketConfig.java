package stud.ntnu.no.krisefikser.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * Configuration class for WebSocket support in the application.
 * <p>
 * This class sets up the WebSocket endpoints, message broker, and
 * configures the handshake process.
 * It uses STOMP (Simple Text Oriented Messaging Protocol)
 * for message handling.
 * </p>
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final JwtHandshakeInterceptor jwtInterceptor;

  /**
   * Constructor for WebSocketConfig.
   *
   * @param jwtInterceptor the JWT handshake interceptor
   */
  public WebSocketConfig(JwtHandshakeInterceptor jwtInterceptor) {
    this.jwtInterceptor = jwtInterceptor;
  }

  /**
   * Registers the WebSocket endpoints and configures the handshake process.
   *
   * @param registry the registry for WebSocket endpoints
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
        .addEndpoint("/ws")
        .addInterceptors(jwtInterceptor)
        .setHandshakeHandler(new DefaultHandshakeHandler() {
          @Override
          protected Principal determineUser(ServerHttpRequest request,
                                            WebSocketHandler wsHandler,
                                            Map<String, Object> attributes) {
            String username = (String) attributes.get("username");
            if (username != null) {
              return () -> username;
            }
            return super.determineUser(request, wsHandler, attributes);
          }
        })
        .setAllowedOriginPatterns("*")
        .withSockJS();
  }

  /**
   * Configures the message broker for handling messages.
   * <p>
   * This method sets up a simple in-memory message broker
   * and defines the prefixes for application destinations.
   *
   * @param registry the registry for message broker configuration
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic");
    registry.setApplicationDestinationPrefixes("/app");
  }
}
