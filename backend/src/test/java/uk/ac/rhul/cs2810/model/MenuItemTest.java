package uk.ac.rhul.cs2810.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the MenuItem entity. Ensures setters correctly store values and getters return
 * them.
 */
class MenuItemTest {

  @Test
  void settersAndGettersShouldStoreValues() {

    MenuItem item = new MenuItem();

    item.setName("Pizza");
    item.setDescription("Cheese pizza");
    item.setKcal(850.5);
    item.setPrice(9.99);

    assertEquals("Pizza", item.getName());
    assertEquals("Cheese pizza", item.getDescription());
    assertEquals(850.5, item.getKcal());
    assertEquals(9.99, item.getPrice());
  }

  @Test
  void defaultValuesShouldBeNullOrZero() {

    MenuItem item = new MenuItem();

    assertNull(item.getId()); // not persisted yet
    assertNull(item.getName());
    assertNull(item.getDescription());
    assertEquals(0.0, item.getKcal());
    assertEquals(0.0, item.getPrice());
  }
}
