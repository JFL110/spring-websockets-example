package org.jfl110.socketcanvas;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Mini end-to-end integration test that creates a websocket and tests
 * interactions with a couple of clients.
 * 
 * @author jim
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestWebsocketIntegration {

	private static final String USER_INIT_CHANNEL = "/user/topic/init/";
	private static final String USER_INIT_DEST = "/app/canvas/init/";

	private static final String CANVAS_CHANNEL = "/topic/canvas/";
	private static final String CANVAS_DEST = "/app/canvas/";

	@LocalServerPort private int port;

	@Test
	public void testCanvasFlow() throws Exception {

		// Given
		String canvasId = UUID.randomUUID().toString();

		// Connect new client to empty canvas
		CanvasClient c1 = new CanvasClient(canvasId).connectAndSubscribe(url("/canvas"));

		// Assert canvas is empty
		assertEquals(1, c1.initMessageHandler.receivedObjects.size());
		assertTrue(c1.initMessage.getLines().isEmpty());

		// Send a start line message
		c1.lineMessageHandler.setObjectCountToWaitFor(1);

		c1.sendLineMessage(new LineMessage("s", 0, ImmutableList.of(
				new Point(10, 10),
				new Point(12, 11)), "#fff", 10, 0, false));

		// Wait for and verify message received back
		c1.waitForLineMessage();

		LineMessageOut msg = c1.lineMessageHandler.receivedObjects.get(0);
		assertEquals(c1.initMessage.getUserId(), msg.getClientId());
		assertEquals(0, msg.getClientLineNumber());
		assertEquals(10, msg.getRadius());
		assertEquals("#fff", msg.getBrushColor());
		assertFalse(msg.isFinished());
		assertEquals("s", msg.getType());
		assertEquals(2, msg.getPoints().size());
		assertEquals(10, msg.getPoints().get(0).getX());
		assertEquals(10, msg.getPoints().get(0).getY());
		assertEquals(12, msg.getPoints().get(1).getX());
		assertEquals(11, msg.getPoints().get(1).getY());

		// Send a continuation of the line
		c1.lineMessageHandler.setObjectCountToWaitFor(1);
		c1.sendLineMessage(new LineMessage("c", 0, ImmutableList.of(
				new Point(13, 12)), "#fff", 8, 0, false));

		c1.waitForLineMessage();
		msg = c1.lineMessageHandler.receivedObjects.get(1);
		assertEquals(c1.initMessage.getUserId(), msg.getClientId());
		assertEquals(0, msg.getClientLineNumber());
		assertFalse(msg.isFinished());
		assertEquals("c", msg.getType());
		assertEquals(1, msg.getPoints().size());
		assertEquals(13, msg.getPoints().get(0).getX());
		assertEquals(12, msg.getPoints().get(0).getY());

		// Send a finished line message
		c1.lineMessageHandler.setObjectCountToWaitFor(1);
		c1.sendLineMessage(new LineMessage("f", 0, ImmutableList.of(), "#fff", 8, 0, false));

		c1.waitForLineMessage();
		msg = c1.lineMessageHandler.receivedObjects.get(2);
		assertEquals(c1.initMessage.getUserId(), msg.getClientId());
		assertEquals(0, msg.getClientLineNumber());
		assertTrue(msg.isFinished());
		assertEquals("f", msg.getType());

		// Connect new client to canvas
		CanvasClient c2 = new CanvasClient(canvasId).connectAndSubscribe(url("/canvas"));

		// New client as different id
		assertNotEquals(c1.initMessage.getUserId(), c2.initMessage.getUserId());

		// Canvas has expected line
		assertEquals(1, c2.initMessage.getLines().size());
		Line line = c2.initMessage.getLines().get(0);
		assertEquals(c1.initMessage.getUserId(), line.getUserId());
		assertEquals(0, line.getOwnerLineNumber());
		assertEquals(10, line.getBrushRadius());
		assertEquals("#fff", line.getBrushColor());
		assertEquals(1, line.getzIndex());
		assertEquals(3, line.getPoints().size());
		assertEquals(10, line.getPoints().get(0).getX());
		assertEquals(10, line.getPoints().get(0).getY());
		assertEquals(12, line.getPoints().get(1).getX());
		assertEquals(11, line.getPoints().get(1).getY());
		assertEquals(13, line.getPoints().get(2).getX());
		assertEquals(12, line.getPoints().get(2).getY());

		// Second client adds a short line
		c1.lineMessageHandler.setObjectCountToWaitFor(1);
		c2.lineMessageHandler.setObjectCountToWaitFor(1);
		c1.sendLineMessage(new LineMessage("s", 1, ImmutableList.of(new Point(15, 13), new Point(15, 12)), "#ccc", 8, 0, true));

		// Both clients get the same message
		c1.waitForLineMessage();
		c2.waitForLineMessage();

		LineMessageOut c1Message = c1.lineMessageHandler.receivedObjects.get(3);
		LineMessageOut c2Message = c2.lineMessageHandler.receivedObjects.get(0);

		ImmutableList.of(c1Message, c2Message).forEach(l -> {
			assertEquals(c1.initMessage.getUserId(), l.getClientId());
			assertEquals(1, l.getClientLineNumber());
			assertTrue(l.isFinished());
			assertEquals("s", l.getType());
			assertEquals(2, l.getPoints().size());
			assertEquals(8, l.getRadius());
			assertEquals("#ccc", l.getBrushColor());
			assertEquals(15, l.getPoints().get(0).getX());
			assertEquals(13, l.getPoints().get(0).getY());
			assertEquals(15, l.getPoints().get(1).getX());
			assertEquals(12, l.getPoints().get(1).getY());
		});

		// Get the app status endpoint response
		String statusResponse = new RestTemplate().getForObject("http://localhost:" + port, String.class);
		assertEquals("App status ok. [2] connected users. [1] canvases. [0] canvases removed. [4] total messages.", statusResponse);
	}


	private String url(String suffix) {
		return "ws://localhost:" + port + suffix;
	}

	/**
	 * Testing client
	 */
	static class CanvasClient {

		private final TestingFrameHandler<LineMessageOut> lineMessageHandler = new TestingFrameHandler<>(LineMessageOut.class);
		private final TestingFrameHandler<InitialCanvasMessage> initMessageHandler = new TestingFrameHandler<>(InitialCanvasMessage.class);
		private final String canvasId;
		private StompSession stompSession;
		private InitialCanvasMessage initMessage;

		CanvasClient(String canvasId) {
			this.canvasId = canvasId;
		}


		CanvasClient connectAndSubscribe(String url) throws Exception {
			// Connect
			WebSocketStompClient stompClient = new WebSocketStompClient(
					new SockJsClient(ImmutableList.of(new WebSocketTransport(new StandardWebSocketClient()))));
			stompClient.setMessageConverter(new MappingJackson2MessageConverter());
			stompSession = stompClient.connect(url, new StompSessionHandlerAdapter() {
			}).get(1, TimeUnit.SECONDS);

			System.out.println(stompSession.getSessionId());

			// Subscribe
			stompSession.subscribe(CANVAS_CHANNEL + canvasId, lineMessageHandler);
			stompSession.subscribe(USER_INIT_CHANNEL + canvasId, initMessageHandler);

			// Send init message and wait
			initMessageHandler.setObjectCountToWaitFor(1);
			stompSession.send(USER_INIT_DEST + canvasId, "{}");
			if (!initMessageHandler.objectWaitLatch.await(1, TimeUnit.SECONDS)) {
				fail("Gave up waiting for init message");
			}

			// Process initialisation message
			assertEquals(1, initMessageHandler.receivedObjects.size());
			InitialCanvasMessage initMessage = initMessageHandler.receivedObjects.get(0);
			assertNotNull(initMessage);
			this.initMessage = initMessage;

			return this;
		}


		void sendLineMessage(LineMessage msg) throws Exception {
			stompSession.send(CANVAS_DEST + canvasId, msg);
		}


		void waitForLineMessage() throws Exception {
			if (!lineMessageHandler.objectWaitLatch.await(1, TimeUnit.SECONDS)) {
				fail("Gave up waiting for line messages");
			}
		}
	}

	/**
	 * Stomp frame handler that captures received objects
	 */
	static class TestingFrameHandler<T> implements StompFrameHandler {

		private final Class<T> type;
		private CountDownLatch objectWaitLatch;
		private final List<T> receivedObjects = Lists.newCopyOnWriteArrayList();

		TestingFrameHandler(Class<T> type) {
			this.type = type;
		}


		@Override
		public Type getPayloadType(StompHeaders headers) {
			return type;
		}


		void setObjectCountToWaitFor(int count) {
			objectWaitLatch = new CountDownLatch(count);
		}


		@SuppressWarnings("unchecked")
		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			System.out.println("Message: " + payload);
			receivedObjects.add((T) payload);
			if (objectWaitLatch != null) {
				objectWaitLatch.countDown();
			}
		}
	}
}
