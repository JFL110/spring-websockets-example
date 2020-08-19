package org.jfl110.socketcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClearedCanvasMessageOut {

	private final String text;

	@JsonCreator
	public ClearedCanvasMessageOut(@JsonProperty("text") String text) {
		this.text = text;
	}


	@JsonProperty("text")
	public String getText() {
		return text;
	}

}
