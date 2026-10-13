package io.phanisment.urars.parser.math.library;

import java.util.HashMap;
import java.util.Map;

import io.phanisment.urars.exception.InvalidMathOperationException;
import io.phanisment.urars.parser.math.value.IValue;
import io.phanisment.urars.parser.math.value.ObjectValue;

public final class MathLibraryRegistry {
	public final Map<String, IValue<?>> libraries = new HashMap<>();

	public  void register(IMathLibrary lib) throws InvalidMathOperationException {
		String name = lib.name();
		if (libraries.containsKey(name)) throw new InvalidMathOperationException("Library is already registered: " + name);

		var obj = new ObjectValue(name);
		lib.register(obj);

		libraries.put(name, obj);
	}

	public IValue<?> get(String name) {
		return  libraries.get(name);
	}

	public boolean contains(String name) {
		return libraries.containsKey(name);
	}

	public int size() {
		return libraries.size();
	}
}