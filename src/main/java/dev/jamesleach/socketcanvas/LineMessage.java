package dev.jamesleach.socketcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Message input format.
 * 
 * @author jim
 *
 */
@Data
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
@JsonIgnoreProperties(ignoreUnknown = true)
public class LineMessage {
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
	private final Boolean isFinished;
}
