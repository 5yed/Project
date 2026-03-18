package uk.ac.rhul.cs2810.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2810.dto.CreateOrderItemRequest;
import uk.ac.rhul.cs2810.model.OrderItem;
import uk.ac.rhul.cs2810.service.OrderItemService;

@RestController
@RequestMapping("/api/orderItems")
public class OrderItemController {

  private final OrderItemService orderItemService;

  public OrderItemController(OrderItemService orderItemService) {
    this.orderItemService = orderItemService;
  }

  @PostMapping
  public OrderItem createOrderItem(@RequestBody CreateOrderItemRequest request) {
    return orderItemService.createOrderItem(request.getOrderId(), request.getMenuItemId(),
        request.getQuantity());
  }
  // /api/orders/id/orderItem/id

  @DeleteMapping("/{id}")
  public void deleteOrderItem(@PathVariable Long id) {
    orderItemService.deleteOrderItem(id);
  }
}
