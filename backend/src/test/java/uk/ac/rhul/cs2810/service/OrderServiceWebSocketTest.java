package uk.ac.rhul.cs2810.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.rhul.cs2810.dto.OrderCheckoutRequest;
import uk.ac.rhul.cs2810.handler.MyWebSocketHandler;
import uk.ac.rhul.cs2810.model.OrderStatus;
import uk.ac.rhul.cs2810.model.Orders;
import uk.ac.rhul.cs2810.repository.OrdersRepository;

/**
 * Unit tests for WebSocket integration in {@link OrderService}.
 *
 * <p>Tests the WebSocket broadcasting functionality for order creation and status updates.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceWebSocketTest {

  @Mock private OrdersRepository ordersRepository;

  @Mock private MyWebSocketHandler wsHandler;

  @Mock private Orders mockOrder;

  private OrderService orderService;

  @BeforeEach
  void setUp() {
    orderService = new OrderService(ordersRepository, wsHandler);
  }

  @Test
  void createOrder_shouldBroadcastOrderCreatedMessage() {
    Long tableId = 5L;
    Orders newOrder = new Orders(tableId, OrderStatus.CREATING);
    newOrder.setId(123L);

    when(ordersRepository.save(any(Orders.class))).thenReturn(newOrder);

    orderService.createOrder(tableId);

    ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
    verify(wsHandler).broadcast(messageCaptor.capture());

    String broadcastMessage = messageCaptor.getValue();
    assertNotNull(broadcastMessage);
    assertTrue(broadcastMessage.contains("\"type\":\"ORDER_CREATED\""));
    assertTrue(broadcastMessage.contains("\"orderId\":123"));
  }

  @Test
  void createOrder_shouldBroadcastCorrectJsonFormat() {
    Long tableId = 3L;
    Orders newOrder = new Orders(tableId, OrderStatus.CREATING);
    newOrder.setId(456L);

    when(ordersRepository.save(any(Orders.class))).thenReturn(newOrder);

    orderService.createOrder(tableId);

    ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
    verify(wsHandler).broadcast(messageCaptor.capture());

    String message = messageCaptor.getValue();
    String expectedJson = "{\"type\":\"ORDER_CREATED\",\"orderId\":456}";

    assertEquals(expectedJson.replaceAll("\\s+", ""), message.replaceAll("\\s+", ""));
  }

  @Test
  void createOrder_shouldHandleWebSocketBroadcastException() {
    Long tableId = 7L;
    Orders newOrder = new Orders(tableId, OrderStatus.CREATING);
    newOrder.setId(789L);

    when(ordersRepository.save(any(Orders.class))).thenReturn(newOrder);
    doThrow(new RuntimeException("WebSocket error")).when(wsHandler).broadcast(anyString());

    assertDoesNotThrow(() -> orderService.createOrder(tableId));

    Orders result = orderService.createOrder(tableId);
    assertNotNull(result);
  }

  @Test
  void createOrder_shouldBroadcastAfterSuccessfulSaving() {
    Long tableId = 2L;
    Orders newOrder = new Orders(tableId, OrderStatus.CREATING);
    newOrder.setId(100L);

    when(ordersRepository.save(any(Orders.class))).thenReturn(newOrder);

    orderService.createOrder(tableId);

    var inOrder = inOrder(ordersRepository, wsHandler);
    inOrder.verify(ordersRepository).save(any(Orders.class));
    inOrder.verify(wsHandler).broadcast(anyString());
  }

  @Test
  void createOrder_shouldCallWebSocketHandlerOnlyOnce() {
    Long tableId = 8L;
    Orders newOrder = new Orders(tableId, OrderStatus.CREATING);
    newOrder.setId(321L);

    when(ordersRepository.save(any(Orders.class))).thenReturn(newOrder);

    orderService.createOrder(tableId);

    verify(wsHandler, times(1)).broadcast(anyString());
  }

  @Test
  void createOrder_shouldHandleNullWebSocketHandler() {
    OrderService serviceWithNullWs = new OrderService(ordersRepository, null);
    Long tableId = 9L;
    Orders newOrder = new Orders(tableId, OrderStatus.CREATING);
    newOrder.setId(555L);

    when(ordersRepository.save(any(Orders.class))).thenReturn(newOrder);

    Orders result = assertDoesNotThrow(() -> serviceWithNullWs.createOrder(tableId));

    assertNotNull(result);
    verify(ordersRepository).save(any(Orders.class));
  }

  @Test
  void createOrder_messageFormat_shouldBeValidJson() {
    Long tableId = 4L;
    Orders newOrder = new Orders(tableId, OrderStatus.CREATING);
    newOrder.setId(888L);

    when(ordersRepository.save(any(Orders.class))).thenReturn(newOrder);

    orderService.createOrder(tableId);

    ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
    verify(wsHandler).broadcast(messageCaptor.capture());

    String message = messageCaptor.getValue().trim();

    assertTrue(message.startsWith("{"));
    assertTrue(message.endsWith("}"));
    assertTrue(message.contains("\"type\""));
    assertTrue(message.contains("\"orderId\""));
    assertTrue(message.contains("ORDER_CREATED"));
  }

  @Test
  void updateStatus_shouldBroadcastOrderUpdateMessage() {
    Long orderId = 123L;
    OrderStatus newStatus = OrderStatus.CONFIRMED;

    when(ordersRepository.findById(orderId)).thenReturn(java.util.Optional.of(mockOrder));
    when(mockOrder.getStatus()).thenReturn(OrderStatus.PLACED);
    when(ordersRepository.save(any(Orders.class))).thenReturn(mockOrder);

    orderService.updateStatus(orderId, newStatus);

    ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
    verify(wsHandler).broadcast(messageCaptor.capture());

    String broadcastMessage = messageCaptor.getValue();
    assertNotNull(broadcastMessage);
    assertTrue(broadcastMessage.contains("\"type\":\"ORDER_UPDATED\""));
    assertTrue(broadcastMessage.contains("\"orderId\":123"));
  }

  @Test
  void updateStatus_shouldBroadcastCorrectJsonFormat() {
    Long orderId = 456L;
    OrderStatus newStatus = OrderStatus.READY;

    when(ordersRepository.findById(orderId)).thenReturn(java.util.Optional.of(mockOrder));
    when(mockOrder.getStatus()).thenReturn(OrderStatus.IN_PROGRESS);
    when(ordersRepository.save(any(Orders.class))).thenReturn(mockOrder);

    orderService.updateStatus(orderId, newStatus);

    ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
    verify(wsHandler).broadcast(messageCaptor.capture());

    String message = messageCaptor.getValue();
    String expectedJson = "{\"type\":\"ORDER_UPDATED\",\"orderId\":456}";

    assertEquals(expectedJson.replaceAll("\\s+", ""), message.replaceAll("\\s+", ""));
  }

  @Test
  void updateStatus_shouldSaveOrderBeforeBroadcasting() {
    Long orderId = 789L;
    OrderStatus newStatus = OrderStatus.DELIVERED;

    when(ordersRepository.findById(orderId)).thenReturn(java.util.Optional.of(mockOrder));
    when(mockOrder.getStatus()).thenReturn(OrderStatus.READY);
    when(ordersRepository.save(any(Orders.class))).thenReturn(mockOrder);

    orderService.updateStatus(orderId, newStatus);

    var inOrder = inOrder(ordersRepository, wsHandler);
    inOrder.verify(ordersRepository).save(any(Orders.class));
    inOrder.verify(wsHandler).broadcast(anyString());
  }

  @Test
  void updateStatus_shouldBroadcastAfterSuccessfulSave() {
    Long orderId = 999L;
    OrderStatus newStatus = OrderStatus.CANCELLED;

    when(ordersRepository.findById(orderId)).thenReturn(java.util.Optional.of(mockOrder));
    when(mockOrder.getStatus()).thenReturn(OrderStatus.PLACED);
    when(ordersRepository.save(any(Orders.class))).thenReturn(mockOrder);

    Orders result = orderService.updateStatus(orderId, newStatus);

    assertNotNull(result);
    verify(wsHandler).broadcast(anyString());
  }

  @Test
  void updateStatus_shouldHandleWebSocketBroadcastException() {
    Long orderId = 555L;
    OrderStatus newStatus = OrderStatus.IN_PROGRESS;

    when(ordersRepository.findById(orderId)).thenReturn(java.util.Optional.of(mockOrder));
    when(mockOrder.getStatus()).thenReturn(OrderStatus.CONFIRMED);
    when(ordersRepository.save(any(Orders.class))).thenReturn(mockOrder);
    doThrow(new RuntimeException("WebSocket error")).when(wsHandler).broadcast(anyString());

    assertDoesNotThrow(() -> orderService.updateStatus(orderId, newStatus));

    Orders result = orderService.updateStatus(orderId, newStatus);
    assertNotNull(result);
  }

  @Test
  void updateStatus_shouldBroadcastForAllValidTransitions() {
    Long orderId = 1000L;

    OrderStatus[][] validTransitions = {
      {OrderStatus.CREATING, OrderStatus.PLACED},
      {OrderStatus.PLACED, OrderStatus.CONFIRMED},
      {OrderStatus.CONFIRMED, OrderStatus.IN_PROGRESS},
      {OrderStatus.IN_PROGRESS, OrderStatus.READY},
      {OrderStatus.READY, OrderStatus.DELIVERED}
    };

    for (OrderStatus[] transition : validTransitions) {
      reset(wsHandler, ordersRepository, mockOrder);
      OrderStatus fromStatus = transition[0];
      OrderStatus toStatus = transition[1];

      when(ordersRepository.findById(orderId)).thenReturn(java.util.Optional.of(mockOrder));
      when(mockOrder.getStatus()).thenReturn(fromStatus);
      when(ordersRepository.save(any(Orders.class))).thenReturn(mockOrder);

      orderService.updateStatus(orderId, toStatus);

      verify(wsHandler).broadcast(anyString());
    }
  }

  @Test
  void updateStatus_shouldBroadcastWithDifferentOrderIds() {
    OrderStatus newStatus = OrderStatus.CONFIRMED;
    Long[] orderIds = {1L, 42L, 999L, 123456L};

    for (Long orderId : orderIds) {
      reset(wsHandler, ordersRepository, mockOrder);
      when(ordersRepository.findById(orderId)).thenReturn(java.util.Optional.of(mockOrder));
      when(mockOrder.getStatus()).thenReturn(OrderStatus.PLACED);
      when(ordersRepository.save(any(Orders.class))).thenReturn(mockOrder);

      orderService.updateStatus(orderId, newStatus);

      ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
      verify(wsHandler).broadcast(messageCaptor.capture());

      String message = messageCaptor.getValue();
      assertTrue(message.contains("\"orderId\":" + orderId));
    }
  }

  @Test
  void updateStatus_shouldCallWebSocketHandlerOnlyOnce() {
    Long orderId = 321L;
    OrderStatus newStatus = OrderStatus.READY;

    when(ordersRepository.findById(orderId)).thenReturn(java.util.Optional.of(mockOrder));
    when(mockOrder.getStatus()).thenReturn(OrderStatus.IN_PROGRESS);
    when(ordersRepository.save(any(Orders.class))).thenReturn(mockOrder);

    orderService.updateStatus(orderId, newStatus);

    verify(wsHandler, times(1)).broadcast(anyString());
  }

  @Test
  void updateStatus_shouldNotBroadcastIfStatusUpdateFails() {
    Long orderId = 777L;
    OrderStatus newStatus = OrderStatus.DELIVERED;

    when(ordersRepository.findById(orderId)).thenReturn(java.util.Optional.of(mockOrder));
    when(mockOrder.getStatus()).thenReturn(OrderStatus.DELIVERED);

    assertThrows(IllegalStateException.class, () -> orderService.updateStatus(orderId, newStatus));

    verify(wsHandler, never()).broadcast(anyString());
  }

  @Test
  void updateStatus_shouldNotBroadcastOnInvalidTransition() {
    Long orderId = 888L;
    OrderStatus newStatus = OrderStatus.DELIVERED;

    when(ordersRepository.findById(orderId)).thenReturn(java.util.Optional.of(mockOrder));
    when(mockOrder.getStatus()).thenReturn(OrderStatus.PLACED);

    assertThrows(IllegalStateException.class, () -> orderService.updateStatus(orderId, newStatus));

    verify(wsHandler, never()).broadcast(anyString());
  }

  @Test
  void updateStatus_messageFormat_shouldBeValidJson() {
    Long orderId = 888L;
    OrderStatus newStatus = OrderStatus.CONFIRMED;

    when(ordersRepository.findById(orderId)).thenReturn(java.util.Optional.of(mockOrder));
    when(mockOrder.getStatus()).thenReturn(OrderStatus.PLACED);
    when(ordersRepository.save(any(Orders.class))).thenReturn(mockOrder);

    orderService.updateStatus(orderId, newStatus);

    ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
    verify(wsHandler).broadcast(messageCaptor.capture());

    String message = messageCaptor.getValue().trim();

    assertTrue(message.startsWith("{"));
    assertTrue(message.endsWith("}"));
    assertTrue(message.contains("\"type\""));
    assertTrue(message.contains("\"orderId\""));
    assertTrue(message.contains("ORDER_UPDATED"));
  }

  @Test
  void updateStatus_shouldBroadcastAfterSettingTimestamps() {
    Long orderId = 999L;
    OrderStatus newStatus = OrderStatus.READY;

    when(ordersRepository.findById(orderId)).thenReturn(java.util.Optional.of(mockOrder));
    when(mockOrder.getStatus()).thenReturn(OrderStatus.IN_PROGRESS);
    when(mockOrder.getStartedAt()).thenReturn(Instant.now());
    when(ordersRepository.save(any(Orders.class))).thenReturn(mockOrder);

    orderService.updateStatus(orderId, newStatus);

    var inOrder = inOrder(mockOrder, ordersRepository, wsHandler);
    inOrder.verify(mockOrder).setStatus(newStatus);
    inOrder.verify(mockOrder).setReadyAt(any(Instant.class));
    inOrder.verify(ordersRepository).save(any(Orders.class));
    inOrder.verify(wsHandler).broadcast(anyString());
  }

  @Test
  void updateStatus_shouldHandleNullWebSocketHandler() {
    OrderService serviceWithNullWs = new OrderService(ordersRepository, null);
    Long orderId = 777L;
    OrderStatus newStatus = OrderStatus.DELIVERED;

    when(ordersRepository.findById(orderId)).thenReturn(java.util.Optional.of(mockOrder));
    when(mockOrder.getStatus()).thenReturn(OrderStatus.READY);
    when(ordersRepository.save(any(Orders.class))).thenReturn(mockOrder);

    Orders result = assertDoesNotThrow(() -> serviceWithNullWs.updateStatus(orderId, newStatus));

    assertNotNull(result);
    verify(ordersRepository).save(any(Orders.class));
  }

  @Test
  void updateStatus_shouldContinueAfterBroadcastFailure() {
    Long orderId = 555L;
    OrderStatus newStatus = OrderStatus.IN_PROGRESS;

    when(ordersRepository.findById(orderId)).thenReturn(java.util.Optional.of(mockOrder));
    when(mockOrder.getStatus()).thenReturn(OrderStatus.CONFIRMED);
    when(ordersRepository.save(any(Orders.class))).thenReturn(mockOrder);
    doThrow(new RuntimeException("Connection lost")).when(wsHandler).broadcast(anyString());

    Orders result = orderService.updateStatus(orderId, newStatus);

    assertNotNull(result);
    verify(wsHandler).broadcast(anyString());
  }

  @Test
  void checkoutOrder_shouldUpdateAmountPaidAndBroadcast() {
    // Arrange
    Long orderId = 101L;
    Orders order = new Orders(orderId, OrderStatus.PLACED);
    order.setId(orderId); // explicitly set the ID
    order.setAmountPaid(BigDecimal.ZERO);
    order.setAmountTotal(new BigDecimal("50"));

    when(ordersRepository.findById(orderId)).thenReturn(java.util.Optional.of(order));
    when(ordersRepository.save(order)).thenReturn(order);

    OrderCheckoutRequest payload = new OrderCheckoutRequest();
    payload.setAmount(new BigDecimal("50"));
    payload.setCardNumber("4111111111111111");
    payload.setCvv("123");
    payload.setNameOnCard("Test User");
    payload.setExpiryMonth(12);
    payload.setExpiryYear(2030);

    // Act
    Orders result = orderService.checkoutOrder(orderId, payload);

    // Assert
    assertNotNull(result);
    assertEquals(new BigDecimal("50"), order.getAmountPaid());
    verify(ordersRepository).save(order);

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(wsHandler).broadcast(captor.capture());

    String message = captor.getValue();
    assertTrue(message.contains("\"type\":\"ORDER_UPDATED\""));
    assertTrue(message.contains("\"orderId\":101"));
  }

  @Test
  void checkoutOrder_shouldThrowIfOverpaying() {
    Long orderId = 102L;
    Orders order = new Orders(orderId, OrderStatus.PLACED);
    order.setAmountPaid(BigDecimal.ZERO);
    order.setAmountTotal(new BigDecimal("10"));

    when(ordersRepository.findById(orderId)).thenReturn(java.util.Optional.of(order));

    OrderCheckoutRequest payload = new OrderCheckoutRequest();
    payload.setOrderId(orderId);
    payload.setAmount(new BigDecimal("20")); // exceeds total
    payload.setCardNumber("4111111111111111");
    payload.setCvv("123");
    payload.setNameOnCard("Test User");
    payload.setExpiryMonth(12);
    payload.setExpiryYear(2030);

    assertThrows(
        IllegalArgumentException.class, () -> orderService.checkoutOrder(orderId, payload));

    verify(wsHandler, never()).broadcast(anyString());
  }

  @Test
  void checkoutOrder_shouldValidateCardAndThrowOnInvalidCard() {
    OrderCheckoutRequest payload = new OrderCheckoutRequest();
    payload.setOrderId(103L);
    payload.setAmount(new BigDecimal("10"));
    payload.setCardNumber("1234567890123456"); // invalid Luhn
    payload.setCvv("123");
    payload.setNameOnCard("Test User");
    payload.setExpiryMonth(12);
    payload.setExpiryYear(2030);

    // Should throw on invalid card before even fetching the order
    assertThrows(
        IllegalArgumentException.class,
        () -> orderService.checkoutOrder(payload.getOrderId(), payload));

    verify(ordersRepository, never()).save(any());
    verify(wsHandler, never()).broadcast(anyString());
  }

  @Test
  void checkoutOrder_shouldThrowIfCancelled() {
    Long orderId = 104L;
    Orders order = mock(Orders.class);
    when(order.getStatus()).thenReturn(OrderStatus.CANCELLED);
    when(ordersRepository.findById(orderId)).thenReturn(java.util.Optional.of(order));

    OrderCheckoutRequest payload = new OrderCheckoutRequest();
    payload.setOrderId(orderId);
    payload.setAmount(new BigDecimal("10"));
    payload.setCardNumber("4111111111111111");
    payload.setCvv("123");
    payload.setNameOnCard("Test User");
    payload.setExpiryMonth(12);
    payload.setExpiryYear(2030);

    assertThrows(IllegalStateException.class, () -> orderService.checkoutOrder(orderId, payload));

    verify(wsHandler, never()).broadcast(anyString());
  }

  @Test
  void checkoutOrder_shouldHandleWebSocketBroadcastException() {
    Long orderId = 105L;
    Orders order = new Orders(orderId, OrderStatus.PLACED);
    order.setId(orderId); // Explicitly set Id
    order.setAmountPaid(BigDecimal.ZERO);
    order.setAmountTotal(new BigDecimal("30"));

    when(ordersRepository.findById(orderId)).thenReturn(java.util.Optional.of(order));
    when(ordersRepository.save(order)).thenReturn(order);

    // Simulate WebSocket failing
    doThrow(new RuntimeException("WebSocket failed")).when(wsHandler).broadcast(anyString());

    OrderCheckoutRequest payload = new OrderCheckoutRequest();
    payload.setAmount(new BigDecimal("30"));
    payload.setCardNumber("4111111111111111");
    payload.setCvv("123");
    payload.setNameOnCard("Test User");
    payload.setExpiryMonth(12);
    payload.setExpiryYear(2030);

    Orders result = assertDoesNotThrow(() -> orderService.checkoutOrder(orderId, payload));

    assertNotNull(result);
    assertEquals(new BigDecimal("30"), order.getAmountPaid());
    verify(ordersRepository).save(order);
    verify(wsHandler).broadcast(anyString());
  }
}
