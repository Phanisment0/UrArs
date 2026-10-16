package io.phanisment.urars.parser.math.eval;

import io.phanisment.urars.exception.InvalidMathOperationException;
import io.phanisment.urars.parser.math.ast.BinaryNode;
import io.phanisment.urars.parser.math.ast.CallNode;
import io.phanisment.urars.parser.math.ast.IdentifierNode;
import io.phanisment.urars.parser.math.ast.MemberNode;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.parser.math.ast.NumberNode;
import io.phanisment.urars.parser.math.ast.StringNode;
import io.phanisment.urars.parser.math.ast.UnaryNode;
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
		throw new IllegalArgumentException();
	}

	private static IValue<?> evalBinary(BinaryNode node, EvalContext context) throws InvalidMathOperationException {
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
			default  -> throw new IllegalArgumentException();
		};
	}

	private static IValue<?> evalUnary(UnaryNode node, EvalContext context) throws InvalidMathOperationException {
		Number value = asNum(eval(node.value(), context));
		return switch (node.op()) {
			case ADD -> new NumberValue(value);
			case MIN -> new NumberValue(negate(value));
			default  -> throw new IllegalArgumentException();
		};
	}

	private static int rank(Number n) {
		if (n instanceof Double || n instanceof Float) return DOUBLE;
		if (n instanceof Long) return LONG;
		return INT; // Integer, Short, Byte
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