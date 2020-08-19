package org.jfl110.socketcanvas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint to return the status of the app.
 * 
 * @author jim
 *
 */
@RestController
public class AppStatusController {

	@Autowired private SimpUserRegistry simpUserRegistry;
	@Autowired private CanvasManager canvasManager;

	@RequestMapping("/")
	public String index() {
		return "App status ok. [" + simpUserRegistry.getUserCount() + "] connected users."
				+ " [" + canvasManager.getCanvasCount() + "] canvases."
				+ " [" + canvasManager.getRemovedCanvasCount() + "] canvases removed."
				+ " [" + canvasManager.getClearedCanvasCount() + "] canvases cleared."
				+ " [" + canvasManager.getTotalMessageCount() + "] total messages.";
	}
}
