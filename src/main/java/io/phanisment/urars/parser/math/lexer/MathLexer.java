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
		}

		if (isDigit(c)) {
			start = pos;
			boolean dot = false;

			while ((c = peek()) != -1) {
				if (isDigit(c)) {
					skip();
					continue;
				}
				if (c == '.' && !dot) {
					dot = true;
					skip();
					continue;
				}
				break;
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

	public BufferedString token() {
		return new BufferedString(input.buffer, start, end);
	}

	public static enum Token {
		IDE(-1),
		STR(-1),
		NUM(-1),
		ADD('+'),
		MIN('-'),
		MUL('*'),
		DIV('/'),
		MOD('%'),
		DOT('.'),
		COM(','),
		L_PAR('('),
		R_PAR(')'),
		EOF(-1);

		int alias;
		Token(int alias) {
			this.alias = alias;
		}

		public String alias() {
			return alias != -1 ? String.valueOf((char)alias) : name();
		}
	}
}