package uk.ac.rhul.cs2810.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;


@Entity
@Table(name = "orders")
public class Orders {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "table_id", nullable = false)
  private Long restaurantTableId;

  @Convert(converter = uk.ac.rhul.cs2810.config.OrderStatusConverter.class)
  @Column(name = "status", nullable = false)
  private OrderStatus status;

  @Column(name = "amount_total", nullable = false)
  private BigDecimal amountTotal = BigDecimal.ZERO;

  @Column(name = "amount_paid", nullable = false)
  private BigDecimal amountPaid = BigDecimal.ZERO;

  @Column(name = "ordered_at")
  private Instant orderedAt;

  @Column(name = "started_at")
  private Instant startedAt;

  @Column(name = "ready_at")
  private Instant readyAt;

  @Column(name = "delivered_at")
  private Instant deliveredAt;

  @Column(name = "cancelled_at")
  private Instant cancelledAt;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> orderItems = new ArrayList<>();

  public Orders() {}

  public Orders(Long restaurantTableId, OrderStatus status) {
    this.restaurantTableId = restaurantTableId;
    this.status = status;
    this.amountTotal = BigDecimal.ZERO;
    this.amountPaid = BigDecimal.ZERO;
  }

  @PrePersist
  public void onCreate() {

    if (status == null)
      status = OrderStatus.PLACED;
    if (amountTotal == null)
      amountTotal = BigDecimal.ZERO;
    if (amountPaid == null)
      amountPaid = BigDecimal.ZERO;
    if (orderedAt == null)
      orderedAt = Instant.now();
  }

  /**
   * Calculates total amout to pay.
   * 
   * @return total as BigDecimal.
   */
  public BigDecimal calculateTotal() {
    BigDecimal total = BigDecimal.ZERO;

    for (OrderItem item : orderItems) {
      BigDecimal price = item.getMenuItem().getPrice();
      BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
      total = total.add(price.multiply(quantity));
    }

    this.amountTotal = total;
    return total;
  }

  public Long getId() {
    return id;
  }

  public Long getRestaurantTableId() {
    return restaurantTableId;
  }

  public void setRestaurantTableId(Long restaurantTableId) {
    this.restaurantTableId = restaurantTableId;
  }

  public Long getTableId() {
    return restaurantTableId;
  }

  public void setTableId(Long tableId) {
    this.restaurantTableId = tableId;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public BigDecimal getAmountTotal() {
    return amountTotal;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setAmountTotal(BigDecimal amountTotal) {
    this.amountTotal = amountTotal;
  }

  public BigDecimal getAmountPaid() {
    return amountPaid;
  }

  public void setAmountPaid(BigDecimal amountPaid) {
    this.amountPaid = amountPaid;
  }

  public Instant getOrderedAt() {
    return orderedAt;
  }

  public void setOrderedAt(Instant orderedAt) {
    this.orderedAt = orderedAt;
  }

  public Instant getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(Instant startedAt) {
    this.startedAt = startedAt;
  }

  public Instant getReadyAt() {
    return readyAt;
  }

  public List<OrderItem> getOrderItems() {
    return orderItems;
  }

  public void setReadyAt(Instant readyAt) {
    this.readyAt = readyAt;
  }

  public Instant getDeliveredAt() {
    return deliveredAt;
  }

  public void setDeliveredAt(Instant deliveredAt) {
    this.deliveredAt = deliveredAt;
  }

  public Instant getCancelledAt() {
    return cancelledAt;
  }

  public void setCancelledAt(Instant cancelledAt) {
    this.cancelledAt = cancelledAt;
  }
}
