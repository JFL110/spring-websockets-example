package org.jfl110.socketcanvas;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

/**
 * Message to describe complete canvas state to new users.
 * 
 * @author jim
 *
 */
public class InitialCanvasMessage {

	private final String userId;
	private final ImmutableList<Line> lines;

	@JsonCreator
	public InitialCanvasMessage(
			@JsonProperty("userId") String userId,
			@JsonProperty("lines") List<Line> lines) {
		this.userId = userId;
		this.lines = ImmutableList.copyOf(lines);
	}


	@JsonProperty("lines")
	public ImmutableList<Line> getLines() {
		return lines;
	}


	@JsonProperty("userId")
	public String getUserId() {
		return userId;
	}


	@Override
	public String toString() {
		return "InitialCanvasMessage [userId=" + userId + ", lines=" + lines + "]";
	}
}
