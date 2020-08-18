package org.jfl110.socketcanvas;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Message returned to all subscribers when a line is modified.
 * 
 * @author jim
 *
 */
public class LineMessageOut {

	private final String clientId;
	private final String type;
	private final int clientLineNumber;
	private final List<Point> points;
	private final String brushColor;
	private final int brushRadius;
	private final int pointsIndexStart;
	private final boolean isFinished;

	@JsonCreator
	public LineMessageOut(
			@JsonProperty("d") String clientId,
			@JsonProperty("t") String type,
			@JsonProperty("n") int clientLineNumber,
			@JsonProperty("p") List<Point> points,
			@JsonProperty("c") String brushColor,
			@JsonProperty("r") int brushRadius,
			@JsonProperty("i") int pointsIndexStart,
			@JsonProperty("f") boolean isFinished) {
		this.clientId = clientId;
		this.type = type;
		this.clientLineNumber = clientLineNumber;
		this.points = points;
		this.brushColor = brushColor;
		this.brushRadius = brushRadius;
		this.pointsIndexStart = pointsIndexStart;
		this.isFinished = isFinished;
	}


	@JsonProperty("d")
	public String getClientId() {
		return clientId;
	}


	@JsonProperty("t")
	public String getType() {
		return type;
	}


	@JsonProperty("n")
	public int getClientLineNumber() {
		return clientLineNumber;
	}


	@JsonProperty("p")
	public List<Point> getPoints() {
		return points;
	}


	@JsonProperty("c")
	public String getBrushColor() {
		return brushColor;
	}


	@JsonProperty("r")
	public int getRadius() {
		return brushRadius;
	}


	@JsonProperty("i")
	public int getPointsIndexStart() {
		return pointsIndexStart;
	}


	@JsonProperty("f")
	public boolean isFinished() {
		return isFinished;
	}
}
