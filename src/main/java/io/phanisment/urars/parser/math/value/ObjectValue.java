package io.phanisment.urars.parser.math.value;

import java.util.List;
import java.util.Map;

import io.phanisment.urars.exception.InvalidMathOperationException;

import java.util.HashMap;

public class ObjectValue implements IValue<Object> {
	private final Object value;
	private final Map<String, IValue<?>> members = new HashMap<>();

	public ObjectValue(Object value) {
		this.value = value;
	}

	@Override
	public Object raw() {
		return value;
	}

	public void member(String name, IValue<?> value) {
		members.put(name, value);
	}

	@Override
	public IValue<?> member(String name) throws InvalidMathOperationException {
		IValue<?> value = members.get(name);
		if (value == null) throw new InvalidMathOperationException("Unknown member: " + name);
		return value;
	}

	@Override
	public IValue<?> invoke(List<IValue<?>> args) throws InvalidMathOperationException {
		throw new InvalidMathOperationException("Member/Variable is not callable");
	}

	@Override
	public String toString() {
		return value.toString();
	}
}