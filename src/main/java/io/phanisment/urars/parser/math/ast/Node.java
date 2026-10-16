package io.phanisment.urars.parser.math.ast;

import io.phanisment.urars.exception.InvalidMathOperationException;
import io.phanisment.urars.parser.math.eval.EvalContext;
import io.phanisment.urars.parser.math.eval.MathEval;
import io.phanisment.urars.parser.math.value.IValue;
import io.phanisment.urars.parser.math.value.NumberValue;
import io.phanisment.urars.parser.math.value.ObjectValue;
import io.phanisment.urars.parser.math.value.StringValue;

public sealed interface Node permits CallNode, IdentifierNode, MemberNode, StringNode, NumberNode, BinaryNode, UnaryNode {
	default IValue<?> eval(EvalContext context) {
		// Fall back to the standard library if the user never picked one
		if (context == null) context = EvalContext.withStandardLibrary();
		return MathEval.eval(this, context);
	}

	default NumberValue evalAsNumber(EvalContext context) throws InvalidMathOperationException {
		IValue<?> result = eval(context);
		if (result instanceof NumberValue number) return number;
		throw new InvalidMathOperationException("Expected number result but got " + result.getClass().getSimpleName());
	}

	default ObjectValue evalAsObject(EvalContext context) {
		IValue<?> result = eval(context);
		if (result instanceof ObjectValue value) return value;
		throw new InvalidMathOperationException("Expected object result but got " + result.getClass().getSimpleName());
	}

	default int evalAsInt(EvalContext context) {
		return evalAsNumber(context).raw().intValue();
	}

	default long evalAsLong(EvalContext context) {
		return evalAsNumber(context).raw().longValue();
	}

	default double evalAsDouble(EvalContext context) {
		return evalAsNumber(context).raw().doubleValue();
	}

	default float evalAsFloat(EvalContext context) {
		return evalAsNumber(context).raw().floatValue();
	}

	default String evalAsString(EvalContext context) {
		IValue<?> result = eval(context);
		if (result instanceof StringValue value) return value.raw();
		return result.toString();
	}
}
