package uk.ac.rhul.cs2810.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.rhul.cs2810.controller.OrderController.UpdateStatusRequest;
import uk.ac.rhul.cs2810.dto.OrderCheckoutRequest;
import uk.ac.rhul.cs2810.model.OrderStatus;
import uk.ac.rhul.cs2810.model.Orders;
import uk.ac.rhul.cs2810.service.OrderService;

/**
 * Unit tests for {@link OrderController}.
 *
 * <p>Tests the controller layer, verifying that it properly delegates to the service layer.
 * WebSocket broadcasting is now handled in OrderService, so these tests focus on the controller's
 * responsibility of HTTP request/response handling.
 */
@ExtendWith(MockitoExtension.class)
class OrderControllerWebSocketTest {

  @Mock private OrderService orderService;

  @Mock private Orders mockOrder;

  private OrderController controller;

  @BeforeEach
  void setUp() {
    controller = new OrderController(orderService);
  }

  @Test
  void updateStatus_shouldDelegateToService() {
    Long orderId = 123L;
    UpdateStatusRequest request = new UpdateStatusRequest(OrderStatus.CONFIRMED);
    when(orderService.updateStatus(orderId, OrderStatus.CONFIRMED)).thenReturn(mockOrder);

    Orders result = controller.updateStatus(orderId, request);

    verify(orderService).updateStatus(orderId, OrderStatus.CONFIRMED);
    assertEquals(mockOrder, result);
  }

  @Test
  void updateStatus_shouldReturnUpdatedOrder() {
    Long orderId = 100L;
    UpdateStatusRequest request = new UpdateStatusRequest(OrderStatus.CONFIRMED);
    when(orderService.updateStatus(orderId, OrderStatus.CONFIRMED)).thenReturn(mockOrder);

    Orders result = controller.updateStatus(orderId, request);

    assertEquals(mockOrder, result);
  }

  @Test
  void updateStatus_shouldPassCorrectParametersToService() {
    Long orderId = 456L;
    OrderStatus status = OrderStatus.READY;
    UpdateStatusRequest request = new UpdateStatusRequest(status);
    when(orderService.updateStatus(orderId, status)).thenReturn(mockOrder);

    controller.updateStatus(orderId, request);

    verify(orderService).updateStatus(orderId, status);
  }

  @Test
  void updateStatus_shouldHandleAllOrderStatuses() {
    // Test each status enum value
    OrderStatus[] statuses = OrderStatus.values();
    Long orderId = 1000L;

    for (OrderStatus status : statuses) {
      reset(orderService); // Reset mocks for each iteration
      UpdateStatusRequest request = new UpdateStatusRequest(status);
      when(orderService.updateStatus(orderId, status)).thenReturn(mockOrder);

      controller.updateStatus(orderId, request);

      verify(orderService).updateStatus(orderId, status);
    }
  }

  @Test
  void updateStatus_shouldReturnNullIfServiceReturnsNull() {
    Long orderId = 999L;
    UpdateStatusRequest request = new UpdateStatusRequest(OrderStatus.CANCELLED);
    when(orderService.updateStatus(orderId, OrderStatus.CANCELLED)).thenReturn(null);

    Orders result = controller.updateStatus(orderId, request);

    assertNull(result);
    verify(orderService).updateStatus(orderId, OrderStatus.CANCELLED);
  }

  @Test
  void updateStatus_shouldPropagateServiceExceptions() {
    Long orderId = 555L;
    UpdateStatusRequest request = new UpdateStatusRequest(OrderStatus.IN_PROGRESS);
    when(orderService.updateStatus(orderId, OrderStatus.IN_PROGRESS))
        .thenThrow(new IllegalStateException("Invalid transition"));

    assertThrows(IllegalStateException.class, () -> controller.updateStatus(orderId, request));
    verify(orderService).updateStatus(orderId, OrderStatus.IN_PROGRESS);
  }

  @Test
  void updateStatus_shouldCallServiceOnlyOnce() {
    Long orderId = 321L;
    UpdateStatusRequest request = new UpdateStatusRequest(OrderStatus.READY);
    when(orderService.updateStatus(orderId, OrderStatus.READY)).thenReturn(mockOrder);

    controller.updateStatus(orderId, request);

    verify(orderService, times(1)).updateStatus(orderId, OrderStatus.READY);
  }

  @Test
  void updateStatus_shouldHandleDifferentOrderIds() {
    UpdateStatusRequest request = new UpdateStatusRequest(OrderStatus.CONFIRMED);

    // Test with various order IDs
    Long[] orderIds = {1L, 42L, 999L, 123456L};

    for (Long orderId : orderIds) {
      reset(orderService);
      when(orderService.updateStatus(orderId, OrderStatus.CONFIRMED)).thenReturn(mockOrder);

      controller.updateStatus(orderId, request);

      verify(orderService).updateStatus(orderId, OrderStatus.CONFIRMED);
    }
  }

  @Test
  void checkoutOrder_shouldDelegateToService() {
    Long orderId = 101L;

    // Create a mock checkout request
    OrderCheckoutRequest payload = new OrderCheckoutRequest();
    payload.setOrderId(orderId);
    payload.setCardNumber("4111111111111111");
    payload.setNameOnCard("John Doe");
    payload.setExpiryMonth(12);
    payload.setExpiryYear(2030);
    payload.setCvv("123");
    payload.setAmount(new BigDecimal("15.50"));

    // Mock service response
    when(orderService.checkoutOrder(orderId, payload)).thenReturn(mockOrder);

    // Call controller
    Orders result = controller.checkoutOrder(orderId, payload);

    // Verify service was called correctly
    verify(orderService).checkoutOrder(orderId, payload);

    // Verify returned order is as expected
    assertEquals(mockOrder, result);
  }

  @Test
  void checkoutOrder_shouldPropagateServiceExceptions() {
    Long orderId = 202L;

    OrderCheckoutRequest payload = new OrderCheckoutRequest();
    payload.setOrderId(orderId);
    payload.setCardNumber("4111111111111111");
    payload.setNameOnCard("John Doe");
    payload.setExpiryMonth(12);
    payload.setExpiryYear(2030);
    payload.setCvv("123");
    payload.setAmount(new BigDecimal("20"));

    // Mock service to throw exception
    when(orderService.checkoutOrder(orderId, payload))
        .thenThrow(new IllegalStateException("Payment failed"));

    // Verify controller propagates the exception
    assertThrows(IllegalStateException.class, () -> controller.checkoutOrder(orderId, payload));
    verify(orderService).checkoutOrder(orderId, payload);
  }
}
