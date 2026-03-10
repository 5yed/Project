package uk.ac.rhul.cs2810.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.ac.rhul.cs2810.model.Alert;
import uk.ac.rhul.cs2810.model.AlertType;
import uk.ac.rhul.cs2810.repository.AlertRepository;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    @Autowired
    private AlertRepository alertRepository;

    @PostMapping("/call-waiter")
    public Alert callWaiter(@RequestParam Long tableId) {

        if (alertRepository.findByTableIdAndResolvedFalse(tableId).isPresent()) {
            throw new RuntimeException("Waiter already called for this table.");
        }

        Alert alert = new Alert();
        alert.setTableId(tableId);
        alert.setType(AlertType.WAITER_CALL);
        alert.setResolved(false);

        return alertRepository.save(alert);
    }

    @GetMapping("/active")
    public List<Alert> getActiveAlerts() {
        return alertRepository.findByResolvedFalse();
    }

    @PatchMapping("/{id}/resolve")
    public Alert resolveAlert(@PathVariable Long id) {

        Alert alert = alertRepository.findById(id).orElseThrow();

        alert.setResolved(true);

        return alertRepository.save(alert);
    }

    @GetMapping("/resolved")
    public List<Alert> getResolvedAlerts(@RequestParam Long tableId) {
        return alertRepository.findByTableIdAndResolvedTrue(tableId);
    }
}