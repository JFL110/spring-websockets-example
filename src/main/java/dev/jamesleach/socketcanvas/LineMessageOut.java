package dev.jamesleach.socketcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Message returned to all subscribers when a line is modified.
 * 
 * @author jim
 *
 */
@Data
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
public class LineMessageOut {
	@JsonProperty("d")
	private final String clientId;
	@JsonProperty("t")
	private final String type;
	@JsonProperty("n")
	private final int clientLineNumber;
	@JsonProperty("p")
	private final List<Point> points;
	@JsonProperty("c")
	private final String brushColor;
	@JsonProperty("r")
	private final int brushRadius;
	@JsonProperty("i")
	private final int pointsIndexStart;
	@JsonProperty("f")
	private final boolean isFinished;
}
