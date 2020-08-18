package org.jfl110.socketcanvas;

import java.time.ZonedDateTime;
import java.util.function.Supplier;

class NowProvider implements Supplier<ZonedDateTime> {
	@Override
	public ZonedDateTime get() {
		return ZonedDateTime.now();
	}
}
