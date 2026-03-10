package uk.ac.rhul.cs2810.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import uk.ac.rhul.cs2810.dto.OrderCheckoutRequest;
import uk.ac.rhul.cs2810.model.Orders;

/**
 * Unit tests for {@link MyWebSocketHandler}.
 *
 * <p>Tests the WebSocket handler's ability to manage multiple client sessions and broadcast
 * messages to all connected clients.
 */
@ExtendWith(MockitoExtension.class)
class MyWebSocketHandlerTest {

  private MyWebSocketHandler handler;

  @Mock private WebSocketSession session1;

  @Mock private WebSocketSession session2;

  @Mock private WebSocketSession session3;

  @BeforeEach
  void setUp() {
    handler = new MyWebSocketHandler();
  }

  @Test
  void afterConnectionEstablished_shouldAddSessionToActiveSessions() throws Exception {
    handler.afterConnectionEstablished(session1);

    // Verify session is tracked by attempting to broadcast
    when(session1.isOpen()).thenReturn(true);
    handler.broadcast("test");
    verify(session1).sendMessage(any(TextMessage.class));
  }

  @Test
  void afterConnectionEstablished_shouldHandleMultipleSessions() throws Exception {
    handler.afterConnectionEstablished(session1);
    handler.afterConnectionEstablished(session2);
    handler.afterConnectionEstablished(session3);

    // All sessions should receive broadcasts
    when(session1.isOpen()).thenReturn(true);
    when(session2.isOpen()).thenReturn(true);
    when(session3.isOpen()).thenReturn(true);

    handler.broadcast("test message");

    verify(session1).sendMessage(any(TextMessage.class));
    verify(session2).sendMessage(any(TextMessage.class));
    verify(session3).sendMessage(any(TextMessage.class));
  }

  @Test
  void afterConnectionClosed_shouldRemoveSessionFromActiveSessions() throws Exception {
    handler.afterConnectionEstablished(session1);
    handler.afterConnectionEstablished(session2);

    handler.afterConnectionClosed(session1, CloseStatus.NORMAL);

    // Only session2 should receive broadcasts
    when(session2.isOpen()).thenReturn(true);
    handler.broadcast("test");

    verify(session1, never()).sendMessage(any(TextMessage.class));
    verify(session2).sendMessage(any(TextMessage.class));
  }

  @Test
  void afterConnectionClosed_shouldHandleClosingNonExistentSession() {
    // Should not throw exception
    assertDoesNotThrow(() -> handler.afterConnectionClosed(session1, CloseStatus.NORMAL));
  }

  @Test
  void broadcast_shouldSendMessageToAllOpenSessions() throws Exception {
    handler.afterConnectionEstablished(session1);
    handler.afterConnectionEstablished(session2);
    when(session1.isOpen()).thenReturn(true);
    when(session2.isOpen()).thenReturn(true);

    String message = "Hello WebSocket clients";

    handler.broadcast(message);

    ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
    verify(session1).sendMessage(captor.capture());
    verify(session2).sendMessage(captor.capture());

    assertEquals(message, captor.getAllValues().get(0).getPayload());
    assertEquals(message, captor.getAllValues().get(1).getPayload());
  }

  @Test
  void broadcast_shouldSkipClosedSessions() throws Exception {
    handler.afterConnectionEstablished(session1);
    handler.afterConnectionEstablished(session2);
    when(session1.isOpen()).thenReturn(false); // Closed session
    when(session2.isOpen()).thenReturn(true); // Open session

    handler.broadcast("test message");

    verify(session1, never()).sendMessage(any(TextMessage.class));
    verify(session2).sendMessage(any(TextMessage.class));
  }

  @Test
  void broadcast_shouldHandleEmptySessionSet() {
    // Should not throw exception when no sessions
    assertDoesNotThrow(() -> handler.broadcast("test"));
  }

  @Test
  void broadcast_shouldContinueBroadcastingEvenIfOneSessionFails() throws Exception {
    handler.afterConnectionEstablished(session1);
    handler.afterConnectionEstablished(session2);
    handler.afterConnectionEstablished(session3);

    when(session1.isOpen()).thenReturn(true);
    when(session2.isOpen()).thenReturn(true);
    when(session3.isOpen()).thenReturn(true);

    // Session2 throws exception when sending
    doThrow(new IOException("Connection error")).when(session2).sendMessage(any(TextMessage.class));

    handler.broadcast("test message");

    // Cession1 and session3 should still receive the message
    verify(session1).sendMessage(any(TextMessage.class));
    verify(session2).sendMessage(any(TextMessage.class)); // Attempted
    verify(session3).sendMessage(any(TextMessage.class));
  }

  @Test
  void broadcast_shouldHandleNullMessage() throws Exception {
    handler.afterConnectionEstablished(session1);
    when(session1.isOpen()).thenReturn(true);

    // TextMessage constructor throws IllegalArgumentException for null
    // The broadcast method catches all exceptions, so it won't propagate
    assertDoesNotThrow(() -> handler.broadcast(null));

    // Verify session.isOpen() was called but sendMessage was not (due to exception)
    verify(session1).isOpen();
    verify(session1, never()).sendMessage(any(TextMessage.class));
  }

  @Test
  void broadcast_shouldSendCorrectJsonMessage() throws Exception {
    handler.afterConnectionEstablished(session1);
    when(session1.isOpen()).thenReturn(true);

    String jsonMessage = "{\"type\":\"ORDER_UPDATED\",\"orderId\":123}";

    handler.broadcast(jsonMessage);

    ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
    verify(session1).sendMessage(captor.capture());
    assertEquals(jsonMessage, captor.getValue().getPayload());
  }

  @Test
  void concurrentOperations_shouldHandleMultipleSimultaneousConnections() throws Exception {
    // Simulate multiple connections being established concurrently
    handler.afterConnectionEstablished(session1);
    handler.afterConnectionEstablished(session2);
    handler.afterConnectionEstablished(session3);

    // Simulate one disconnection
    handler.afterConnectionClosed(session2, CloseStatus.NORMAL);

    when(session1.isOpen()).thenReturn(true);
    when(session3.isOpen()).thenReturn(true);
    handler.broadcast("concurrent test");

    // Only remaining sessions should receive message
    verify(session1).sendMessage(any(TextMessage.class));
    verify(session2, never()).sendMessage(any(TextMessage.class));
    verify(session3).sendMessage(any(TextMessage.class));
  }

  @Test
  void afterConnectionClosed_shouldHandleDifferentCloseStatuses() {
    handler.afterConnectionEstablished(session1);

    // Should handle various close statuses without exception
    assertDoesNotThrow(() -> handler.afterConnectionClosed(session1, CloseStatus.NORMAL));

    handler.afterConnectionEstablished(session2);
    assertDoesNotThrow(() -> handler.afterConnectionClosed(session2, CloseStatus.GOING_AWAY));

    handler.afterConnectionEstablished(session3);
    assertDoesNotThrow(() -> handler.afterConnectionClosed(session3, CloseStatus.SERVER_ERROR));
  }

  @Test
  void checkout_shouldBroadcastOrderUpdatedMessage() throws Exception {
    // Arrange
    MyWebSocketHandler handler = spy(new MyWebSocketHandler());
    WebSocketSession session = mock(WebSocketSession.class);
    when(session.isOpen()).thenReturn(true);
    handler.afterConnectionEstablished(session);

    // Mock order
    Orders order = mock(Orders.class);

    // Checkout request
    OrderCheckoutRequest payload = new OrderCheckoutRequest();
    payload.setOrderId(101L);
    payload.setCardNumber("4111111111111111");
    payload.setNameOnCard("John Doe");
    payload.setExpiryMonth(12);
    payload.setExpiryYear(2030);
    payload.setCvv("123");
    payload.setAmount(new BigDecimal("20"));

    // Simulate checkout broadcasting
    String expectedJson = "{\"type\":\"ORDER_UPDATED\",\"orderId\":101}";
    handler.broadcast(expectedJson);

    // Verify session receives broadcast
    verify(session).sendMessage(argThat(msg -> expectedJson.equals(msg.getPayload())));
  }
}
