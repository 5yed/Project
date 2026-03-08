package uk.ac.rhul.cs2810.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the OrderStatus enum. Verifies database string conversions and parsing logic.
 */
class OrderStatusTest {

  @Test
  void toDbValueShouldReturnLowercaseName() {
    assertEquals("placed", OrderStatus.PLACED.toDbValue());
    assertEquals("confirmed", OrderStatus.CONFIRMED.toDbValue());
    assertEquals("in_progress", OrderStatus.IN_PROGRESS.toDbValue());
    assertEquals("ready", OrderStatus.READY.toDbValue());
    assertEquals("delivered", OrderStatus.DELIVERED.toDbValue());
    assertEquals("cancelled", OrderStatus.CANCELLED.toDbValue());
    assertEquals("creating", OrderStatus.CREATING.toDbValue());
  }

  @Test
  void fromDbValueShouldParseLowercaseValues() {
    assertEquals(OrderStatus.PLACED, OrderStatus.fromDbValue("placed"));
    assertEquals(OrderStatus.CONFIRMED, OrderStatus.fromDbValue("confirmed"));
    assertEquals(OrderStatus.IN_PROGRESS, OrderStatus.fromDbValue("in_progress"));
  }

  @Test
  void conversionShouldBeSymmetric() {
    for (OrderStatus status : OrderStatus.values()) {
      String db = status.toDbValue();
      OrderStatus parsed = OrderStatus.fromDbValue(db);
      assertEquals(status, parsed);
    }
  }

  @Test
  void fromDbValueShouldThrowForInvalidValue() {
    assertThrows(IllegalArgumentException.class, () -> OrderStatus.fromDbValue("not_a_status"));
  }
}
