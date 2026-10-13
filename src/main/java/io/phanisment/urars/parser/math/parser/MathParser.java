package io.phanisment.urars.parser.math.parser;

import java.util.ArrayList;
import java.util.List;

import io.phanisment.urars.exception.MathParserException;
import io.phanisment.urars.parser.math.ast.BinaryNode;
import io.phanisment.urars.parser.math.ast.CallNode;
import io.phanisment.urars.parser.math.ast.IdentifierNode;
import io.phanisment.urars.parser.math.ast.MemberNode;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.parser.math.ast.NumberNode;
import io.phanisment.urars.parser.math.ast.StringNode;
import io.phanisment.urars.parser.math.ast.UnaryNode;
import io.phanisment.urars.parser.math.lexer.MathLexer;
import io.phanisment.urars.parser.math.lexer.MathLexer.Token;
import io.phanisment.urars.util.BufferedString;

public class MathParser {
	private static final int PREC_ADD     = 10;
	private static final int PREC_MUL    = 20;
	private static final int PREC_PREFIX = 30;

	private final MathLexer lexer;

	public MathParser(String input) {
		this(input.getBytes());
	}

	public MathParser(byte[] input) {
		this(new BufferedString(input, 0, input.length));
	}

	public MathParser(BufferedString input) {
		this.lexer = new MathLexer(input);
	}

	public Node parse() throws MathParserException {
		Node node = expression(0);
		if (lexer.next() != Token.EOF) throw new MathParserException("Unexpected token", lexer.pos());
		return node;
	}

	private void expect(Token expect) throws MathParserException {
		Token actual = lexer.next();
		if (actual != expect) throw new MathParserException("Expected '" + expect + "' but found '" + actual + "'", lexer.pos());
	}

	private int binding(Token token) {
		return switch (token) {
			case ADD, MIN      -> PREC_ADD;
			case MUL, DIV, MOD -> PREC_MUL;
			default             -> -1;
		};
	}

	private Node prefix() throws MathParserException {
		Token token = lexer.next();
		return switch (token) {
			case IDE -> postfix(new IdentifierNode(lexer.token()));
			case NUM -> new NumberNode(lexer.token().toDouble());
			case STR -> new StringNode(lexer.token());
			case ADD, MIN -> new UnaryNode(token, expression(PREC_PREFIX));
			case L_PAR -> {
				Node node = expression(0);
				expect(Token.R_PAR);
				yield postfix(node);
			}
			default -> throw new MathParserException("Unexpected token " + token, lexer.pos());
		};
	}

	private Node postfix(Node node) throws MathParserException {
		while (true) switch (lexer.lookahead()) {
			case DOT -> {
				lexer.next();
				expect(Token.IDE);
				node = new MemberNode(node, lexer.token());
			}
			case L_PAR -> node = function(node);
			default -> {
				return node;
			}
		}
	}

	private Node function(Node target) throws MathParserException {
		expect(Token.L_PAR);
		List<Node> args = new ArrayList<>();
		if (lexer.lookahead() != Token.R_PAR) do {
			args.add(expression(0));
			if (lexer.lookahead() != Token.COM) break;
			lexer.next();
		} while (true);
		expect(Token.R_PAR);
		return new CallNode(target, args);
	}

	private Node expression(int limit) throws MathParserException {
		Node left = prefix();
		while (true) {
			Token operator = lexer.lookahead();
			int bp = binding(operator);
			if (bp < limit) break;

			lexer.next();

			Node right = expression(bp + 1);
			left = new BinaryNode(left, operator, right);
		}
		return left;
	}
}