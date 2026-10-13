package io.phanisment.urars.parser.math.function;

import java.util.List;

import io.phanisment.urars.exception.InvalidMathOperationException;
import io.phanisment.urars.parser.math.value.IValue;

public class FunctionValue implements IValue<Function> {
	private final Function value;

	public FunctionValue(Function value) {
		this.value = value;
	}

	@Override
	public Function raw() {
		return value;
	}

	@Override
	public IValue<?> member(String name) throws InvalidMathOperationException {
		throw new InvalidMathOperationException("Cannot access member '" + name + "' on function");
	}

	@Override
	public IValue<?> invoke(List<IValue<?>> args) throws InvalidMathOperationException {
		var wrapper = new Arguments(args);
		return value.invoke(wrapper);
	}

	@Override
	public String toString() {
		return "function@" + this.hashCode();
	}
}