package io.phanisment.urars.parser.math.ast;

import io.phanisment.urars.util.BufferedString;
import org.jspecify.annotations.NonNull;

public record MemberNode(Node member, BufferedString name) implements Node {
	@Override
	public @NonNull String toString() {
		return member.toString() + "." + name.toString();
	}
}