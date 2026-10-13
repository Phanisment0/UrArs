package io.phanisment.urars.parser.math.ast;

import org.jspecify.annotations.NonNull;

import java.util.List;

public record CallNode(Node function, List<Node> args) implements Node {
	@Override
	public @NonNull String toString() {
		return function.toString() + args;
	}
}
