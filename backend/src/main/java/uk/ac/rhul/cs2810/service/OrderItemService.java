package uk.ac.rhul.cs2810.service;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.model.OrderItem;
import uk.ac.rhul.cs2810.model.OrderStatus;
import uk.ac.rhul.cs2810.model.Orders;
import uk.ac.rhul.cs2810.repository.MenuItemRepository;
import uk.ac.rhul.cs2810.repository.OrderItemRepository;
import uk.ac.rhul.cs2810.repository.OrdersRepository;

@Service
public class OrderItemService {

  private final OrderItemRepository orderItemRepository;
  private final OrdersRepository ordersRepository;
  private final MenuItemRepository menuItemRepository;

  public OrderItemService(OrderItemRepository orderItemRepository,
      OrdersRepository ordersRepository, MenuItemRepository menuItemRepository) {
    this.orderItemRepository = orderItemRepository;
    this.ordersRepository = ordersRepository;
    this.menuItemRepository = menuItemRepository;
  }

  public OrderItem createOrderItem(Long orderId, Long menuItemId, int quantity) {

    Orders order = ordersRepository.findById(orderId)
        .orElseThrow(() -> new RuntimeException("Order not found"));

    if (!order.getStatus().equals(OrderStatus.CREATING)) {
      throw new RuntimeException("Cannot modify finalized order");
    }

    MenuItem menuItem = menuItemRepository.findById(menuItemId)
        .orElseThrow(() -> new RuntimeException("Menu item not found"));

    // Check if item already exists
    for (OrderItem item : order.getOrderItems()) {
      if (item.getMenuItem().getId().equals(menuItemId)) {
        item.setQuantity(item.getQuantity() + quantity);
        order.calculateTotal();
        return orderItemRepository.save(item);
      }
    }

    // Create new item
    OrderItem orderItem = new OrderItem(order, menuItem, quantity);

    order.getOrderItems().add(orderItem); // maintain relationship

    OrderItem savedItem = orderItemRepository.save(orderItem);

    order.calculateTotal();
    ordersRepository.save(order);

    return savedItem;
  }


  @Transactional
  public void deleteOrderItem(Long orderItemId) {

    OrderItem orderItem = orderItemRepository.findById(orderItemId)
        .orElseThrow(() -> new RuntimeException("Order item not found"));

    Orders order = orderItem.getOrder();

    if (order.getStatus().equals(OrderStatus.DELIVERED)) {
      throw new RuntimeException("Can't modify delivered order");
    }

    if (orderItem.getQuantity() > 1) {
      orderItem.setQuantity(orderItem.getQuantity() - 1);
    } else {
      order.getOrderItems().remove(orderItem); // orphanRemoval handles delete
    }

    order.calculateTotal();
  }
}
