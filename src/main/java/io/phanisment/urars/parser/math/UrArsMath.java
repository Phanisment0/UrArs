package io.phanisment.urars.parser.math;

import io.phanisment.urars.exception.InvalidMathOperationException;
import io.phanisment.urars.exception.MathParserException;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.parser.math.eval.EvalContext;
import io.phanisment.urars.parser.math.eval.MathEval;
import io.phanisment.urars.parser.math.library.IMathLibrary;
import io.phanisment.urars.parser.math.parser.MathParser;
import io.phanisment.urars.parser.math.value.IValue;
import io.phanisment.urars.parser.math.value.NumberValue;
import io.phanisment.urars.parser.math.value.StringValue;
import io.phanisment.urars.util.BufferedString;

/**
 * All in 1 class features.
 */
public final class UrArsMath {
	private EvalContext context;
	private Node parsed;

	private UrArsMath(EvalContext context) {
		this.context = context;
	}

	public static UrArsMath withLibraries(IMathLibrary... libraries) {
		return new UrArsMath(EvalContext.withLibraries(libraries));
	}

	public UrArsMath withStandardLibrary() {
		return new UrArsMath(EvalContext.withStandardLibrary());
	}

	public UrArsMath parse(BufferedString input) throws MathParserException {
		this.parsed = new MathParser(input).parse();
		return this;
	}

	public UrArsMath parse(String input) throws MathParserException {
		this.parsed = new MathParser(input).parse();
		return this;
	}

	public IValue<?> eval() throws IllegalStateException {
		if (parsed == null) throw new IllegalStateException("Nothing parsed yet, call parse() first");
		// Fall back to the standard library if the user never picked one
		if (context == null) context = EvalContext.withStandardLibrary();
		return MathEval.eval(parsed, context);
	}

	public NumberValue evalAsNumber() throws InvalidMathOperationException {
		IValue<?> result = eval();
		if (result instanceof NumberValue number) return number;
		throw new InvalidMathOperationException("Expected number result but got " + result.getClass().getSimpleName());
	}

	public double evalAsDouble() {
		return evalAsNumber().raw();
	}

	public String evalAsString() {
		IValue<?> result = eval();
		if (result instanceof StringValue value) return value.raw();
		return result.toString();
	}

	public Node node() {
		return parsed;
	}
}
