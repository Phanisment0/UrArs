package io.phanisment.urars.util;

import java.util.function.Supplier;

import static io.phanisment.urars.UrArs.LOGGER;

public final class UrArsDebugger {
	private static final String prefix = "[UrArs]";

	// volatile: can be changed from a command thread and read from other threads
	private static volatile Level level = Level.OFF;

	private UrArsDebugger() {
	}

	public static void level(Level new_level) {
		level = new_level;
	}

	public static void level(int new_level) {
		level = Level.of(new_level);
	}

	public static Level level() {
		return level;
	}

	public static boolean isEnabled(Level required) {
		return level.value >= required.value;
	}

	public static void info(String msg, Object... args) {
		if (isEnabled(Level.INFO)) LOGGER.info(prefix + " [DEBUG] " + msg, args);
	}

	public static void debug(String msg, Object... args) {
		if (isEnabled(Level.DEBUG)) LOGGER.info(prefix + " [DEBUG/D] " + msg, args);
	}

	public static void trace(String msg, Object... args) {
		if (isEnabled(Level.TRACE)) LOGGER.info(prefix + " DEBUG/T] " + msg, args);
	}

	/** Lazy variant: the message is only built if the level is enabled. */
	public static void debug(Supplier<String> msg) {
		if (isEnabled(Level.DEBUG)) LOGGER.info(prefix + " [DEBUG/D] {}", msg.get());
	}

	public static void trace(Supplier<String> msg) {
		if (isEnabled(Level.TRACE)) LOGGER.info(prefix + " [DEBUG/T] {}", msg.get());
	}

	/** Warnings and errors are always logged, regardless of debug level. */
	public static void warn(String msg, Object... args) {
		LOGGER.warn(prefix + msg, args);
	}

	public static void error(String msg, Throwable t) {
		LOGGER.error(prefix + msg, t);
	}

	/** Logs the exception with full stack trace, only when debugging is on. */
	public static void exception(String context, Throwable t) {
		if (isEnabled(Level.DEBUG)) LOGGER.error(prefix + " [DEBUG/EX] " + context, t);
	}

	/** Measures how long a task takes (shown at DEBUG level). */
	public static void time(String label, Runnable task) {
		if (!isEnabled(Level.DEBUG)) {
			task.run();
			return;
		}
		long start = System.nanoTime();
		try {
			task.run();
		} finally {
			long ms = (System.nanoTime() - start) / 1_000_000;
			LOGGER.info(prefix + " [DEBUG/TIME] {} took {} ms", label, ms);
		}
	}

	public enum Level {
		OFF(0), INFO(1), DEBUG(2), TRACE(3);

		private final int value;

		Level(int value) {
			this.value = value;
		}

		static Level of(int value) {
			for (Level l : values()) if (l.value == value) return l;
			return value <= 0 ? OFF : TRACE;
		}
	}
}