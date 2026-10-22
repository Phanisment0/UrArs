package io.phanisment.urars.parser.math.eval;

import io.phanisment.urars.exception.InvalidMathOperationException;
import io.phanisment.urars.parser.math.ast.*;
import io.phanisment.urars.parser.math.lexer.MathLexer;
import io.phanisment.urars.parser.math.value.BooleanValue;
import io.phanisment.urars.parser.math.value.IValue;
import io.phanisment.urars.parser.math.value.NumberValue;
import io.phanisment.urars.parser.math.value.StringValue;
import io.phanisment.urars.util.BufferedString;

import java.util.List;
import java.util.ArrayList;

public class MathEval {
	private static final int INT = 0, LONG = 1, DOUBLE = 2;

	public static IValue<?> eval(Node node, EvalContext context) throws InvalidMathOperationException {
		if (node instanceof NumberNode(Number value)) return new NumberValue(value);
		if (node instanceof StringNode(BufferedString value)) return new StringValue(value.toString());
		if (node instanceof IdentifierNode identifier) return evalIdentifier(identifier, context);
		if (node instanceof MemberNode member) return evalMember(member, context);
		if (node instanceof CallNode call) return evalCall(call, context);
		if (node instanceof UnaryNode unary) return evalUnary(unary, context);
		if (node instanceof BinaryNode binary) return evalBinary(binary, context);
		if (node instanceof BooleanNode(boolean value)) return BooleanValue.of(value);
		if (node instanceof TernaryNode ternary) return evalTernary(ternary, context);
		throw new IllegalArgumentException();
	}

	private static IValue<?> evalTernary(TernaryNode node, EvalContext context) {
		return asBool(eval(node.condition(), context))
				? eval(node.then(), context)
				: eval(node.otherwise(), context);
	}

	private static IValue<?> evalBinary(BinaryNode node, EvalContext context) throws InvalidMathOperationException {
		if (node.op() == MathLexer.Token.AND) {
			if (!asBool(eval(node.left(), context))) return BooleanValue.FALSE;
			return BooleanValue.of(asBool(eval(node.right(), context)));
		}
		if (node.op() == MathLexer.Token.OR) {
			if (asBool(eval(node.left(), context))) return BooleanValue.TRUE;
			return BooleanValue.of(asBool(eval(node.right(), context)));
		}

		IValue<?> left = eval(node.left(), context);
		IValue<?> right = eval(node.right(), context);
		return switch (node.op()) {
			case ADD -> {
				if (left instanceof StringValue || right instanceof StringValue) yield new StringValue(left.toString() + right.toString());
				yield new NumberValue(add(asNum(left), asNum(right)));
			}
			case MIN -> new NumberValue(sub(asNum(left), asNum(right)));
			case MUL -> new NumberValue(mul(asNum(left), asNum(right)));
			case DIV -> new NumberValue(div(asNum(left), asNum(right)));
			case MOD -> new NumberValue(mod(asNum(left), asNum(right)));
			case EQ  -> BooleanValue.of(valueEquals(left, right));
			case NEQ -> BooleanValue.of(!valueEquals(left, right));
			case LT  -> BooleanValue.of(compare(left, right) < 0);
			case LTE -> BooleanValue.of(compare(left, right) <= 0);
			case GT  -> BooleanValue.of(compare(left, right) > 0);
			case GTE -> BooleanValue.of(compare(left, right) >= 0);
			default  -> throw new IllegalArgumentException();
		};
	}

	private static IValue<?> evalUnary(UnaryNode node, EvalContext context) throws InvalidMathOperationException {
		IValue<?> operand = eval(node.value(), context);
		return switch (node.op()) {
			case ADD -> new NumberValue(asNum(operand));
			case MIN -> new NumberValue(negate(asNum(operand)));
			case NOT -> BooleanValue.of(!asBool(operand));
			default  -> throw new IllegalArgumentException();
		};
	}

	private static boolean asBool(IValue<?> value) {
		if (value instanceof BooleanValue(Boolean raw)) return raw;
		throw new InvalidMathOperationException("Expected boolean but got " + value.getClass().getSimpleName());
	}

	private static boolean valueEquals(IValue<?> a, IValue<?> b) {
		if (a instanceof NumberValue x && b instanceof NumberValue y) return compareNum(x.raw(), y.raw()) == 0;
		return java.util.Objects.equals(a.raw(), b.raw());
	}

	private static int compare(IValue<?> a, IValue<?> b) {
		if (a instanceof NumberValue x && b instanceof NumberValue y) return compareNum(x.raw(), y.raw());
		if (a instanceof StringValue x && b instanceof StringValue y) return x.raw().compareTo(y.raw());
		throw new InvalidMathOperationException("Cannot compare " + a.getClass().getSimpleName() + " with " + b.getClass().getSimpleName());
	}

	private static int compareNum(Number a, Number b) {
		if (rank(a, b) == DOUBLE) {
			double x = a.doubleValue(), y = b.doubleValue();
			return Double.compare(x, y);
		}
		return Long.compare(a.longValue(), b.longValue());
	}

	private static int rank(Number n) {
		if (n instanceof Double || n instanceof Float) return DOUBLE;
		if (n instanceof Long) return LONG;
		return INT;
	}

	private static int rank(Number a, Number b) {
		return Math.max(rank(a), rank(b));
	}

	private static Number wrap(long value, int rank) {
		if (rank == INT && value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) return (int) value;
		return value;
	}

	private static Number add(Number a, Number b) {
		int r = rank(a, b);
		if (r == DOUBLE) return a.doubleValue() + b.doubleValue();
		try {
			return wrap(Math.addExact(a.longValue(), b.longValue()), r);
		} catch (ArithmeticException e) {
			return a.doubleValue() + b.doubleValue(); // long overflow -> double
		}
	}

	private static Number sub(Number a, Number b) {
		int r = rank(a, b);
		if (r == DOUBLE) return a.doubleValue() - b.doubleValue();
		try {
			return wrap(Math.subtractExact(a.longValue(), b.longValue()), r);
		} catch (ArithmeticException e) {
			return a.doubleValue() - b.doubleValue();
		}
	}

	private static Number mul(Number a, Number b) {
		int r = rank(a, b);
		if (r == DOUBLE) return a.doubleValue() * b.doubleValue();
		try {
			return wrap(Math.multiplyExact(a.longValue(), b.longValue()), r);
		} catch (ArithmeticException e) {
			return a.doubleValue() * b.doubleValue();
		}
	}

	private static Number div(Number a, Number b) throws InvalidMathOperationException {
		int r = rank(a, b);
		if (r == DOUBLE) return a.doubleValue() / b.doubleValue();
		if (b.longValue() == 0) throw new InvalidMathOperationException("Division by zero");
		return wrap(a.longValue() / b.longValue(), r);
	}

	private static Number mod(Number a, Number b) throws InvalidMathOperationException {
		int r = rank(a, b);
		if (r == DOUBLE) return a.doubleValue() % b.doubleValue();
		if (b.longValue() == 0) throw new InvalidMathOperationException("Modulo by zero");
		return wrap(a.longValue() % b.longValue(), r);
	}

	private static Number negate(Number a) {
		int r = rank(a);
		if (r == DOUBLE) return -a.doubleValue();
		long v = a.longValue();
		if (v == Long.MIN_VALUE) return -(double) v;
		return wrap(-v, r);
	}

	private static IValue<?> evalIdentifier(IdentifierNode node, EvalContext context) {
		String name = node.value().toString();
		IValue<?> value = context.get(name);
		if (value == null) throw new IllegalArgumentException("Unknown variable: " + name);
		return value;
	}

	private static IValue<?> evalMember(MemberNode node, EvalContext context) throws InvalidMathOperationException {
		IValue<?> object = eval(node.member(), context);
		return object.member(node.name().toString());
	}

	private static IValue<?> evalCall(CallNode node, EvalContext context) throws InvalidMathOperationException {
		IValue<?> function = eval(node.function(), context);
		List<IValue<?>> args = new ArrayList<>(node.args().size());
		for (Node arg : node.args()) args.add(eval(arg, context));
		return function.invoke(args);
	}

	private static Number asNum(IValue<?> value) {
		if (value instanceof NumberValue number) return number.raw();
		throw new IllegalArgumentException("Expected number but got " + value.getClass().getSimpleName());
	}
}