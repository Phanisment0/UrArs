package io.phanisment.urars.parser.file.tokenize;

import io.phanisment.urars.exception.UrArsParseException.SourceLocation;
import io.phanisment.urars.util.BufferedString;
import io.phanisment.urars.util.ParserUtils;

/**
 * Tokenize byte array to more simple form(Token).
 */
public final class Lexer {
	/** Current input buffer */
	public byte[] buffer;
	public int pos = 0, length = 0, start = 0, end = 0;

	private int line = 0, column = 0;

	/**
	 * Creating lexer from {@class BufferedString}
	 * 
	 * <p>This useful for lazy parsing.</p>
	 * 
	 * @param buffer slice/entire byte array
	 */
	public Lexer(BufferedString buffer) {
		this.buffer = buffer.buffer();
		this.pos = buffer.start();
		this.start = buffer.start();
		this.end = buffer.end();
		this.length = buffer.end();
	}

	/**
	 * Create lexer that read all byte array
	 * 
	 * @param buffer byte of file/string from String.getBytes()
	 */
	public Lexer(byte[] buffer) {
		this.buffer = buffer;
		this.length = buffer.length;
	}

	/**
	 * Return true if the given character is treated as sytax token.
	 * 
	 * @param c char input
	 * @return false if not sytax token
	 */
	public static boolean isSytax(int c) {
		return c == '{'
				|| c == '}'
				|| c == '='
				|| c == '#';
	}

	/**
	 * Skip indentation characters
	 */
	private void skipIndent() {
		while (peek() != -1 && ParserUtils.isIndent(peek())) skip();
	}

	/**
	 * Consumes and return the next byte
	 * 
	 * @return the next byte, or -1 if EOF is reached.
	 */
	private byte skip() {
		if (pos >= length) return -1;
		byte c = buffer[pos++];
		if (c == '\n') {
			line++;
			column = 1;
		} else column++;
		return c;
	}

	/**
	 * Returns the next byte without consuming it.
	 *
	 * @return the next byte, or -1 if end of input is reached.
	 */
	private byte peek() {
		return pos >= length ? -1 : buffer[pos];
	}

	/**
	 * Reads the next token without advancing the lexer state.
	 *
	 * <p>This is commonly used by parsers that need to inspect the upcoming
	 * token before deciding which parsing rule to apply.</p>
	 * 
	 * @return the next token
	 */
	public Token lookahead() {
		int pos = this.pos, start = this.start, end = this.end;
		
		Token token = next();
		
		this.pos = pos;
		this.start = start;
		this.end = end;
		return token;
	}

	/**
	 * Read ad return the token.
	 * 
	 * <p>This is the core lexer and this is used mainly on parser.</p>
	 * 
	 * @return the token
	 */
	public Token next() {
		skipIndent();

		byte c = peek();
		if (c == -1) return Token.EOF;
		switch (c) {
			case '{' -> {
				start = pos;
				skip();
				end = pos;
				return Token.L_BRACKET;
			}
			case '}' -> {
				start = pos;
				skip();
				end = pos;
				return Token.R_BRACKET;
			}
			case '=' -> {
				start = pos;
				skip();
				end = pos;
				return Token.EQUAL;
			}
			case '\n' -> {
				start = pos;
				skip();
				end = pos;
				return Token.NEW_LINE;
			}
			case '#' -> {
				// Skip for new line
				byte current;
				while ((current = peek()) != -1 && !ParserUtils.isNewLine(current)) pos++;
				return next();
			}
		}

		start = pos;
		byte current;
		while ((current = peek()) != -1 && !isSytax(current) && !ParserUtils.isWhitespace(current)) pos++;
		end = pos;
		return Token.TEXT;
	}

	/**
	 * Simple utility method for getting the current slice/byte array location
	 * @return the slice/buffered string
	 */
	public BufferedString token() {
		return new BufferedString(buffer, start, end);
	}

	public SourceLocation location() {
		return new SourceLocation(line, column, start, length);
	}

	public int line() {
		return line;
	}

	public int column() {
		return column;
	}

	/**
	 * Reset field value this class
	 * 
	 * @param new_buffer replace the new buffer
	 */
	public void reset(byte[] new_buffer) {
		this.buffer = new_buffer;
		this.length = new_buffer.length;
		this.pos = 0;
		this.start = 0;
		this.end = 0;
	}

	/**
	 * Parse inside the slice/buffered string
	 * 
	 * @param new_buffer
	 */
	public void reset(BufferedString new_buffer) {
		this.buffer = new_buffer.buffer();
		this.length = new_buffer.end();
		this.pos = new_buffer.start();
		this.start = new_buffer.start();
		this.end = new_buffer.end();
	}

	/**
	 * Token types produced by lexer
	 */
	public static enum Token {

		/** '{' */
		L_BRACKET,

		/** '}' */
		R_BRACKET,

		/** '\n' */
		NEW_LINE,

		/** '=' */
		EQUAL,

		/** Any non-sytax text. */
		TEXT,

		/** End of File. */
		EOF
	}
}