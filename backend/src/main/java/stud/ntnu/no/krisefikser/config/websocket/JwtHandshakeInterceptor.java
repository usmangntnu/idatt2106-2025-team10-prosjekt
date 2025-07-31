package stud.ntnu.no.krisefikser.config.websocket;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import stud.ntnu.no.krisefikser.config.JWTUtil;
import java.util.Map;

/**
 * Interceptor for handling WebSocket handshake requests.
 * <p>
 * This interceptor checks for a valid JWT token in the cookies of the request.
 * If the token is valid, it extracts the username and adds it to the WebSocket session attributes.
 * </p>
 */
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

  private final JWTUtil jwtUtil;
  private static final Logger logger = LogManager.getLogger(JwtHandshakeInterceptor.class);

  public JwtHandshakeInterceptor(JWTUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  /**
   * This method is called before the WebSocket handshake is completed.
   * It checks for a valid JWT token in the request cookies.
   *
   * @param request   the HTTP request
   * @param response  the HTTP response
   * @param wsHandler the WebSocket handler
   * @param attributes the attributes to be added to the WebSocket session
   * @return true if the handshake should proceed, false otherwise
   */
  @Override
  public boolean beforeHandshake(ServerHttpRequest request,
                                 ServerHttpResponse response,
                                 WebSocketHandler wsHandler,
                                 Map<String, Object> attributes) {
    if (!(request instanceof ServletServerHttpRequest servletReq)) {
      logger.error("Invalid request type for WebSocket handshake");
      return false;
    }
    Cookie[] cookies = servletReq.getServletRequest().getCookies();
    logger.debug("Cookies: {}", (Object) cookies);
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("auth-token".equals(cookie.getName())) {
          String token = cookie.getValue();
          try {
            if (jwtUtil.isTokenValid(token)) {
              String username = jwtUtil.extractUsername(token);
              attributes.put("username", username);
              logger.info("WebSocket handshake successful for user: {}", username);
              return true;
            }
          } catch (JwtException e) {
            logger.error("JWT validation failed: {}", e.getMessage());
            return false;
          }
        }
      }
    }
    logger.error("Missing or invalid auth-token cookie for URI {}", request.getURI());
    return false;
  }

  /**
   * This method is called after the WebSocket handshake is completed.
   * It can be used to perform any additional actions after the handshake.
   *
   * @param request   the HTTP request
   * @param response  the HTTP response
   * @param wsHandler the WebSocket handler
   * @param exception any exception that occurred during the handshake
   */
  @Override
  public void afterHandshake(ServerHttpRequest request,
                             ServerHttpResponse response,
                             WebSocketHandler wsHandler,
                             Exception exception) {
    logger.info("WebSocket handshake completed and was successful");
  }
}
