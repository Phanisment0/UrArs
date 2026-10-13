package io.phanisment.urars.parser.math.function;

import io.phanisment.urars.parser.math.value.IValue;

@FunctionalInterface
public interface Function {
	public IValue<?> invoke(Arguments args);
}