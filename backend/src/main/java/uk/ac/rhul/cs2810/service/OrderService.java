package uk.ac.rhul.cs2810.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.rhul.cs2810.dto.OrderCheckoutRequest;
import uk.ac.rhul.cs2810.handler.MyWebSocketHandler;
import uk.ac.rhul.cs2810.model.OrderStatus;
import uk.ac.rhul.cs2810.model.Orders;
import uk.ac.rhul.cs2810.repository.OrdersRepository;

@Service
public class OrderService {
  private final MyWebSocketHandler wsHandler;

  private final OrdersRepository ordersRepository;

  public OrderService(OrdersRepository ordersRepository, MyWebSocketHandler wsHandler) {
    this.ordersRepository = ordersRepository;
    this.wsHandler = wsHandler;
  }

  public Orders createOrder(Long tableId) {
    Orders order = ordersRepository.save(new Orders(tableId, OrderStatus.CREATING));

    try {
      String json =
          """
          {"type":"ORDER_CREATED","orderId":%d}
          """
              .formatted(order.getId());

      wsHandler.broadcast(json);
    } catch (Exception e) {
      System.out.println("WebSocket broadcast failed: " + e.getMessage());
    }

    return order;
  }

  public Orders getOrder(Long id) {
    return ordersRepository
        .findById(id)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + id));
  }

  public Iterable<Orders> getAllOrders() {
    return ordersRepository.findAll();
  }

  public List<Orders> getOrdersForTable(Long tableId) {
    return ordersRepository.findByRestaurantTableId(tableId);
  }

  public Orders updateStatus(Long orderId, OrderStatus newStatus) {
    Orders current = getOrder(orderId);

    if (current.getStatus() == OrderStatus.DELIVERED
        || current.getStatus() == OrderStatus.CANCELLED) {
      throw new IllegalStateException("Order is final and cannot be updated");
    }

    if (!isValidTransition(current.getStatus(), newStatus)) {
      throw new IllegalStateException(
          "Invalid transition: " + current.getStatus() + " -> " + newStatus);
    }

    Instant now = Instant.now();
    current.setStatus(newStatus);

    if (newStatus == OrderStatus.IN_PROGRESS) {
      if (current.getStartedAt() == null) current.setStartedAt(now);
    }

    if (newStatus == OrderStatus.READY) {
      if (current.getStartedAt() == null) current.setStartedAt(now);
      if (current.getReadyAt() == null) current.setReadyAt(now);
    }

    if (newStatus == OrderStatus.DELIVERED) {
      if (current.getStartedAt() == null) current.setStartedAt(now);
      if (current.getReadyAt() == null) current.setReadyAt(now);
      if (current.getDeliveredAt() == null) current.setDeliveredAt(now);
    }

    if (newStatus == OrderStatus.CANCELLED) {
      if (current.getCancelledAt() == null) current.setCancelledAt(now);
    }

    Orders updated = ordersRepository.save(current);

    try {
      String json =
          """
          {"type":"ORDER_UPDATED","orderId":%d}
          """
              .formatted(orderId);

      wsHandler.broadcast(json);
    } catch (Exception e) {
      System.out.println("WebSocket broadcast failed: " + e.getMessage());
    }

    return updated;
  }

  private boolean isValidTransition(OrderStatus from, OrderStatus to) {
    if (to == OrderStatus.CANCELLED) return true;

    return switch (from) {
      case CREATING -> to == OrderStatus.PLACED;
      case PLACED -> to == OrderStatus.CONFIRMED;
      case CONFIRMED -> to == OrderStatus.IN_PROGRESS;
      case IN_PROGRESS -> to == OrderStatus.READY;
      case READY -> to == OrderStatus.DELIVERED;
      default -> false;
    };
  }

  public Orders checkoutOrder(Long orderId, OrderCheckoutRequest payload) {
    validateCard(payload);

    Orders order = getOrder(orderId);

    if (order.getStatus() == OrderStatus.CANCELLED) {
      throw new IllegalStateException("Cannot pay for cancelled order");
    }

    if (payload.getAmount() == null || payload.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Payment amount must be positive");
    }

    BigDecimal currentPaid =
        order.getAmountPaid() == null ? BigDecimal.ZERO : order.getAmountPaid();

    BigDecimal newAmountPaid = currentPaid.add(payload.getAmount());

    if (newAmountPaid.compareTo(order.getAmountTotal()) > 0) {
      throw new IllegalArgumentException("Payment exceeds total amount");
    }

    order.setAmountPaid(newAmountPaid);

    Orders saved = ordersRepository.save(order);

    broadcastOrderUpdated(order.getId());

    return saved;
  }

  private void validateCard(OrderCheckoutRequest payload) {

    if (payload.getCardNumber() == null || !payload.getCardNumber().matches("\\d{16}")) {
      throw new IllegalArgumentException("Invalid card number");
    }

    if (!isValidLuhn(payload.getCardNumber())) {
      throw new IllegalArgumentException("Card failed validation check");
    }

    if (payload.getCvv() == null || !payload.getCvv().matches("\\d{3,4}")) {
      throw new IllegalArgumentException("Invalid CVV");
    }

    if (payload.getNameOnCard() == null || payload.getNameOnCard().isBlank()) {
      throw new IllegalArgumentException("Name on card required");
    }

    if (payload.getExpiryMonth() == null || payload.getExpiryYear() == null) {
      throw new IllegalArgumentException("Expiry date required");
    }

    if (payload.getExpiryMonth() < 1 || payload.getExpiryMonth() > 12) {
      throw new IllegalArgumentException("Invalid expiry month");
    }

    YearMonth expiry = YearMonth.of(payload.getExpiryYear(), payload.getExpiryMonth());

    if (expiry.isBefore(YearMonth.now())) {
      throw new IllegalArgumentException("Card expired");
    }
  }

  // Luhn algorithm (used by real cards)
  private boolean isValidLuhn(String cardNumber) {
    int sum = 0;
    boolean alternate = false;

    for (int i = cardNumber.length() - 1; i >= 0; i--) {
      int n = Integer.parseInt(cardNumber.substring(i, i + 1));

      if (alternate) {
        n *= 2;
        if (n > 9) {
          n -= 9;
        }
      }

      sum += n;
      alternate = !alternate;
    }

    return (sum % 10 == 0);
  }

  private void broadcastOrderUpdated(Long orderId) {
    try {
      String json =
          """
          {"type":"ORDER_UPDATED","orderId":%d}
          """
              .formatted(orderId);

      wsHandler.broadcast(json);
    } catch (Exception e) {
      System.out.println("WebSocket broadcast failed: " + e.getMessage());
    }
  }
}
