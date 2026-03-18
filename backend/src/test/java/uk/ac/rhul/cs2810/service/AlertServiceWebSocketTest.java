package uk.ac.rhul.cs2810.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.rhul.cs2810.handler.MyWebSocketHandler;
import uk.ac.rhul.cs2810.model.Alert;
import uk.ac.rhul.cs2810.model.AlertType;
import uk.ac.rhul.cs2810.repository.AlertRepository;

@ExtendWith(MockitoExtension.class)
class AlertServiceWebSocketTest {

  @Mock private AlertRepository alertRepository;

  @Mock private MyWebSocketHandler wsHandler;

  @Mock private Alert mockAlert;

  private AlertService alertService;

  @BeforeEach
  void setUp() {alertService = new AlertService(wsHandler, alertRepository);}

  @Test
  void callWaiter_shouldBroadcastAlertCreatedMessage() {
    Long tableId = 5L;

    Alert savedAlert = new Alert();
    savedAlert.setTableId(tableId);
    savedAlert.setType(AlertType.WAITER_CALL);
    savedAlert.setResolved(false);

    when(alertRepository.findByTableIdAndResolvedFalse(tableId)).thenReturn(Optional.empty());
    when(alertRepository.save(any(Alert.class))).thenReturn(savedAlert);

    alertService.callWaiter(tableId);

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(wsHandler).broadcast(captor.capture());

    String message = captor.getValue().trim();
    assertTrue(message.contains("ALERT_CREATED"));
    assertTrue(message.contains("\"tableId\" : 5"));
    assertTrue(message.contains("\"resolved\" : false"));
    assertTrue(message.contains("\"alertType\" : \"WAITER_CALL\""));
  }

  @Test
  void callWaiter_shouldNotBroadcastIfActiveAlertAlreadyExists() {
    Long tableId = 5L;

    Alert existingAlert = new Alert();
    existingAlert.setTableId(tableId);
    existingAlert.setType(AlertType.WAITER_CALL);
    existingAlert.setResolved(false);

    when(alertRepository.findByTableIdAndResolvedFalse(tableId)).thenReturn(Optional.of(existingAlert));

    assertThrows(RuntimeException.class, () -> alertService.callWaiter(tableId));

    verify(wsHandler, never()).broadcast(anyString());
    verify(alertRepository, never()).save(any(Alert.class));
  }

  @Test
  void callWaiter_shouldContinueIfBroadcastFails() {
    Long tableId = 7L;

    Alert savedAlert = new Alert();
    savedAlert.setTableId(tableId);
    savedAlert.setType(AlertType.WAITER_CALL);
    savedAlert.setResolved(false);

    when(alertRepository.findByTableIdAndResolvedFalse(tableId)).thenReturn(Optional.empty());
    when(alertRepository.save(any(Alert.class))).thenReturn(savedAlert);
    doThrow(new RuntimeException("WebSocket error")).when(wsHandler).broadcast(anyString());

    Alert result = assertDoesNotThrow(() -> alertService.callWaiter(tableId));

    assertNotNull(result);
    verify(alertRepository).save(any(Alert.class));
    verify(wsHandler).broadcast(anyString());
  }

  @Test
  void resolveAlert_shouldBroadcastAlertResolvedMessage() {
    Long alertId = 10L;

    when(alertRepository.findById(alertId)).thenReturn(Optional.of(mockAlert));
    when(mockAlert.getTableId()).thenReturn(3L);
    when(mockAlert.isResolved()).thenReturn(true);
    when(mockAlert.getType()).thenReturn(AlertType.WAITER_CALL);
    when(alertRepository.save(mockAlert)).thenReturn(mockAlert);

    alertService.resolveAlert(alertId);

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(wsHandler).broadcast(captor.capture());

    String message = captor.getValue();
    assertTrue(message.contains("ALERT_RESOLVED"));
    assertTrue(message.contains("\"tableId\" : 3"));
    assertTrue(message.contains("\"resolved\" : true"));
    assertTrue(message.contains("\"alertType\" : \"WAITER_CALL\""));
  }

  @Test
  void resolveAlert_shouldContinueIfBroadcastFails() {
    Long alertId = 11L;

    when(alertRepository.findById(alertId)).thenReturn(Optional.of(mockAlert));
    when(mockAlert.getTableId()).thenReturn(4L);
    when(mockAlert.isResolved()).thenReturn(true);
    when(mockAlert.getType()).thenReturn(AlertType.WAITER_CALL);
    when(alertRepository.save(mockAlert)).thenReturn(mockAlert);
    doThrow(new RuntimeException("WebSocket error")).when(wsHandler).broadcast(anyString());

    Alert result = assertDoesNotThrow(() -> alertService.resolveAlert(alertId));

    assertNotNull(result);
    verify(alertRepository).save(mockAlert);
    verify(wsHandler).broadcast(anyString());
  }

  @Test
  void resolveAlert_shouldThrowIfAlertNotFound() {
    Long alertId = 99L;

    when(alertRepository.findById(alertId)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class, () -> alertService.resolveAlert(alertId));

    assertEquals("Alert not found", exception.getMessage());
    verify(wsHandler, never()).broadcast(anyString());
    verify(alertRepository, never()).save(any(Alert.class));
  }

}