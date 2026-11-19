package io.phanisment.urars.parser.file;

import io.phanisment.urars.parser.file.config.LineConfig;
import io.phanisment.urars.parser.BufferedString;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.phanisment.urars.util.ParserUtils.isWhitespace;

/**
 * IDENTIFIER{KEY=VALUE;KEY=VALUE} CONTEXT
 */
public final class LineParser {
	private final byte[] buffer;
	private final int end;
	private int pos;

	private String key;
	private final Map<BufferedString, Object> arguments = new HashMap<>(4);
	private BufferedString context;

	private LineParser(final byte[] buffer, final int start, final int end) {
		this.buffer = buffer;
		this.pos = start;
		this.end = end;
	}

	/** Shortcut to test parsing with a string. */
	public static LineConfig parse(final String line) throws ParseException {
		return parse(new BufferedString(line), true);
	}

	public static LineConfig parse(final BufferedString line) throws ParseException {
		return parse(line.buffer, line.start, line.end, true);
	}

	public static LineConfig parse(final BufferedString line, final boolean include_context) throws ParseException {
		return parse(line.buffer, line.start, line.end, include_context);
	}

	public static LineConfig parse(final byte[] buffer, final int start, final int end, final boolean include_context) throws ParseException {
		return new LineParser(buffer, start, end).run(include_context);
	}

	private LineConfig run(final boolean include_context) throws ParseException {
		skipWhitespace();
		if (pos >= end) return config();

		// key
		final int key_start = pos;
		while (pos < end && buffer[pos] != '{' && !isWhitespace(buffer[pos])) pos++;
		key = new String(buffer, key_start, pos - key_start); // String.equals does not support BufferedString, so the key stays a String

		skipWhitespace();
		if (pos >= end) return config();

		// arguments
		if (buffer[pos] == '{') parseArguments();

		// context
		if (include_context) {
			skipWhitespace();
			if (pos < end) context = new BufferedString(buffer, pos, end);
		}
		return config();
	}

	private void parseArguments() throws ParseException {
		int depth = 1;
		int seg_start = ++pos;
		int nest = 0; // depth inside the arg list for ';' splitting

		while (pos < end) {
			final byte c = buffer[pos];
			if (c == '{' || c == '[') {
				depth++;
				nest++;
			} else if (c == '}' || c == ']') {
				depth--;
				if (c == '}' && depth == 0) {
					splitArg(seg_start, pos);
					pos++;
					break;
				}
				nest--;
			} else if (c == ';' && nest == 0) {
				splitArg(seg_start, pos);
				seg_start = pos + 1;
			}
			pos++;
		}
		if (depth != 0) throw new ParseException("Missing '}'", pos);
	}

	private void skipWhitespace() {
		while (pos < end && isWhitespace(buffer[pos])) pos++;
	}

	private LineConfig config() {
		return new LineConfig(key, arguments, context);
	}

	private void splitArg(final int start, final int end) {
		if (start >= end) return;

		int eq = -1;
		int depth = 0;
		for (int i = start; i < end; i++) {
			final byte b = buffer[i];
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
	private static Object value(final byte[] buf, final int start, final int end) {
		int s = start;
		int e = end;
		while (s < e && isWhitespace(buf[s])) s++;
		while (e > s && isWhitespace(buf[e - 1])) e--;

		if (s < e && buf[s] == '[' && closingBracket(buf, s, e) == e - 1) return parseList(buf, s + 1, e - 1);
		return new BufferedString(buf, start, end);
	}

	/** Index of the ']' matching the '[' at {@code open}, or -1. */
	private static int closingBracket(final byte[] buf, final int open, final int end) {
		int depth = 0;
		for (int i = open; i < end; i++) {
			final byte b = buf[i];
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
	private static List<BufferedString> parseList(final byte[] buf, final int start, final int end) {
		final List<BufferedString> result = new ArrayList<>();
		int current = start;
		int depth = 0;

		for (int i = start; i < end; i++) {
			final byte b = buf[i];
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

	private static void addIfNotBlank(final List<BufferedString> result, final byte[] buf, final int start, final int end) {
		if (start >= end) return;
		final var item = new BufferedString(buf, start, end);
		if (!item.isBlank()) result.add(item);
	}
}