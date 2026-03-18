package uk.ac.rhul.cs2810.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.rhul.cs2810.handler.MyWebSocketHandler;
import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.model.OrderStatus;
import uk.ac.rhul.cs2810.model.Orders;
import uk.ac.rhul.cs2810.repository.MenuItemRepository;
import uk.ac.rhul.cs2810.repository.OrderItemRepository;
import uk.ac.rhul.cs2810.repository.OrdersRepository;

class OrderServiceTest {

  @Mock
  private OrdersRepository ordersRepository;

  @Mock
  private MenuItemRepository menuItemRepository;

  @Mock
  private OrderItemRepository orderItemRepository;

  @Mock
  private OrderItemService orderItemService;

  @Mock
  private MyWebSocketHandler wsHandler;

  @InjectMocks
  private OrderService orderService;

  private Orders order;

  private MenuItem menuItem;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    order = new Orders(1L, OrderStatus.CREATING);
    order.setId(1L);

    menuItem = new MenuItem();
    menuItem.setId(1L);
    menuItem.setDescription("description");
    menuItem.setKcal(1.1);
    menuItem.setName("garlic");
    menuItem.setPrice(new BigDecimal("9.99"));
  }

  // ===============================
  // createOrder
  // ===============================

  @Test
  void createOrder_shouldSaveOrderAndBroadcast() throws Exception {
    when(ordersRepository.save(any())).thenReturn(order);

    Orders created = orderService.createOrder(1L);

    assertEquals(OrderStatus.CREATING, created.getStatus());
    verify(ordersRepository).save(any());
    verify(wsHandler).broadcast(contains("ORDER_CREATED"));
  }

  // ===============================
  // getOrder
  // ===============================

  @Test
  void getOrder_shouldReturnOrder() {
    when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));

    Orders found = orderService.getOrder(1L);

    assertEquals(order, found);
  }

  @Test
  void getOrder_shouldThrowIfNotFound() {
    when(ordersRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResponseStatusException.class, () -> orderService.getOrder(1L));
  }

  // ===============================
  // getAllOrders
  // ===============================

  @Test
  void getAllOrders_shouldReturnAll() {
    when(ordersRepository.findAll()).thenReturn(List.of(order));

    Iterable<Orders> result = orderService.getAllOrders();

    assertTrue(result.iterator().hasNext());
    verify(ordersRepository).findAll();
  }

  // ===============================
  // getOrdersForTable
  // ===============================

  @Test
  void getOrdersForTable_shouldReturnOrders() {
    when(ordersRepository.findByRestaurantTableId(1L)).thenReturn(List.of(order));

    List<Orders> result = orderService.getOrdersForTable(1L);

    assertEquals(1, result.size());
  }

  // ===============================
  // updateStatus - valid transitions
  // ===============================

  @Test
  void updateStatus_shouldTransitionCorrectly() throws Exception {
    order.setStatus(OrderStatus.CREATING);

    when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));
    when(ordersRepository.save(any())).thenReturn(order);

    Orders updated = orderService.updateStatus(1L, OrderStatus.PLACED);

    assertEquals(OrderStatus.PLACED, updated.getStatus());
    verify(wsHandler).broadcast(contains("ORDER_UPDATED"));
  }

  @Test
  void updateStatus_shouldSetStartedAtWhenInProgress() {
    order.setStatus(OrderStatus.CONFIRMED);

    when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));
    when(ordersRepository.save(any())).thenReturn(order);

    orderService.updateStatus(1L, OrderStatus.IN_PROGRESS);

    assertNotNull(order.getStartedAt());
  }

  @Test
  void updateStatus_shouldSetAllTimestampsWhenDelivered() {
    order.setStatus(OrderStatus.READY);

    when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));
    when(ordersRepository.save(any())).thenReturn(order);

    orderService.updateStatus(1L, OrderStatus.DELIVERED);

    assertNotNull(order.getStartedAt());
    assertNotNull(order.getReadyAt());
    assertNotNull(order.getDeliveredAt());
  }

  // ===============================
  // invalid transitions
  // ===============================

  @Test
  void updateStatus_shouldThrowForInvalidTransition() {
    order.setStatus(OrderStatus.CREATING);

    when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));

    assertThrows(IllegalStateException.class,
        () -> orderService.updateStatus(1L, OrderStatus.READY));
  }

  @Test
  void updateStatus_shouldNotAllowUpdateIfDelivered() {
    order.setStatus(OrderStatus.DELIVERED);

    when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));

    assertThrows(IllegalStateException.class,
        () -> orderService.updateStatus(1L, OrderStatus.CANCELLED));
  }

  @Test
  void updateStatus_shouldAllowCancelFromAnyState() {
    order.setStatus(OrderStatus.CREATING);

    when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));
    when(ordersRepository.save(any())).thenReturn(order);

    Orders updated = orderService.updateStatus(1L, OrderStatus.CANCELLED);

    assertEquals(OrderStatus.CANCELLED, updated.getStatus());
    assertNotNull(order.getCancelledAt());
  }
}
