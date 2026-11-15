package io.phanisment.urars.parser.file;

import io.phanisment.urars.parser.file.config.LineConfig;
import io.phanisment.urars.util.BufferedString;
import io.phanisment.urars.util.ParserUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IDENTIFIER{KEY=VALUE;KEY=VALUE} CONTEXT
 */
public class LineParser {
	private final BufferedString line;

	private String key;
	private final Map<BufferedString, Object> arguments = new HashMap<>(8);
	private BufferedString context;

	/**
	 * Shortcut to test parsing with string
	 *
	 * @param line to parse
	 */
	public LineParser(final String line) {
		this(new BufferedString(line));
	}

	public LineParser(final BufferedString line) {
		this.line = line;
	}

	public String key() {
		return key;
	}

	public BufferedString context() {
		return context;
	}

	public LineConfig config() {
		return new LineConfig(key, arguments, context);
	}

	public void parse() throws ParseException {
		parse(true);
	}

	public void parse(final boolean include_context) throws ParseException {
		final byte[] buffer = line.buffer;
		final int end = line.end;
		int start = line.start;

		// skip whitespace
		while (start < end && ParserUtils.isWhitespace(buffer[start])) start++;
		if (start >= end) return;

		// key
		int key_start = start;
		while (start < end && buffer[start] != '{' && !ParserUtils.isWhitespace(buffer[start])) start++;
		key = new String(buffer, key_start, start - key_start); // Because String.equals not support BufferedString, cant get data in map using BufferedString

		while (start < end && ParserUtils.isWhitespace(buffer[start])) start++;
		if (start >= end) return;

		// arguments
		if (buffer[start] == '{') {
			int arg_start = ++start;
			int depth = 1;
			int seg_start = arg_start;
			int nest = 0; // depth inside the arg list for ';' splitting

			while (start < end) {
				byte c = buffer[start];
				if (c == '{' || c == '[') {
					depth++;
					nest++;
				} else if (c == '}' || c == ']') {
					depth--;
					if (c == '}' && depth == 0) {
						splitArg(seg_start, start);
						start++;
						break;
					}
					nest--;
				} else if (c == ';' && nest == 0) {
					splitArg(seg_start, start);
					seg_start = start + 1;
				}
				start++;
			}
			if (depth != 0) throw new ParseException("Missing '}'", start);
		}

		// context
		if (include_context) {
			while (start < end && ParserUtils.isWhitespace(buffer[start])) start++;
			if (start < end) context = new BufferedString(buffer, start, end);
		}
	}

	private void splitArg(int start, int end) {
		if (start >= end) return;
		final byte[] buffer = line.buffer;

		int eq = -1;
		int depth = 0;
		for (int i = start; i < end; i++) {
			byte b = buffer[i];
			if (b == '{' || b == '[') depth++;
			else if (b == '}' || b == ']') depth--;
			else if (b == '=' && depth == 0) {
				eq = i;
				break;
			}
		}

		if (eq != -1) arguments.put(new BufferedString(buffer, start, eq), value(buffer, eq + 1, end));
		else arguments.put(null, value(buffer, start, end));
	}

	/**
	 * Returns a {@code List<BufferedString>} if the value is a single bracketed
	 * list, otherwise the raw {@link BufferedString}.
	 */
	private static Object value(byte[] buf, int start, int end) {
		int s = start;
		int e = end;
		while (s < e && ParserUtils.isWhitespace(buf[s])) s++;
		while (e > s && ParserUtils.isWhitespace(buf[e - 1])) e--;

		if (s < e && buf[s] == '[' && closingBracket(buf, s, e) == e - 1) return parseList(buf, s + 1, e - 1);
		return new BufferedString(buf, start, end);
	}

	/** Index of the ']' matching the '[' at {@code open}, or -1. */
	private static int closingBracket(byte[] buf, int open, int end) {
		int depth = 0;
		for (int i = open; i < end; i++) {
			byte b = buf[i];
			if (b == '[' || b == '{') depth++;
			else if (b == ']' || b == '}') {
				depth--;
				if (depth == 0) return b == ']' ? i : -1;
			}
		}
		return -1;
	}

	/**
	 * Splits list content (between the brackets) into non-blank elements.
	 * Separators inside nested braces/brackets are ignored.
	 */
	private static List<BufferedString> parseList(byte[] buf, int start, int end) {
		List<BufferedString> result = new ArrayList<>();
		int current = start;
		int depth = 0;

		for (int i = start; i < end; i++) {
			byte b = buf[i];
			if (b == '{' || b == '[') depth++;
			else if (b == '}' || b == ']') depth--;
			else if (depth == 0 && (b == ';' || b == '-')) {
				addIfNotBlank(result, buf, current, i);
				current = i + 1;
			}
		}
		addIfNotBlank(result, buf, current, end);
		return result;
	}

	private static void addIfNotBlank(List<BufferedString> result, byte[] buf, int start, int end) {
		if (start >= end) return;
		var item = new BufferedString(buf, start, end);
		if (!item.isBlank()) result.add(item);
	}


	@Override
	public String toString() {
		return key + " " + arguments + " " + context;
	}
}