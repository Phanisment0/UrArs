package io.phanisment.urars.parser.math.ast;

import org.jspecify.annotations.NonNull;

public record BooleanNode(boolean value) implements Node {
	@Override
	public @NonNull String toString() {
		return String.valueOf(value);
	}
}