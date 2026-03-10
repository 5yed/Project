package uk.ac.rhul.cs2810.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import uk.ac.rhul.cs2810.handler.MyWebSocketHandler;

/**
 * WebSocket configuration for the application.
 *
 * <p>This class registers a WebSocket handler at a specific endpoint and allows cross-origin
 * connections from any origin (for frontend clients).
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  /** The WebSocket handler responsible for managing sessions and messages. */
  private final MyWebSocketHandler handler;

  /**
   * Constructs the WebSocket configuration with the provided handler.
   *
   * @param handler the MyWebSocketHandler to handle WebSocket sessions
   */
  public WebSocketConfig(MyWebSocketHandler handler) {
    this.handler = handler;
  }

  /**
   * Registers the WebSocket handler at the "/ws" endpoint.
   *
   * <p>All origins are allowed to connect to this endpoint.
   *
   * @param registry the MyWebSocketHandlerRegistry used to register handlers
   */
  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(handler, "/ws").setAllowedOrigins("*"); // allow frontend connections
  }
}
