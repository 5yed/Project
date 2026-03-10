package uk.ac.rhul.cs2810.repository;

import org.springframework.data.repository.CrudRepository;
import uk.ac.rhul.cs2810.model.Alert;

import java.util.List;
import java.util.Optional;

public interface AlertRepository extends CrudRepository<Alert, Long> {

    List<Alert> findByResolvedFalse();

    Optional<Alert> findByTableIdAndResolvedFalse(Long tableId);

    List<Alert> findByTableIdAndResolvedTrue(Long tableId);
}