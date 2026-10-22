package io.phanisment.urars.parser.file.parser;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import io.phanisment.urars.parser.file.config.LineConfig;
import io.phanisment.urars.util.BufferedString;
import io.phanisment.urars.util.ParserUtils;
import io.phanisment.urars.util.UrArsDebugger;

/**
 * IDENTIFIER{KEY=VALUE;KEY=VALUE} CONTEXT
 */
public class LineParser {
	private final BufferedString line;

	private String key;
	private final Map<String, BufferedString> arguments = new HashMap<>();
	private BufferedString context;
	private int pos, start, end;

	public LineParser(String line) {
		this(new BufferedString(line));
	}

	public LineParser(BufferedString line) {
		this.line = line;
		this.pos = line.start;
	}

	public LineConfig config() {
		return new LineConfig(key, arguments, context);
	}

	private byte next() {
		return pos >= line.end ? -1 : line.buffer[pos++];
	}

	private byte peek() {
		return pos >= line.end ? -1 : line.buffer[pos];
	}

	private void skipWhitespace() {
		while (peek() != -1 && ParserUtils.isWhitespace(peek())) next();
	}

	public void parse() throws ParseException {
		parse(true);
	}

	private void parse(boolean include_context) throws ParseException {
		skipWhitespace();
		if (peek() == -1) return;

		// Key
		start = pos;
		while (peek() != -1 && peek() != '{' && !ParserUtils.isWhitespace(peek())) next();
		end = pos;

		key = token().toString();

		skipWhitespace();
		if (peek() == -1) return;

		// Arguments
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
			if (pos < line.end) {
				BufferedString ctx = new BufferedString(line.buffer, pos, line.end);
				if (!ctx.isEmpty()) context = ctx;
			}
		}
	}

	private void split(int start, int end) {
		int depth = 0;
		int current = start;
		for (int i = start; i < end; i++) {
			byte c = line.buffer[i];
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
		for (int i = start; i < end; i++) if ((line.buffer[i]) == '=') {
			equals_index = i;
			break;
		}

		if (equals_index != -1) {
			var key = new BufferedString(line.buffer, start, equals_index);
			var value = new BufferedString(line.buffer, equals_index + 1, end);
			arguments.put(key.toString(), value);
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
		return key + " " + arguments + " " + context;
	}
}