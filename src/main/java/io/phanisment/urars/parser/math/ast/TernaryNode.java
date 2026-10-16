package io.phanisment.urars.parser.math.ast;

import org.jspecify.annotations.NonNull;

public record TernaryNode(Node condition, Node then, Node otherwise) implements Node {
	@Override
	public @NonNull String toString() {
		return "(" + condition + " ? " + then + " : " + otherwise + ")";
	}
}