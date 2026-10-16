package io.phanisment.urars.parser.math.value;

import java.util.List;

import io.phanisment.urars.exception.InvalidMathOperationException;

public class NumberValue implements IValue<Number> {
	private final Number value;

	public NumberValue(Number value) {
		this.value = value;
	}

	@Override
	public Number raw() {
		return value;
	}

	@Override
	public IValue<?> member(String name) throws InvalidMathOperationException {
		throw new InvalidMathOperationException("Cannot access member '" + name + "' on number");
	}

	@Override
	public IValue<?> invoke(List<IValue<?>> args) throws InvalidMathOperationException {
		throw new InvalidMathOperationException("Number is not callable");
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}