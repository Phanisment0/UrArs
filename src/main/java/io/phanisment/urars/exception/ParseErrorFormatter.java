package io.phanisment.urars.exception;

import java.nio.charset.StandardCharsets;

/**
 * <file>:<line>: error: <message>
 *
 *  1 | player {
 *  2 |     health == 20
 *    |            ^^
 *  3 | }
 */
public final class ParseErrorFormatter {

	private ParseErrorFormatter() {
	}

	public static String format(String file, UrArsParseException err, byte[] source) {
		return format(file, err, source, 1);
	}

	public static String format(String file, UrArsParseException err, byte[] source, int context_line) {
		String text = new String(source, StandardCharsets.UTF_8);

		String[] lines = text.split("\\R", -1);
		var loc = err.location();

		int line = loc.line();
		int column = loc.column();

		int from = Math.max(1, line - context_line);
		int to = Math.min(lines.length, line + context_line);

		int width = Integer.toString(to).length();

		StringBuilder out = new StringBuilder();

		out.append(file)
			.append(':')
			.append(line)
			.append(": error: ")
			.append(err.getMessage())
			.append('\n')
			.append('\n');

		for (int current = from; current <= to; current++) {
			out.append(String.format("%%%dd | %%s%%n".formatted(width), current, lines[current - 1]));
			if (current == line) {
				out.repeat(" ", width);
				out.append(" | ");
				out.repeat(" ", Math.max(0, column - 1));
				int len = Math.max(1, loc.length());
				out.repeat("^", len);
				out.append('\n');
			}
		}
		return out.toString();
	}
}