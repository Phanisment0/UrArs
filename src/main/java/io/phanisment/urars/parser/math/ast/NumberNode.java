package io.phanisment.urars.parser.math.ast;

import org.jspecify.annotations.NonNull;

public record NumberNode(double value) implements Node {
	@Override
	public @NonNull String toString() {
		return String.valueOf(value);
	}
}