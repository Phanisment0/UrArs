package io.phanisment.urars.exception;

import java.text.ParseException;

public final class Unchecked {
	private Unchecked() {}

	@FunctionalInterface
	public interface ParseSupplier<T> {
		T get() throws ParseException;
	}

	public static <T> T parse(ParseSupplier<T> supplier) {
		try {
			return supplier.get();
		} catch (ParseException | UnsupportedOperationException e) { // also covers MathParserException if it extends ParseException
			throw new SkillParseException(e);
		}
	}
}