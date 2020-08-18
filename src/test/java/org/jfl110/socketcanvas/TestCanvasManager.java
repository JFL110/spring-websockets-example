package org.jfl110.socketcanvas;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

public class TestCanvasManager {

	private final ZonedDateTime startingTime = ZonedDateTime.of(2019, 1, 1, 1, 1, 1, 1, ZoneId.of("UTC"));
	private final NowProvider now = mock(NowProvider.class);
	private final CanvasManager canvasManager = new CanvasManager(now);

	/**
	 * Tests that upon cleanup, oldest canvases are removed
	 */
	@Test
	public void testCleanup() {

		// Create a lot of canvases
		Map<String, ZonedDateTime> canvasCreationTimes = Maps.newConcurrentMap();
		IntStream.range(0, 1500)
				.forEach(i -> {
					ZonedDateTime canvasTime = startingTime.plusSeconds(i);
					String canvasId = UUID.randomUUID().toString();
					canvasCreationTimes.put(canvasId, canvasTime);
					when(now.get()).thenReturn(canvasTime);
					canvasManager.handleLineMessage(
							canvasId,
							() -> "some-user",
							new LineMessage("s", 0, ImmutableList.of(
									new Point(10, 10), new Point(12, 12)), "#fff", 10, 0, true));
				});

		assertEquals(1500, canvasManager.getCanvasCount());

		// Cleanup
		canvasManager.cleanupOldCanvases();
		assertEquals(100, canvasManager.getCanvasCount());
		assertEquals(1400, canvasManager.getRemovedCanvasCount());
		assertEquals(1500, canvasManager.getTotalMessageCount());

		// Oldest canvases should have been removed
		ZonedDateTime deletionCutoff = startingTime.plusSeconds(1400);
		canvasCreationTimes
				.forEach((id, time) -> {
					int numberOfCanvasLines = canvasManager.getAllLines(id, () -> "none").size();

					// Canvases
					assertEquals(time.isBefore(deletionCutoff) ? 0 : 1,
							numberOfCanvasLines,
							"time " + Duration.between(startingTime, time).getSeconds());
				});
	}


	/**
	 * Tests that messages are ignored if a canvas gets too big
	 */
	@Test
	public void testCanvasSizeLimit() {
		String canvasId = UUID.randomUUID().toString();
		IntStream.range(0, 1000500 / 3)
				.forEach(i -> {
					LineMessageOut response = canvasManager.handleLineMessage(
							canvasId,
							() -> "some-user",
							new LineMessage("s", i, ImmutableList.of(
									new Point(10, 10),
									new Point(12, 12)), "#fff", 10, 0, true));

					// Each line has two points so has a 'size' of 3

					if (i > 1000000 / 3) {
						assertNull("should have null response at index " + i, response);
					} else {
						assertNotNull("should have non-null response at index " + i, response);
					}
				});

		assertEquals(1000500 / 3, canvasManager.getTotalMessageCount());
	}


	/**
	 * Do all operations a lot at the same time to (hopefully) surface any
	 * concurrency exceptions
	 */
	@Test
	public void testConcurrencyBomb() {
		ExecutorService threads = Executors.newFixedThreadPool(10);
		IntStream.range(0, 5000)
				.parallel()
				.mapToObj(i -> {
					return threads.submit(() -> {
						when(now.get()).thenReturn(startingTime.plusSeconds(i));
						canvasManager.handleLineMessage(
								"c- " + i % 150, // More canvases than allowed so regular cleanup occurs
								() -> "some-user",
								new LineMessage(i % 2 == 0 ? "s" : "c", i,
										ImmutableList.of(
												new Point(10, 10),
												new Point(12, 12)),
										"#fff",
										10,
										0,
										true));

						if (i % 100 == 0) {
							canvasManager.cleanupOldCanvases();
						}
					});
				})
				.collect(Collectors.toList())
				.forEach(f -> {
					try {
						f.get(10, TimeUnit.SECONDS);
					} catch (InterruptedException | ExecutionException | TimeoutException e) {
						throw new RuntimeException(e);
					}
				});

		assertEquals(5000, canvasManager.getTotalMessageCount());
		assertTrue(canvasManager.getRemovedCanvasCount() > 0);
	}
}
