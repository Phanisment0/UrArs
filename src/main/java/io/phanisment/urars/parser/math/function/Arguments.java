package io.phanisment.urars.parser.math.function;

import java.util.List;

import io.phanisment.urars.exception.InvalidMathOperationException;
import io.phanisment.urars.parser.math.value.IValue;
import io.phanisment.urars.parser.math.value.NumberValue;
import io.phanisment.urars.parser.math.value.StringValue;

public class Arguments {
	private final List<IValue<?>> args;

	public Arguments(List<IValue<?>> args) {
		this.args = args;
	}

	public IValue<?> get(int index) throws InvalidMathOperationException {
		return args.get(index);
	}

	public int size() {
		return args.size();
	}

	public double number(int index, double def) {
		try {
			return number(index);
		} catch (IndexOutOfBoundsException | InvalidMathOperationException e) {
			return def;
		}
	}

	public double number(int index) throws InvalidMathOperationException {
		IValue<?> val = get(index);
		if (val instanceof NumberValue num) return num.raw();
		throw new InvalidMathOperationException("Expect number but got " + val.getClass().getSimpleName());
	}

	public String string(int index, String def) {
		try {
			return string(index);
		} catch (IndexOutOfBoundsException | InvalidMathOperationException e) {
			return def;
		}
	}

	public String string(int index) throws InvalidMathOperationException {
		IValue<?> val = get(index);
		if (val instanceof StringValue num) return num.raw();
		throw new InvalidMathOperationException("Expect string but got " + val.getClass().getSimpleName());
	}
}