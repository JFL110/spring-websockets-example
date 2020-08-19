package org.jfl110.socketcanvas;

import java.security.Principal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

/**
 * Endpoint methods for the websocket.
 * 
 * @author jim
 *
 */
@Controller
public class WebsocketController {

	private static final Logger LOG = LogManager.getLogger(CanvasManager.class);

	@Autowired private CanvasManager canvasManager;

	@MessageMapping("/canvas/line/{canvasId}")
	@SendTo("/topic/canvas/{canvasId}")
	public LineMessageOut line(@DestinationVariable("canvasId") String canvasId, LineMessage message, Principal principal) throws Exception {
		LOG.atDebug().log("Got " + message);
		return canvasManager.handleLineMessage(canvasId, principal, message);
	}


	@MessageMapping("/canvas/clear/{canvasId}")
	@SendTo("/topic/clear/{canvasId}")
	public ClearedCanvasMessageOut clear(@DestinationVariable("canvasId") String canvasId, Principal principal) throws Exception {
		LOG.atInfo().log("Got clear " + canvasId);
		canvasManager.clearCanvas(canvasId);
		return new ClearedCanvasMessageOut("cleared");
	}


	@MessageMapping("/canvas/init/{canvasId}")
	@SendToUser("/topic/init/{canvasId}")
	public InitialCanvasMessage initalCanvas(@DestinationVariable("canvasId") String canvasId, Principal principal) throws Exception {
		LOG.atDebug().log("Got initial canvas [" + canvasId + "] request from " + principal.getName());
		return new InitialCanvasMessage(principal.getName(), canvasManager.getAllLines(canvasId, principal));
	}
}