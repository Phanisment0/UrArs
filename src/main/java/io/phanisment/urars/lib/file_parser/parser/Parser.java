package io.phanisment.urars.lib.file_parser.parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import io.phanisment.urars.lib.file_parser.tokenize.Lexer;
import io.phanisment.urars.lib.file_parser.tokenize.Lexer.Token;
import io.phanisment.urars.lib.file_parser.util.BufferedString;

public class Parser {
	private final Lexer lexer;

	public Parser() {
		this(new byte[0]);
	}

	public Parser(File file) throws IOException {
		this(file.toPath());
	}

	public Parser(Path path) throws IOException {
		this(Files.readAllBytes(path));
	}

	public Parser(String string) {
		this(string.getBytes(StandardCharsets.UTF_8));
	}

	public Parser(byte[] buffer) {
		this.lexer = new Lexer(buffer);
	}

	public Parser(BufferedString buffer) {
		this.lexer = new Lexer(buffer);
	}

	public static boolean isSytax(Token t) {
		return t == Token.L_BRACKET
				|| t == Token.R_BRACKET
				|| t == Token.EQUAL;
	}

	public Map<BufferedString, BufferedString> parse() throws ParseException {
		Map<BufferedString, BufferedString> result = new HashMap<>();

		while (true) {
			Token token = lexer.next();
			if (token == Token.EOF) break;
			if (token == Token.NEW_LINE) continue;
			if (token != Token.TEXT) throw new ParseException("Expected Key " + token, lexer.pos);

			BufferedString key = parseKey();

			token = lexer.next();
			if (token == Token.L_BRACKET) {
				BufferedString value = parseBlock();
				result.put(key, value);
			} else if (token == Token.EQUAL) {
				BufferedString value = parseField();
				result.put(key, value);
			} else throw new ParseException("Expected '=' or '{'", lexer.pos);
		}
		return result;
	}

	/**
	 * TEXT ... isSytax()
	 */
	private BufferedString parseKey() throws ParseException {
		int start = lexer.start;
		while (true)  {
			Token token = lexer.lookahead();
			if (token == Token.EOF) throw new ParseException("Unexpected EOF", lexer.pos);
			if (isSytax(token)) return new BufferedString(lexer.buffer, start, lexer.end);
			lexer.next();
		}
	}

	/**
	 * TEXT EQUAL TEXT
	 */
	private BufferedString parseField() throws ParseException {
		int start = lexer.end;
		while (true) {
			Token token = lexer.next();
			if (token == Token.EOF) throw new ParseException("Unexpected EOF", lexer.pos);
			if (isSytax(token) || token == Token.NEW_LINE) return new BufferedString(lexer.buffer, start, lexer.start);
		}
	}

	/**
	 * L_BRACKET ... R_BRACKET
	 */
	private BufferedString parseBlock() throws ParseException {
		int start = lexer.pos;
		int depth = 1;
		while (depth > 0) {
			Token token = lexer.next();
			if (token == Token.NEW_LINE) continue;
			if (token == Token.EOF) throw new ParseException("Missing '}'", lexer.pos);
			if (token == Token.L_BRACKET) depth++;
			else if (token == Token.R_BRACKET) depth--;
		}
		return new BufferedString(lexer.buffer, start, lexer.end);
	}

	public BufferedString token() {
		return new BufferedString(lexer.buffer, lexer.start, lexer.end);
	}

	public void reset(byte[] new_buffer) {
		this.lexer.reset(new_buffer);
	}

	public void reset(BufferedString new_buffer) {
		this.lexer.reset(new_buffer);
	}
}