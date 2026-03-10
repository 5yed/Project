package uk.ac.rhul.cs2810.repository;

import org.springframework.data.repository.CrudRepository;
import uk.ac.rhul.cs2810.model.RestaurantTable;

/**
 * Table repository interface.
 */
public interface RestaurantTableRepository extends CrudRepository<RestaurantTable, Long> {

}
