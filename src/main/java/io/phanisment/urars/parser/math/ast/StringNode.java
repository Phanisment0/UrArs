package io.phanisment.urars.parser.math.ast;

import io.phanisment.urars.util.BufferedString;
import org.jspecify.annotations.NonNull;

public record StringNode(BufferedString value) implements Node {
	public StringNode(String value) {
		this(new BufferedString(value));
	}

	@Override
	public @NonNull String toString() {
		return value.toString();
	}
}