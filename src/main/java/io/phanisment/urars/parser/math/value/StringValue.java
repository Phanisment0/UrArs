package io.phanisment.urars.parser.math.value;

import java.util.List;

import io.phanisment.urars.exception.InvalidMathOperationException;

public class StringValue implements IValue<String> {
	private final String value;

	public StringValue(String value) {
		this.value = value;
	}

	@Override
	public String raw() {
		return value;
	}

	@Override
	public IValue<?> member(String name) throws InvalidMathOperationException {
		throw new InvalidMathOperationException("Cannot access member '" + name + "' on string");
	}

	@Override
	public IValue<?> invoke(List<IValue<?>> args) throws InvalidMathOperationException {
		throw new InvalidMathOperationException("String is not callable");
	}

	@Override
	public String toString() {
		return value;
	}
}
