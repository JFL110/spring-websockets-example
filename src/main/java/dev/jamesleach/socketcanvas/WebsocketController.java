package dev.jamesleach.socketcanvas;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * Endpoint methods for the websocket.
 *
 * @author jim
 */
@Controller
@RequiredArgsConstructor
@Slf4j
class WebsocketController {
  private final CanvasManager canvasManager;

  @MessageMapping("/canvas/line/{canvasId}")
  @SendTo("/topic/canvas/{canvasId}")
  LineMessageOut line(@DestinationVariable("canvasId") String canvasId, LineMessage message, Principal principal) {
    log.debug("Got {}", message);
    return canvasManager.handleLineMessage(canvasId, principal, message);
  }


  @MessageMapping("/canvas/clear/{canvasId}")
  @SendTo("/topic/clear/{canvasId}")
  ClearedCanvasMessageOut clear(@DestinationVariable("canvasId") String canvasId, Principal principal) {
    log.info("Got clear {}", canvasId);
    canvasManager.clearCanvas(canvasId);
    return new ClearedCanvasMessageOut("cleared");
  }


  @MessageMapping("/canvas/init/{canvasId}")
  @SendToUser("/topic/init/{canvasId}")
  InitialCanvasMessage initalCanvas(@DestinationVariable("canvasId") String canvasId, Principal principal) {
    log.debug("Got initial canvas {} request from {}", canvasId, principal.getName());
    return new InitialCanvasMessage(principal.getName(), canvasManager.getAllLines(canvasId, principal));
  }
}