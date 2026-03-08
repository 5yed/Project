package uk.ac.rhul.cs2810.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Creates Table entity.
 */

@Entity
@Table(name = "restaurantTable")
public class RestaurantTable {
  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false, name = "table_number")
  public int tableNum;

  /**
   * A class constructor.
   * 
   * @param id table's id.
   * @param tableNum table number.
   */
  public RestaurantTable(int tableNum) {
    this.tableNum = tableNum;
  }

  /**
   * Empty constructor.
   */
  public RestaurantTable() {}

  /**
   * Sets Id.
   * 
   * @param id
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Sets table number.
   * 
   * @param tableNum
   */
  public void setTableNum(int tableNum) {
    this.tableNum = tableNum;
  }

  /**
   * Gets the id.
   * 
   * @return id
   */
  public Long getId() {
    return this.id;
  }

  /**
   * Gets tableNum.
   *
   * @return tableNum
   */
  public int getTableNum() {
    return this.tableNum;
  }

}
