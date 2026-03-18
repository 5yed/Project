
package uk.ac.rhul.cs2810.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the OrderItem entity.
 *
 * <p>
 * This test verifies that setter methods correctly store values and that getter methods return the
 * expected values. It ensures the OrderItem object maintains internal state properly when populated
 * with related Orders and MenuItem instances.
 * </p>
 */
public class OrderItemTest {

  /**
   * Verifies that all fields set via setters are correctly stored and returned by their
   * corresponding getter methods.
   */
  @Test
  public void constructorStoresFields() {

    Orders order = new Orders();
    order.setRestaurantTableId(1L);

    MenuItem menuItem = new MenuItem();
    menuItem.setDescription("description");
    menuItem.setKcal(1.1);
    menuItem.setName("garlic");
    menuItem.setPrice(new BigDecimal("9.99"));

    OrderItem orderItem = new OrderItem();
    orderItem.setOrder(order);
    orderItem.setId(1L);
    orderItem.setMenuItem(menuItem);
    orderItem.setAvailableConfirmed(true);
    orderItem.setQuantity(2);
    orderItem.setSpecialNote("note");

    assertEquals(order, orderItem.getOrder());
    assertEquals(1L, orderItem.getId());
    assertEquals(menuItem, orderItem.getMenuItem());
    assertEquals(true, orderItem.isAvailableConfirmed());
    assertEquals(2, orderItem.getQuantity());
    assertEquals("note", orderItem.getSpecialNote());
  }
}
