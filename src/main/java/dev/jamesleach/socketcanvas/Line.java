package dev.jamesleach.socketcanvas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.With;

import java.util.List;

/**
 * A line on the canvas, possibly being drawn.
 *
 * @author jim
 */
@Data
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
public class Line {
  @JsonProperty("d")
  private final String userId;
  @JsonProperty("n")
  private final int ownerLineNumber;
  @JsonProperty("z")
  private final int zIndex;
  @JsonProperty("p")
  private final List<Point> points;
  @JsonProperty("r")
  private final int brushRadius;
  @JsonProperty("c")
  private final String brushColor;
  @JsonProperty("f")
  @With
  private final boolean finished;
}
