package uk.ac.rhul.cs2810.service;

import java.util.List ;

import org.springframework.stereotype.Service;

import uk.ac.rhul.cs2810.model.Alert;
import uk.ac.rhul.cs2810.model.AlertType;
import uk.ac.rhul.cs2810.model.WebsocketEvent;
import uk.ac.rhul.cs2810.repository.AlertRepository;
import uk.ac.rhul.cs2810.handler.MyWebSocketHandler;

@Service
public class AlertService {
  private final MyWebSocketHandler wsHandler;

  private final AlertRepository alertRepository;

  public AlertService (MyWebSocketHandler wsHandler, AlertRepository alertRepository) {
    this.wsHandler = wsHandler;
    this.alertRepository = alertRepository;
  }

  public Alert callWaiter(Long tableId) {

    if (alertRepository.findByTableIdAndResolvedFalse(tableId).isPresent()) {
      throw new RuntimeException("Waiter already called for this table.");
    }

    Alert alert = new Alert();
    alert.setTableId(tableId);
    alert.setType(AlertType.WAITER_CALL);
    alert.setResolved(false);

    try {
      wsHandler.broadcast(jsonBuilder(WebsocketEvent.ALERT_CREATED, alert));
    } catch (Exception e) {
      System.out.println("WebSocket broadcast failed: " + e.getMessage());
    }
    return alertRepository.save(alert);
  }

  public List<Alert> getActiveAlerts() {
    return alertRepository.findByResolvedFalse();
  }

  public Alert resolveAlert(Long id) {

    Alert alert = alertRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Alert not found"));

    alert.setResolved(true);

    try {
      wsHandler.broadcast(jsonBuilder(WebsocketEvent.ALERT_RESOLVED, alert));
    } catch (Exception e) {
      System.out.println("WebSocket broadcast failed: " + e.getMessage());
    }
    return alertRepository.save(alert);
  }

  public List<Alert> getResolvedAlerts(Long tableId) {
    return alertRepository.findByTableIdAndResolvedTrue(tableId);
  }

  public String jsonBuilder(WebsocketEvent event, Alert alert){
    String json =
        """
        {"event" : "%s", "tableId" : %d, "resolved" : %b , "alertType" : "%s"}
        """
            .formatted(event, alert.getTableId(), alert.isResolved(), alert.getType());
    return json;
  }
}


