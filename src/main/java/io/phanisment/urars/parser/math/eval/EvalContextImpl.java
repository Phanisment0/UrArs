package io.phanisment.urars.parser.math.eval;

import java.util.HashMap;
import java.util.Map;

import io.phanisment.urars.exception.InvalidMathOperationException;
import io.phanisment.urars.parser.math.library.IMathLibrary;
import io.phanisment.urars.parser.math.library.MathLibraryRegistry;
import io.phanisment.urars.parser.math.value.IValue;

public class EvalContextImpl implements EvalContext {
	private final Map<String, IValue<?>> values = new HashMap<>();
	private final MathLibraryRegistry libraries = new MathLibraryRegistry();

	public void put(String name, IValue<?> value) {
		if (libraries.contains(name)) throw new IllegalArgumentException("Cannot override library: " + name);
		values.put(name, value);
	}

	public void register(IMathLibrary lib) throws InvalidMathOperationException {
		if (values.containsKey(lib.name())) throw new IllegalArgumentException("Variable already exists: " + lib.name());
		libraries.register(lib);
	}

	@Override
	public IValue<?> get(String name) {
		IValue<?> value = values.get(name);
		if (value != null) return value;
		return libraries.get(name);
	}

	@Override
	public int size() {
		return values.size();
	}
}