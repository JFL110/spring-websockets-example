package org.jfl110.socketcanvas;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A line on the canvas, possibly being drawn.
 * 
 * Mutable
 * 
 * @author jim
 *
 */
public class Line {

	private final String userId;
	private final int ownerLineNumber;
	private final int zIndex;
	private final List<Point> points;
	private final int brushRadius;
	private final String brushColor;
	private boolean finished;

	public Line(
			@JsonProperty("d") String userId,
			@JsonProperty("n") int ownerLineNumber,
			@JsonProperty("z") int zIndex,
			@JsonProperty("p") List<Point> points,
			@JsonProperty("r") int brushRadius,
			@JsonProperty("c") String brushColor,
			@JsonProperty("f") boolean finished) {
		this.userId = userId;
		this.ownerLineNumber = ownerLineNumber;
		this.zIndex = zIndex;
		this.points = points;
		this.brushRadius = brushRadius;
		this.brushColor = brushColor;
		this.finished = finished;
	}


	@JsonProperty("f")
	public boolean isFinished() {
		return finished;
	}


	@JsonProperty("d")
	public String getUserId() {
		return userId;
	}


	@JsonProperty("n")
	public int getOwnerLineNumber() {
		return ownerLineNumber;
	}


	@JsonProperty("z")
	public int getzIndex() {
		return zIndex;
	}


	@JsonProperty("p")
	public List<Point> getPoints() {
		return points;
	}


	@JsonProperty("r")
	public int getBrushRadius() {
		return brushRadius;
	}


	@JsonProperty("c")
	public String getBrushColor() {
		return brushColor;
	}


	public void setFinished(boolean finished) {
		this.finished = finished;
	}
}
