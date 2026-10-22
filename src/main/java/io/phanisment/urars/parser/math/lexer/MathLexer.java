package io.phanisment.urars.parser.math.lexer;

import io.phanisment.urars.exception.MathParserException;
import io.phanisment.urars.util.BufferedString;

public class MathLexer {
	private final BufferedString input;
	private int pos, start, end;

	public MathLexer(BufferedString input) {
		this.input = input;
		this.pos = input.start;
	}

	public int pos() {
		return pos;
	}

	private int peek() {
		if (pos >= input.end) return -1;
		return input.buffer[pos] & 0xFF;
	}

	private void skip() {
		if (pos >= input.end) return;
		pos++;
	}
	private int peekAt(int offset) {
		int p = pos + offset;
		if (p >= input.end) return -1;
		return input.buffer[p] & 0xFF;
	}

	private boolean isSuffix(int c) {
		return c == 'l' || c == 'L'
				|| c == 'd' || c == 'D'
				|| c == 'f' || c == 'F';
	}

	private void skipWhitespace() {
		while (peek() != -1 && isWhitespace(peek())) skip();
	}

	private boolean isWhitespace(int c) {
		return c == ' '
				|| c == '\t'
				|| c == '\n'
				|| c == '\r';
	}

	public Token lookahead() throws MathParserException {
		int pos = this.pos, start = this.start, end = this.end;

		Token token = next();
		this.pos = pos;
		this.start = start;
		this.end = end;
		return token;
	}

	private boolean isIdentifier(int c) {
		return (c >= 'A' && c <= 'Z')
				|| (c >= 'a' && c <= 'z')
				|| c == '_';
	}

	private boolean isIdentifierPart(int c) {
		return isIdentifier(c) || isDigit(c);
	}

	private boolean isDigit(int c) {
		return c >= '0' && c <= '9';
	}

	public Token next() throws MathParserException {
		skipWhitespace();

		int c = peek();
		if (c == -1) return Token.EOF;

		switch (c) {
			case '+' -> {
				start = pos;
				skip();
				end = pos;
				return Token.ADD;
			} 
			case '-' -> {
				start = pos;
				skip();
				end = pos;
				return Token.MIN;
			}
			case '*' -> {
				start = pos;
				skip();
				end = pos;
				return Token.MUL;
			}
			case '/' -> {
				start = pos;
				skip();
				end = pos;
				return Token.DIV;
			}
			case '%' -> {
				start = pos;
				skip();
				end = pos;
				return Token.MOD;
			}
			case '.' -> {
				start = pos;
				skip();
				end = pos;
				return Token.DOT;
			}
			case ',' -> {
				start = pos;
				skip();
				end = pos;
				return Token.COM;
			}
			case '(' -> {
				start = pos;
				skip();
				end = pos;
				return Token.L_PAR;
			}
			case ')' -> {
				start = pos;
				skip();
				end = pos;
				return Token.R_PAR;
			}
			case '"' -> {
				skip();
				start = pos;
				while (peek() != -1 && peek() != '"') skip();
				end = pos;
				skip();
				return Token.STR;
			}
			case '!' -> {
				return peekAt(1) == '=' ? op(2, Token.NEQ) : op(1, Token.NOT);
			}
			case '<' -> {
				return peekAt(1) == '=' ? op(2, Token.LTE) : op(1, Token.LT);
			}
			case '>' -> {
				return peekAt(1) == '=' ? op(2, Token.GTE) : op(1, Token.GT);
			}
			case '=' -> {
				if (peekAt(1) == '=') return op(2, Token.EQ);
				throw new MathParserException("Unexpected character '=', did you mean '=='?", pos);
			}
			case '&' -> {
				if (peekAt(1) == '&') return op(2, Token.AND);
				throw new MathParserException("Unexpected character '&', did you mean '&&'?", pos);
			}
			case '|' -> {
				if (peekAt(1) == '|') return op(2, Token.OR);
				throw new MathParserException("Unexpected character '|', did you mean '||'?", pos);
			}
			case '?' -> {
				return op(1, Token.QUE);
			}
			case ':' -> {
				return op(1, Token.COL);
			}
		}

		if (isDigit(c)) {
			start = pos;
			boolean dot = false;

			while ((c = peek()) != -1) {
				if (isDigit(c) || (c == '_' && isDigit(peekAt(1)))) {
					skip();
					continue;
				}

				if (c == '.' && !dot && isDigit(peekAt(1))) {
					dot = true;
					skip();
					continue;
				}
				break;
			}

			c = peek();
			if (isSuffix(c) && !isIdentifierPart(peekAt(1))) {
				if (dot && (c == 'l' || c == 'L')) {
					throw new MathParserException("Long suffix cannot be used on a decimal number", pos);
				}
				skip();
			}

			end = pos;
			return Token.NUM;
		} else if (isIdentifier(c)) {
			start = pos;
			while ((c = peek()) != -1 && isIdentifierPart(c)) skip();
			end = pos;
			return Token.IDE;
		}

		throw new MathParserException("Unexpected character '" + (char)c + "'", pos);
	}

	public BufferedString slice() {
		return new BufferedString(input.buffer, start, end);
	}

	private Token op(int len, Token token) {
		start = pos;
		pos += len;
		end = pos;
		return token;
	}

	public enum Token {
		IDE, STR, NUM,
		ADD("+"), MIN("-"), MUL("*"), DIV("/"), MOD("%"),
		DOT("."), COM(","), L_PAR("("), R_PAR(")"),
		EQ("=="), NEQ("!="), LT("<"), LTE("<="), GT(">"), GTE(">="),
		AND("&&"), OR("||"), NOT("!"),
		QUE("?"), COL(":"),
		EOF;

		private final String alias;
		Token() {
			this(null);
		}

		Token(String alias) {
			this.alias = alias;
		}

		public String alias() {
			return alias != null ? alias : name();
		}
	}
}