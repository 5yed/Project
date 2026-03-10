package uk.ac.rhul.cs2810.repository;

import org.springframework.data.repository.CrudRepository;
import uk.ac.rhul.cs2810.model.DietaryRestriction;

/**
 * DietaryRestriction repository interface.
 */

public interface DietaryRestrictionRepository extends CrudRepository<DietaryRestriction, Long> {

}
