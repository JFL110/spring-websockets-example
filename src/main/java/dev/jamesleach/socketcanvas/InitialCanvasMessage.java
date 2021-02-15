package dev.jamesleach.socketcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Message to describe complete canvas state to new users.
 * 
 * @author jim
 *
 */
@Data
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
public class InitialCanvasMessage {
	private final String userId;
	private final List<Line> lines;
}
