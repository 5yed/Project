package uk.ac.rhul.cs2810.repository;

import org.springframework.data.repository.CrudRepository;
import uk.ac.rhul.cs2810.model.OrderItem;

/**
 * Table repository interface.
 */
public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {

}
