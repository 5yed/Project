package uk.ac.rhul.cs2810.service;

import org.springframework.stereotype.Service;
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

  public OrderItemService(
      OrderItemRepository orderItemRepository,
      OrdersRepository ordersRepository,
      MenuItemRepository menuItemRepository) {
    this.orderItemRepository = orderItemRepository;
    this.ordersRepository = ordersRepository;
    this.menuItemRepository = menuItemRepository;
  }

  public OrderItem createOrderItem(Long orderId, Long menuItemId, int quantity) {
    Orders order =
        ordersRepository
            .findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

    if (!order.getStatus().equals(OrderStatus.CREATING)) {
      throw new RuntimeException("Cannot modify finalized order");
    }

    MenuItem menuItem =
        menuItemRepository
            .findById(menuItemId)
            .orElseThrow(() -> new RuntimeException("Menu item not found"));

    OrderItem orderItem = new OrderItem(order, menuItem, quantity);

    return orderItemRepository.save(orderItem);
  }
}
