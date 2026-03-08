package uk.ac.rhul.cs2810.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.ac.rhul.cs2810.model.Orders;

@Repository
public interface OrdersRepository extends CrudRepository<Orders, Long> {
  List<Orders> findByRestaurantTableId(Long restaurantTableId);

  default Orders create(long orderId) {
    Orders order = new Orders();
    order.setId(orderId);
    return save(order);
  }
}
