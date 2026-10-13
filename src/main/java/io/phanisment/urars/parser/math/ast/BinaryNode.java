package io.phanisment.urars.parser.math.ast;

import io.phanisment.urars.parser.math.lexer.MathLexer.Token;
import org.jspecify.annotations.NonNull;

public record BinaryNode(Node left, Token op, Node right) implements Node {
	@Override
	public @NonNull String toString() {
		return "(" + left + " " + op.alias() + " " + right + ")";
	}
}