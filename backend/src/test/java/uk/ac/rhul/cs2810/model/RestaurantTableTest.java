package uk.ac.rhul.cs2810.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RestaurantTableTest {

  @Test
  public void ConstructorStoresFields() {
    RestaurantTable table = new RestaurantTable(4);
    assertEquals(4, table.getTableNum());

    RestaurantTable table_1 = new RestaurantTable();
    table_1.setId(1L);
    table_1.setTableNum(5);
    assertEquals(1L, table_1.getId());
    assertEquals(5, table_1.getTableNum());
  }
}
