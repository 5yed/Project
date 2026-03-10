package uk.ac.rhul.cs2810.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import uk.ac.rhul.cs2810.handler.MyWebSocketHandler;

/**
 * Unit tests for {@link WebSocketConfig}.
 *
 * <p>Tests the WebSocket configuration to ensure handlers are properly registered at the correct
 * endpoints with appropriate CORS settings.
 */
@ExtendWith(MockitoExtension.class)
class WebSocketConfigTest {

  @Mock private MyWebSocketHandler handler;

  @Mock private WebSocketHandlerRegistry registry;

  @Mock private WebSocketHandlerRegistration registration;

  private WebSocketConfig config;

  @BeforeEach
  void setUp() {
    config = new WebSocketConfig(handler);
  }

  @Test
  void constructor_shouldAcceptHandler() {
    WebSocketConfig newConfig = new WebSocketConfig(handler);

    assertNotNull(newConfig);
  }

  @Test
  void registerWebSocketHandlers_shouldRegisterHandlerAtCorrectEndpoint() {
    when(registry.addHandler(any(), anyString())).thenReturn(registration);
    when(registration.setAllowedOrigins(anyString())).thenReturn(registration);

    config.registerWebSocketHandlers(registry);

    ArgumentCaptor<String> endpointCaptor = ArgumentCaptor.forClass(String.class);
    verify(registry).addHandler(eq(handler), endpointCaptor.capture());
    assertEquals("/ws", endpointCaptor.getValue());
  }

  @Test
  void registerWebSocketHandlers_shouldAllowAllOrigins() {
    when(registry.addHandler(any(), anyString())).thenReturn(registration);
    when(registration.setAllowedOrigins(anyString())).thenReturn(registration);

    config.registerWebSocketHandlers(registry);

    ArgumentCaptor<String> originsCaptor = ArgumentCaptor.forClass(String.class);
    verify(registration).setAllowedOrigins(originsCaptor.capture());
    assertEquals("*", originsCaptor.getValue());
  }

  @Test
  void registerWebSocketHandlers_shouldRegisterCorrectHandler() {
    when(registry.addHandler(any(), anyString())).thenReturn(registration);
    when(registration.setAllowedOrigins(anyString())).thenReturn(registration);

    config.registerWebSocketHandlers(registry);

    ArgumentCaptor<MyWebSocketHandler> handlerCaptor =
        ArgumentCaptor.forClass(MyWebSocketHandler.class);
    verify(registry).addHandler(handlerCaptor.capture(), anyString());
    assertEquals(handler, handlerCaptor.getValue());
  }

  @Test
  void registerWebSocketHandlers_shouldChainRegistrationMethods() {
    when(registry.addHandler(any(), anyString())).thenReturn(registration);
    when(registration.setAllowedOrigins(anyString())).thenReturn(registration);

    config.registerWebSocketHandlers(registry);

    // verify the chain: addHandler -> setAllowedOrigins
    verify(registry).addHandler(handler, "/ws");
    verify(registration).setAllowedOrigins("*");
  }

  @Test
  void registerWebSocketHandlers_shouldOnlyRegisterOnce() {
    when(registry.addHandler(any(), anyString())).thenReturn(registration);
    when(registration.setAllowedOrigins(anyString())).thenReturn(registration);

    config.registerWebSocketHandlers(registry);

    // should only call addHandler once
    verify(registry, times(1)).addHandler(any(), anyString());
  }
}
