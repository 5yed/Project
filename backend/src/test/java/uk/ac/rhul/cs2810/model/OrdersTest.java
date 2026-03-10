package uk.ac.rhul.cs2810.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class OrdersTest {

  @Test
  void constructorShouldSetTableAndStatus() {
    Orders order = new Orders(5L, OrderStatus.READY);

    assertEquals(5L, order.getRestaurantTableId());
    assertEquals(OrderStatus.READY, order.getStatus());
  }

  @Test
  void onCreateShouldPopulateDefaultsWhenNull() {

    Orders order = new Orders(3L, OrderStatus.CREATING);

    // explicitly null values to simulate pre-persist state
    order.setAmountTotal(null);
    order.setAmountPaid(null);
    order.setOrderedAt(null);

    order.onCreate();

    assertNotNull(order.getOrderedAt());
    assertEquals(OrderStatus.CREATING, order.getStatus());
    assertEquals(BigDecimal.ZERO, order.getAmountTotal());
    assertEquals(BigDecimal.ZERO, order.getAmountPaid());
  }

  @Test
  void onCreateShouldNotOverrideExistingValues() {

    Orders order = new Orders(3L, OrderStatus.READY);

    Instant now = Instant.now();
    order.setOrderedAt(now);
    order.setAmountTotal(BigDecimal.TEN);
    order.setAmountPaid(BigDecimal.ONE);

    order.onCreate();

    assertEquals(now, order.getOrderedAt());
    assertEquals(OrderStatus.READY, order.getStatus());
    assertEquals(BigDecimal.TEN, order.getAmountTotal());
    assertEquals(BigDecimal.ONE, order.getAmountPaid());
  }
}
