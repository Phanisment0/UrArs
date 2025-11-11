package io.phanisment.urars.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public final class TickScheduler {
	private static final List<TickScheduler> handler = new ArrayList<>();
	private long remaining;
	private final Runnable callback;
	
	private TickScheduler(long tick, Runnable callback) {
		this.remaining = tick;
		this.callback = callback;
	}
	
	public static void wait(long tick, Runnable callback) {
		if (tick <= 0) {
			callback.run();
			return;
		}
		handler.add(new TickScheduler(tick, callback));
	}
	
	public static void init() {
		Iterator<TickScheduler> it = handler.iterator();
		while (it.hasNext()) {
			TickScheduler scheduler = it.next();
			scheduler.remaining--;
			if (scheduler.remaining <= 0) {
				scheduler.callback.run();
				it.remove();
			}
		}
	}
}