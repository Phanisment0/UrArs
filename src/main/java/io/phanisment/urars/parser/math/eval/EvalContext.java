package io.phanisment.urars.parser.math.eval;

import io.phanisment.urars.parser.math.value.IValue;
import io.phanisment.urars.parser.math.library.standard.MathLibrary;
import io.phanisment.urars.parser.math.library.standard.StandardLibrary;
import io.phanisment.urars.exception.InvalidMathOperationException;
import io.phanisment.urars.parser.math.library.IMathLibrary;

public interface EvalContext {
	static EvalContext empty() {
		return new EvalContextImpl();
	}

	static EvalContext withLibraries(IMathLibrary... libs) throws InvalidMathOperationException {
		EvalContextImpl context = new EvalContextImpl();
		for (IMathLibrary lib : libs) context.register(lib);
		return context;
	}

	static EvalContext withStandardLibrary() throws InvalidMathOperationException {
		return withLibraries(new StandardLibrary(), new MathLibrary());
	}

	IValue<?> get(String name);
	int size();
}