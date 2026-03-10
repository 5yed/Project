package uk.ac.rhul.cs2810.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.model.OrderItem;
import uk.ac.rhul.cs2810.model.OrderStatus;
import uk.ac.rhul.cs2810.model.Orders;
import uk.ac.rhul.cs2810.repository.MenuItemRepository;
import uk.ac.rhul.cs2810.repository.OrderItemRepository;
import uk.ac.rhul.cs2810.repository.OrdersRepository;

/**
 * Unit tests for OrderItemService.
 *
 * Verifies order item creation behaviour including: successful creation, missing order, finalised
 * order, and missing menu item scenarios.
 */
class OrderItemServiceTest {

  @Mock
  private OrderItemRepository orderItemRepository;

  @Mock
  private OrdersRepository ordersRepository;

  @Mock
  private MenuItemRepository menuItemRepository;

  @InjectMocks
  private OrderItemService service;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Tests successful creation of an order item when order exists and is still in CREATING status.
   */
  @Test
  void createOrderItem_success() {

    Orders order = mock(Orders.class);
    MenuItem menuItem = mock(MenuItem.class);

    when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));
    when(order.getStatus()).thenReturn(OrderStatus.CREATING);

    when(menuItemRepository.findById(2L)).thenReturn(Optional.of(menuItem));

    // return the saved object
    when(orderItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    OrderItem result = service.createOrderItem(1L, 2L, 3);

    assertNotNull(result);
    verify(orderItemRepository).save(any(OrderItem.class));
  }

  /**
   * Tests exception thrown when order does not exist.
   */
  @Test
  void createOrderItem_orderNotFound() {

    when(ordersRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> service.createOrderItem(1L, 2L, 1));
  }

  /**
   * Tests exception thrown when order is finalized and cannot be modified.
   */
  @Test
  void createOrderItem_orderFinalized() {

    Orders order = mock(Orders.class);

    when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));
    when(order.getStatus()).thenReturn(OrderStatus.DELIVERED); // any non-CREATING value

    assertThrows(RuntimeException.class, () -> service.createOrderItem(1L, 2L, 1));
  }

  /**
   * Tests exception thrown when menu item does not exist.
   */
  @Test
  void createOrderItem_menuItemNotFound() {

    Orders order = mock(Orders.class);

    when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));
    when(order.getStatus()).thenReturn(OrderStatus.CREATING);

    when(menuItemRepository.findById(2L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> service.createOrderItem(1L, 2L, 1));
  }
}
