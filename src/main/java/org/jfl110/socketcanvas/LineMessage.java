package org.jfl110.socketcanvas;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Message input format.
 * 
 * @author jim
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LineMessage {

	private final String type;
	private final int clientLineNumber;
	private final List<Point> points;
	private final String brushColor;
	private final int brushRadius;
	private final int pointsIndexStart;
	private final Boolean isFinished;

	@JsonCreator
	public LineMessage(
			@JsonProperty("t") String type,
			@JsonProperty("n") int clientLineNumber,
			@JsonProperty("p") List<Point> points,
			@JsonProperty("c") String brushColor,
			@JsonProperty("r") int brushRadius,
			@JsonProperty("i") int pointsIndexStart,
			@JsonProperty("f") Boolean isFinished) {
		this.type = type;
		this.clientLineNumber = clientLineNumber;
		this.points = points;
		this.brushColor = brushColor;
		this.brushRadius = brushRadius;
		this.pointsIndexStart = pointsIndexStart;
		this.isFinished = isFinished;
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
		return isFinished != null && isFinished;
	}


	@Override
	public String toString() {
		return "LineMessage [type=" + type + ", clientLineNumber=" + clientLineNumber + ", points=" + points + ", brushColor=" + brushColor
				+ ", brushRadius=" + brushRadius + ", pointsIndexStart=" + pointsIndexStart + ", isFinished=" + isFinished + "]";
	}

}
