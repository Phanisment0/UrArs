package io.phanisment.urars.util;

import io.phanisment.urars.UrArs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public final class UrArsScheduler {
	private static final Map<Long, List<Runnable>> queue = new HashMap<>();
	private static final PriorityQueue<Timed> timed = new PriorityQueue<>((a, b) -> Long.compare(a.deadline() - b.deadline(), 0));

	private static long now = 0;

	private UrArsScheduler() {}

	/** Wait N game ticks (slows down when the server lags). */
	public static void wait(long ticks, Runnable callback) {
		if (ticks <= 0) {
			callback.run();
			return;
		}
		queue.computeIfAbsent(now + ticks, k -> new ArrayList<>()).add(callback);
	}

	/** Wait N real-time milliseconds (not affected by server lag). */
	public static void waitMillis(long millis, Runnable callback) {
		if (millis <= 0) {
			callback.run();
			return;
		}
		waitUntilNanos(System.nanoTime() + millis * 1_000_000L, callback);
	}

	/** Run at an absolute System.nanoTime() deadline. */
	public static void waitUntilNanos(long deadline, Runnable callback) {
		timed.add(new Timed(deadline, callback));
	}

	public static void init() {
		now++;

		List<Runnable> due = queue.remove(now);
		if (due != null) for (Runnable task : due) run(task);

		long t = System.nanoTime();
		while (!timed.isEmpty() && timed.peek().deadline() - t <= 0) run(timed.poll().task());
	}

	private static void run(Runnable task) {
		try {
			task.run();
		} catch (Throwable e) {
			UrArs.LOGGER.error("Scheduled task failed", e);
		}
	}

	private record Timed(long deadline, Runnable task) {
	}
}