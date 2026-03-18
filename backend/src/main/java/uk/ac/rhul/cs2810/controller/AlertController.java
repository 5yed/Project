package uk.ac.rhul.cs2810.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.ac.rhul.cs2810.model.Alert;
import uk.ac.rhul.cs2810.model.AlertType;
import uk.ac.rhul.cs2810.repository.AlertRepository;
import uk.ac.rhul.cs2810.service.AlertService;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    @Autowired
    private AlertRepository alertRepository;
    private final AlertService alertService;

    public AlertController(AlertService alertService){
        this.alertService = alertService;
    }

    @PostMapping("/call-waiter")
    public Alert callWaiter(@RequestParam Long tableId) {
        return alertService.callWaiter(tableId);
    }

    @GetMapping("/active")
    public List<Alert> getActiveAlerts() {
        return alertService.getActiveAlerts();
    }

    @PatchMapping("/{id}/resolve")
    public Alert resolveAlert(@PathVariable Long id) {
        return alertService.resolveAlert(id);
    }

    @GetMapping("/resolved")
    public List<Alert> getResolvedAlerts(@RequestParam Long tableId) {
        return alertService.getResolvedAlerts(tableId);
    }

    @GetMapping("/resolved/all")
    public List<Alert> getAllResolvedAlerts() {
        return alertRepository.findByResolvedTrue();
    }
}