package dev.jamesleach.socketcanvas;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Service to hold (in memory) and apply changes to canvases.
 *
 * @author jim
 */
@Component
@Slf4j
@RequiredArgsConstructor
class CanvasManager {
  // Apply some very basic limits on size and number of canvases
  private static final long MAX_SIZE_PER_CANVAS = 1000000;
  private static final int MAX_CANVASES = 100;
  private static final long CLEANUP_CANVASES_TASK_INTERVAL_MS = 3 * 60 * 1000;

  // Message types
  private static final String START_LINE_TYPE = "s";
  private static final String CONTINUE_LINE_TYPE = "c";
  private static final String FINISH_LINE_TYPE = "f";

  private final NowProvider now;
  private final Lock cleanupLock = new ReentrantLock();

  // State
  private final Map<String, Canvas> canvases = Maps.newConcurrentMap();
  private final AtomicLong clearedCanvasesCount = new AtomicLong();
  private final AtomicLong totalMessageCount = new AtomicLong();
  private final AtomicLong removedCanvasCount = new AtomicLong();


  /**
   * Fetch or create a canvas
   */
  private Canvas getCanvas(String canvasId) {
    return canvases.computeIfAbsent(canvasId, x -> new Canvas(canvasId, now.get()));
  }


  @Scheduled(fixedDelay = CLEANUP_CANVASES_TASK_INTERVAL_MS)
  public void cleanupOldCanvases() {
    log.debug("Scheduled canvas cleanup.");

    cleanupLock.lock();
    try {
      int canvasesToRemove = canvases.size() - MAX_CANVASES;
      if (canvasesToRemove <= 0) {
        return;
      }

      log.info("Removing {} canvases.", canvasesToRemove);

      // Remove that many canvases, oldest first
      canvases.values()
        .stream()
        .map(c -> new CanvasWithFixedUpdateTime(c, c.lastUpdateTime.get()))
        .collect(Collectors.toList()) // Collect and fix all update times
        .stream()
        .sorted(Comparator.comparing(a -> a.lastUpdateTime)) // Oldest first
        .limit(canvasesToRemove)
        .map(c -> c.canvas.id)
        .collect(Collectors.toList())
        .forEach(i -> {
          canvases.remove(i);
          removedCanvasCount.incrementAndGet();
        });
    } finally {
      cleanupLock.unlock();
    }
  }


  /**
   * Completely clear a canvas
   */
  void clearCanvas(String id) {
    canvases.remove(id);
    clearedCanvasesCount.incrementAndGet();
  }


  /**
   * List all the lines in a canvas
   */
  ImmutableList<Line> getAllLines(String canvasId, Principal excludingUser) {
    return ImmutableList.copyOf(
      getCanvas(canvasId).lines.values()
        .stream()
        .filter(l -> !l.getUserId().equals(excludingUser.getName()))
        .collect(Collectors.toList()));
  }


  /**
   * Create or continue a line in a canvas
   */
  LineMessageOut handleLineMessage(String canvasId, Principal principal, LineMessage msg) {

    totalMessageCount.incrementAndGet();

    LineKey key = new LineKey(principal.getName(), msg.getClientLineNumber());
    Canvas canvas = getCanvas(canvasId);

    // Limit canvas size
    if (canvas.sizeEstimation.get() > MAX_SIZE_PER_CANVAS) {
      log.debug("Canvas is full [{}] - ignoring message", canvasId);
      return null;
    }

    // Update the last time this canvas was touched
    canvas.lastUpdateTime.set(now.get());

    if (START_LINE_TYPE.equals(msg.getType())) {
      canvas.lines.put(key, new Line(
        key.ownerId,
        key.ownerLineNumber,
        canvas.zIndex.incrementAndGet(),
        msg.getPoints().stream().filter(Objects::nonNull).collect(Collectors.toList()),
        msg.getBrushRadius(),
        msg.getBrushColor(),
        msg.getIsFinished()));

      canvas.sizeEstimation.addAndGet(1 + msg.getPoints().size());
    }

    Line line = canvas.lines.get(key);
    if (line == null) {
      // Line create message was lost or out of sequence
      return null;
    }

    if (CONTINUE_LINE_TYPE.equals(msg.getType())) {
      msg.getPoints().stream().filter(Objects::nonNull).forEach(line.getPoints()::add);
      canvas.sizeEstimation.addAndGet(msg.getPoints().size());
    } else if (FINISH_LINE_TYPE.equals(msg.getType())) {
      line = line.withFinished(true);
      canvas.lines.put(key, line);
    }

    return new LineMessageOut(
      principal.getName(),
      msg.getType(),
      msg.getClientLineNumber(),
      msg.getPoints(),
      msg.getBrushColor(),
      msg.getBrushRadius(),
      msg.getPointsIndexStart(),
      line.isFinished());
  }

  /**
   * A Canvas.
   */
  private static class Canvas {
    private final AtomicLong sizeEstimation = new AtomicLong();
    private final String id;
    private final Map<LineKey, Line> lines = Maps.newConcurrentMap();
    private final AtomicInteger zIndex = new AtomicInteger();
    private final AtomicReference<ZonedDateTime> lastUpdateTime;

    Canvas(String id, ZonedDateTime creationTime) {
      this.id = id;
      lastUpdateTime = new AtomicReference<>(creationTime);
    }
  }

  /**
   * Wrap a canvas with its last update time fixed so it won't be modified while
   * sorting canvases for deletion
   */
  @Data
  private static class CanvasWithFixedUpdateTime {
    private final Canvas canvas;
    private final ZonedDateTime lastUpdateTime;
  }

  /**
   * Hash key for a Line
   */
  @Data
  private static class LineKey {
    private final String ownerId;
    private final int ownerLineNumber;
  }

  // State reporting

  int getCanvasCount() {
    return canvases.size();
  }


  long getRemovedCanvasCount() {
    return removedCanvasCount.get();
  }


  long getTotalMessageCount() {
    return totalMessageCount.get();
  }


  long getClearedCanvasCount() {
    return clearedCanvasesCount.get();
  }

}
