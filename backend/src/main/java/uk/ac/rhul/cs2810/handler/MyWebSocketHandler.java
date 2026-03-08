package uk.ac.rhul.cs2810.handler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * A MyWebSocket handler that manages multiple client sessions and allows broadcasting text messages
 * to all connected clients.
 *
 * <p>This handler keeps track of active WebSocket sessions and provides a method to send a message
 * to all currently connected sessions.
 */
@Component
public class MyWebSocketHandler extends TextWebSocketHandler {

  /** A thread-safe set of all active WebSocket sessions. */
  private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

  /**
   * Called when a new WebSocket connection is established. Adds the session to the active sessions
   * set.
   *
   * @param session the newly established WebSocket session
   */
  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    sessions.add(session);
  }

  /**
   * Called when a WebSocket connection is closed. Removes the session from the active sessions set.
   *
   * @param session the WebSocket session that was closed
   * @param status the status of the closed connection
   */
  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    sessions.remove(session);
  }

  /**
   * Broadcasts a text message to all currently connected sessions. Only sessions that are open will
   * receive the message.
   *
   * @param message the text message to broadcast
   */
  public void broadcast(String message) {
    for (WebSocketSession session : sessions) {
      if (session.isOpen()) {
        try {
          session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
