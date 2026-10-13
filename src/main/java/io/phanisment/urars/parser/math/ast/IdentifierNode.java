package io.phanisment.urars.parser.math.ast;

import io.phanisment.urars.util.BufferedString;
import org.jspecify.annotations.NonNull;

public record IdentifierNode(BufferedString value) implements Node {
	@Override
	public @NonNull String toString() {
		return value.toString();
	}
}