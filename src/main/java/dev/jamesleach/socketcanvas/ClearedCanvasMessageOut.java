package dev.jamesleach.socketcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
public class ClearedCanvasMessageOut {
	private final String text;
}
