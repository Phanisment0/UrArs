package io.phanisment.urars.parser.math.ast;

import io.phanisment.urars.parser.math.lexer.MathLexer.Token;
import org.jspecify.annotations.NonNull;

public record UnaryNode(Token op, Node value) implements Node {
	@Override
	public @NonNull String toString() {
		return op.alias() + value;
	}
}