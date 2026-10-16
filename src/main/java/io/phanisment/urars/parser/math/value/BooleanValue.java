package io.phanisment.urars.parser.math.value;

import java.util.List;
import io.phanisment.urars.exception.InvalidMathOperationException;
import org.jspecify.annotations.NonNull;

public record BooleanValue(Boolean raw) implements IValue<Boolean> {
	public static final BooleanValue TRUE = new BooleanValue(true);
	public static final BooleanValue FALSE = new BooleanValue(false);

	public static BooleanValue of(boolean b) { return b ? TRUE : FALSE; }

	@Override
	public IValue<?> member(String name) throws InvalidMathOperationException {
		throw new InvalidMathOperationException("Boolean has no member '" + name + "'");
	}

	@Override
	public IValue<?> invoke(List<IValue<?>> args) throws InvalidMathOperationException {
		throw new InvalidMathOperationException("Boolean is not callable");
	}

	@Override
	public @NonNull String toString() { return String.valueOf(raw); }
}