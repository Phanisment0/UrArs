package io.phanisment.urars.lib.file_parser.parser;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import io.phanisment.urars.lib.file_parser.config.LineConfig;
import io.phanisment.urars.lib.file_parser.util.BufferedString;

/**
 * IDENTIFIER{KEY=VALUE;KEY=VALUE} CONTEXT
 */
public class LineParser {
	private final BufferedString line;

	private BufferedString key;
	private Map<BufferedString, BufferedString> arguments;
	private BufferedString context;

	private int pos;
	private int start, end = 0;

	public LineParser(BufferedString line) {
		this.line = line;
		this.pos = line.start;
	}

	public LineConfig config() {
		return new LineConfig(key, arguments, context);
	}

	public static boolean isWhitespace(int c) {
		return c == ' '
				|| c == '\t'
				|| c == '\n'
				|| c == '\r';
	}

	private int next() {
		if (pos >= line.end) return -1;
		return line.buffer[pos++] & 0xFF;
	}

	private int peek() {
		if (pos >= line.end) return -1;
		return line.buffer[pos] & 0xFF;
	}

	private void skipWhitespace() {
		while (peek() != -1 && isWhitespace(peek())) next();
	}

	public void parse() throws ParseException {
		parse(true);
	}

	public void parse(boolean include_context) throws ParseException {
		skipWhitespace();
		if (peek() == -1) return;

			// Key
		start = pos;
		while (peek() != -1 && peek() != '{' && !isWhitespace(peek())) next();
		end = pos;

		key = token();

		skipWhitespace();
		if (peek() == -1) return;

		// Arguments
		arguments = new HashMap<>();
		if (peek() == '{') {
			next();
			start = pos;
			int depth = 1;
			while (depth > 0) {
				int c = next();
				if (c == -1) throw new ParseException("Missing '}'", pos);
				if (c == '{') depth++;
				else if (c == '}') depth--;
			}
			end = pos;

			split(start, end - 1);
		}

		// Context
		if (include_context) {
			skipWhitespace();
			if (pos < line.end) context = new BufferedString(line.buffer, end, line.end);
		}
	}

	private void split(int start, int end) {
		int depth = 0;
		int current = start;
		for (int i = start; i < end; i++) {
			int c = line.buffer[i] & 0xFF;
			if (c == '{' || c == '[') depth++;
			else if (c == '}' || c == ']') depth--;
			else if (c == ';' && depth == 0) {
				splitArg(current, i);
				current = i + 1;
			}
		}
		if (start < end) splitArg(current, end);
	}

	private void splitArg(int start, int end) {
		if (start >= end) return;

		int equals_index = -1;
		for (int i = start; i < end; i++) if ((line.buffer[i] & 0xFF) == '=') {
			equals_index = i;
			break;
		}

		if (equals_index != -1) {
			var key = new BufferedString(line.buffer, start, equals_index);
			var value = new BufferedString(line.buffer, equals_index + 1, end);
			arguments.put(key, value);
		} else {
			var value = new BufferedString(line.buffer, start, end);
			arguments.put(null, value);
		}
	}

	private BufferedString token() {
		return new BufferedString(line.buffer, start, end);
	}

	@Override
	public String toString() {
		return "Line(" + key + ", " + arguments + ", " + context + ")";
	}
}