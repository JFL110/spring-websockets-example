package org.jfl110.socketcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * X,Y tuple
 * 
 * @author jim
 *
 */
public class Point {

	private final double x;
	private final double y;

	@JsonCreator
	public Point(@JsonProperty("x") double x, @JsonProperty("y") double y) {
		this.x = x;
		this.y = y;
	}


	@JsonProperty("x")
	public double getX() {
		return x;
	}


	@JsonProperty("y")
	public double getY() {
		return y;
	}
}
