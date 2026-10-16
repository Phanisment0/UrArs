package io.phanisment.urars.parser.math;

import io.phanisment.urars.exception.InvalidMathOperationException;
import io.phanisment.urars.exception.MathParserException;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.parser.math.eval.EvalContext;
import io.phanisment.urars.parser.math.eval.EvalContextImpl;
import io.phanisment.urars.parser.math.eval.MathEval;
import io.phanisment.urars.parser.math.library.IMathLibrary;
import io.phanisment.urars.parser.math.parser.MathParser;
import io.phanisment.urars.parser.math.value.IValue;
import io.phanisment.urars.parser.math.value.NumberValue;
import io.phanisment.urars.parser.math.value.ObjectValue;
import io.phanisment.urars.parser.math.value.StringValue;
import io.phanisment.urars.util.BufferedString;

import java.util.Map;

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

	public UrArsMath setVar(String name, IValue<?> value) {
		((EvalContextImpl)context).put(name, value);
		return this;
	}

	public UrArsMath setVars(Map<String, IValue<?>> values) {
		((EvalContextImpl)context).put(values);
		return this;
	}

	public IValue<?> eval() throws IllegalStateException {
		if (parsed == null) throw new IllegalStateException("Nothing parsed yet, call parse() first");
		// Fall back to the standard library if the user never picked one
		if (context == null) context = EvalContext.withStandardLibrary();
		return MathEval.eval(parsed, context);
	}

	public Node node() {
		return parsed;
	}
}
