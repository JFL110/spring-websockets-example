package org.jfl110.socketcanvas;

import java.time.ZonedDateTime;
import java.util.function.Supplier;

/**
 * Provider for the current time.
 * 
 * Used instead of a static reference for testing.
 * 
 * @author jim
 */
class NowProvider implements Supplier<ZonedDateTime> {
	@Override
	public ZonedDateTime get() {
		return ZonedDateTime.now();
	}
}
