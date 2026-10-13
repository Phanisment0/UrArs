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
	public static IValue<?> eval(Node node, EvalContext context) throws InvalidMathOperationException {
		if (node instanceof NumberNode(double value)) return new NumberValue(value);
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
				yield new NumberValue(asNum(left) + asNum(right));
			}
			case MIN -> new NumberValue(asNum(left) - asNum(right));
			case MUL -> new NumberValue(asNum(left) * asNum(right));
			case DIV -> new NumberValue(asNum(left) / asNum(right));
			case MOD -> new NumberValue(asNum(left) % asNum(right));
			default  -> throw new IllegalArgumentException();
		};
	}

	private static IValue<?> evalUnary(UnaryNode node, EvalContext context) throws InvalidMathOperationException {
		double value = asNum(eval(node.value(), context));
		return switch (node.op()) {
			case ADD -> new NumberValue(value);
			case MIN -> new NumberValue(-value);
			default  -> throw new IllegalArgumentException();
		};
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

	private static double asNum(IValue<?> value) {
		if (value instanceof NumberValue number) return number.raw();
		throw new IllegalArgumentException("Expected number but got " + value.getClass().getSimpleName());
	}
}