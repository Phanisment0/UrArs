package io.phanisment.urars.parser.file.parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import io.phanisment.urars.exception.UrArsParseException;
import io.phanisment.urars.parser.file.tokenize.Lexer;
import io.phanisment.urars.parser.file.tokenize.Lexer.Token;
import io.phanisment.urars.util.BufferedString;

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

	public Map<String, BufferedString> parse() throws UrArsParseException {
		Map<String, BufferedString> result = new HashMap<>();

		while (true) {
			Token token = lexer.next();
			if (token == Token.EOF) break;
			if (token == Token.NEW_LINE) continue;
			if (token != Token.TEXT) throw error("Expected a key, but found " + tokenName(token) + ".");

			BufferedString key = parseKey();

			token = lexer.next();
			if (token == Token.L_BRACKET) {
				BufferedString value = parseBlock();
				result.put(key.toString(), value);
			} else if (token == Token.EQUAL) {
				BufferedString value = parseField();
				result.put(key.toString(), value);
			} else throw error("Expected '=' or '{'");
		}
		return result;
	}

	/**
	 * TEXT ... isSyntax()
	 */
	private BufferedString parseKey() throws UrArsParseException {
		int start = lexer.start;
		while (true)  {
			Token token = lexer.lookahead();
			if (token == Token.EOF) throw error("Unexpected end of input while reading a key.");
			if (isSytax(token)) return new BufferedString(lexer.buffer, start, lexer.end);
			lexer.next();
		}
	}

	/**
	 * TEXT EQUAL TEXT
	 */
	private BufferedString parseField() {
		int start = lexer.end;
		int last = start;
		while (true) {
			Token token = lexer.next();
			if (token == Token.TEXT) {
				last = lexer.end;
				continue;
			}
			if (token == Token.EOF || token == Token.NEW_LINE || isSytax(token)) break;
		}
		return new BufferedString(lexer.buffer, start, last).trimStart();
	}

	/**
	 * L_BRACKET ... R_BRACKET
	 */
	private BufferedString parseBlock() throws UrArsParseException {
		int start = lexer.pos;
		int depth = 1;
		while (depth > 0) {
			Token token = lexer.next();
			if (token == Token.NEW_LINE) continue;
			if (token == Token.EOF) throw error("Missing closing '}' before end of input.");
			if (token == Token.L_BRACKET) depth++;
			else if (token == Token.R_BRACKET) depth--;
		}
		return new BufferedString(lexer.buffer, start, lexer.start);
	}

	public BufferedString token() {
		return new BufferedString(lexer.buffer, lexer.start, lexer.end);
	}

	private static String tokenName(Token token) {
		return switch (token) {
			case L_BRACKET -> "'{'";
			case R_BRACKET -> "'}'";
			case EQUAL -> "'='";
			case NEW_LINE -> "a new line";
			case TEXT -> "text";
			case EOF -> "end of input";
		};
	}

	private UrArsParseException error(String message) {
		return new UrArsParseException(message, lexer.location());
	}

	public void reset(byte[] new_buffer) {
		this.lexer.reset(new_buffer);
	}

	public void reset(BufferedString new_buffer) {
		this.lexer.reset(new_buffer);
	}
}