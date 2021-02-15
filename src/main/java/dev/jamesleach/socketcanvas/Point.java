package dev.jamesleach.socketcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * X,Y tuple
 * 
 * @author jim
 *
 */
@Data
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
public class Point {
	private final double x;
	private final double y;
}
