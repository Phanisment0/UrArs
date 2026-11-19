package io.phanisment.urars.exception;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;

/**
 * Formats parser errors with source code context.
 *
 * Example:
 *
 *  <file>:<line>: error: <message>
 *
 *   2 | player {
 *   3 |     health == 20
 *     |            ^^
 *   4 | }
 */
public final class ParseErrorFormatter {

	private ParseErrorFormatter() {
	}

	public static String format(String file, ParseException err, byte[] source) {
		return format(file, err, source, 1);
	}

	public static String format(String file, ParseException err, byte[] source, int context_line) {
		String text = new String(source, StandardCharsets.UTF_8);
		String[] lines = text.split("\\R", -1);

		int line;
		int column;
		int length;
		String message;

		if (err instanceof UrArsParserException ex) {
			var loc = ex.location();

			line = loc.line();
			column = loc.column();
			length = loc.length();

			message = ex.rawMessage();
		} else {
			int[] pos = locate(text, err.getErrorOffset());

			line = pos[0];
			column = pos[1];
			length = 1;

			message = err.getMessage();
		}

		line = Math.clamp(line, 1, lines.length);

		String error_line = lines[line - 1];

		// Keep the underline within the source line.
		column = Math.clamp(column, 1, error_line.length() + 1);
		length = Math.clamp(length, 1, error_line.length() - column + 1);

		int from = Math.max(1, line - context_line);
		int to = Math.min(lines.length, line + context_line);

		int width = Integer.toString(to).length();

		StringBuilder out = new StringBuilder();

		out.append(file)
				.append(':')
				.append(line)
				.append(": error: ")
				.append(message)
				.append('\n');

		for (int current = from; current <= to; current++) {
			String current_line = lines[current - 1];

			out.append(String.format("%" + width + "d | %s%n", current, current_line));

			if (current == line) {
				out.repeat(" ", width);
				out.append(" | ");

				out.repeat(" ", column - 1);
				out.repeat("^", length);

				out.append('\n');
			}
		}

		return out.toString();
	}

	/**
	 * Converts a character offset into a 1-based line and column.
	 */
	private static int[] locate(String text, int offset) {
		offset = Math.clamp(offset, 0, text.length());

		int line = 1;
		int line_start = 0;

		for (int i = 0; i < offset; i++) {
			char c = text.charAt(i);
			boolean newline = c == '\n' || (c == '\r' && (i + 1 >= text.length() || text.charAt(i + 1) != '\n'));
			if (newline) {
				line++;
				line_start = i + 1;
			}
		}

		return new int[] {line, offset - line_start + 1};
	}
}
