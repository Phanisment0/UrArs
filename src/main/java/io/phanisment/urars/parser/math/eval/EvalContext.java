package io.phanisment.urars.parser.math.eval;

import java.util.HashMap;
import java.util.Map;

import io.phanisment.urars.exception.InvalidMathOperationException;
import io.phanisment.urars.parser.math.library.IMathLibrary;
import io.phanisment.urars.parser.math.library.MathLibraryRegistry;
import io.phanisment.urars.parser.math.library.standard.MathLibrary;
import io.phanisment.urars.parser.math.library.standard.StandardLibrary;
import io.phanisment.urars.parser.math.value.IValue;

public class EvalContext {
	private MathLibraryRegistry libraries = new MathLibraryRegistry();
	private Map<String, IValue<?>> values = new HashMap<>();

	public static EvalContext empty() {
		return new EvalContext();
	}

	public static EvalContext withLibraries(IMathLibrary... libs) throws InvalidMathOperationException {
		var context = new EvalContext();
		for (IMathLibrary lib : libs) context.register(lib);
		return context;
	}

	public static EvalContext withStandardLibrary() throws InvalidMathOperationException {
		return withLibraries(new StandardLibrary(), new MathLibrary());
	}

	public EvalContext copy() {
		var new_eval = new EvalContext();
		new_eval.libraries = this.libraries;
		new_eval.values = this.values;
		return new_eval;
	}

	public void put(Map<String, IValue<?>> values) {
		this.values = values;
	}

	public void put(String name, IValue<?> value) {
		if (libraries.contains(name)) throw new IllegalArgumentException("Cannot override library: " + name);
		values.put(name, value);
	}

	public void register(IMathLibrary lib) throws InvalidMathOperationException {
		if (values.containsKey(lib.name())) throw new IllegalArgumentException("Variable already exists: " + lib.name());
		libraries.register(lib);
	}

	public IValue<?> get(String name) {
		IValue<?> value = values.get(name);
		if (value != null) return value;
		return libraries.get(name);
	}

	public boolean remove(String name) {
		return values.remove(name) != null;
	}

	public boolean contains(String name) {
		return values.containsKey(name);
	}

	public int size() {
		return values.size();
	}
}