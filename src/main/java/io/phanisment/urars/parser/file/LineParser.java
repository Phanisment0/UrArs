package io.phanisment.urars.parser.file;

import io.phanisment.urars.parser.file.config.LineConfig;
import io.phanisment.urars.util.BufferedString;
import io.phanisment.urars.util.ParserUtils;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * IDENTIFIER{KEY=VALUE;KEY=VALUE} CONTEXT
 */
public class LineParser {
	private final BufferedString line;

	private String key;
	private final Map<BufferedString, Object> arguments = new HashMap<>(8);
	private BufferedString context;

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

	public void parse(final boolean includeContext) throws ParseException {
		final byte[] buf = line.buffer;
		final int limit = line.end;
		int i = line.start;

		// skip whitespace
		while (i < limit && ParserUtils.isWhitespace(buf[i])) i++;
		if (i >= limit) return;

		// key
		int keyStart = i;
		while (i < limit && buf[i] != '{' && !ParserUtils.isWhitespace(buf[i])) i++;
		key = new String(buf, keyStart, i - keyStart, StandardCharsets.UTF_8);

		while (i < limit && ParserUtils.isWhitespace(buf[i])) i++;
		if (i >= limit) return;

		// arguments
		if (buf[i] == '{') {
			int argStart = ++i;
			int depth = 1;
			int seg_start = argStart;
			int nest = 0; // depth inside the arg list for ';' splitting

			while (i < limit) {
				byte c = buf[i];
				if (c == '{' || c == '[') {
					depth++;
					nest++;
				} else if (c == '}' || c == ']') {
					depth--;
					if (c == '}' && depth == 0) {
						splitArg(seg_start, i);
						i++;
						break;
					}
					nest--;
				} else if (c == ';' && nest == 0) {
					splitArg(seg_start, i);
					seg_start = i + 1;
				}
				i++;
			}
			if (depth != 0) throw new ParseException("Missing '}'", i);
		}

		// context
		if (includeContext) {
			while (i < limit && ParserUtils.isWhitespace(buf[i])) i++;
			if (i < limit) context = new BufferedString(buf, i, limit);
		}
	}

	private void splitArg(int start, int end) {
		if (start >= end) return;
		final byte[] buf = line.buffer;

		int eq = -1;
		for (int i = start; i < end; i++) if (buf[i] == '=') {
			eq = i;
			break;
		}

		if (eq != -1) arguments.put(new BufferedString(buf, start, eq), new BufferedString(buf, eq + 1, end));
		else arguments.put(null, new BufferedString(buf, start, end));
	}

	@Override
	public String toString() {
		return key + " " + arguments + " " + context;
	}
}