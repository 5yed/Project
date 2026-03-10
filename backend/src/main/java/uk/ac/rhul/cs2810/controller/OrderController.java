package uk.ac.rhul.cs2810.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.rhul.cs2810.dto.OrderCheckoutRequest;
import uk.ac.rhul.cs2810.model.OrderStatus;
import uk.ac.rhul.cs2810.model.Orders;
import uk.ac.rhul.cs2810.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  public Orders createOrder(@RequestBody CreateOrderRequest payload) {
    return orderService.createOrder(payload.tableId());
  }

  @PostMapping("/{id}/checkout")
  public Orders checkoutOrder(@PathVariable Long id, @RequestBody OrderCheckoutRequest payload) {
    return orderService.checkoutOrder(id, payload);
  }

  @GetMapping("/{id}")
  public Orders getOrder(@PathVariable Long id) {
    return orderService.getOrder(id);
  }

  @GetMapping
  public List<Orders> getOrders(@RequestParam(required = false) Long tableId) {
    if (tableId == null) {
      return toList(orderService.getAllOrders());
    }
    return orderService.getOrdersForTable(tableId);
  }

  @GetMapping("/all")
  public List<Orders> getAllOrders() {
    return toList(orderService.getAllOrders());
  }

  @PatchMapping("/{id}/status")
  public Orders updateStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest payload) {
    return orderService.updateStatus(id, payload.status());
  }

  private static List<Orders> toList(Iterable<Orders> iterable) {
    List<Orders> list = new ArrayList<>();
    for (Orders o : iterable) list.add(o);
    return list;
  }

  public record CreateOrderRequest(Long tableId) {}

  public record UpdateStatusRequest(OrderStatus status) {}
}
