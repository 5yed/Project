package uk.ac.rhul.cs2810.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "order_item")
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonIgnore
  @ManyToOne(optional = false)
  @JoinColumn(name = "order_id", nullable = false)
  private Orders order;

  @ManyToOne(optional = false)
  @JoinColumn(name = "menu_item_id", nullable = false)
  private MenuItem menuItem;

  @Column(nullable = false)
  private int quantity;

  @Column(name = "available_confirmed", nullable = false)
  private boolean availableConfirmed = false;

  @Column(name = "special_note")
  private String specialNote;

  protected OrderItem() {}

  public OrderItem(Orders order, MenuItem menuItem, int quantity) {
    this.order = order;
    this.menuItem = menuItem;
    this.quantity = quantity;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Orders getOrder() {
    return order;
  }

  public void setOrder(Orders order) {
    this.order = order;
  }

  public MenuItem getMenuItem() {
    return menuItem;
  }

  public void setMenuItem(MenuItem menuItem) {
    this.menuItem = menuItem;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public boolean isAvailableConfirmed() {
    return availableConfirmed;
  }

  public void setAvailableConfirmed(boolean availableConfirmed) {
    this.availableConfirmed = availableConfirmed;
  }

  public String getSpecialNote() {
    return specialNote;
  }

  public void setSpecialNote(String specialNote) {
    this.specialNote = specialNote;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof OrderItem))
      return false;
    OrderItem other = (OrderItem) o;
    return id != null && id.equals(other.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
