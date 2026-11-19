package io.phanisment.urars.parser.file;

import io.phanisment.urars.parser.BufferedString;
import org.jspecify.annotations.NonNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static io.phanisment.urars.util.ParserUtils.*;

public final class FlagParser {
	private final byte[] buffer;
	private final int end;
	private int pos;

	private FlagParser(final byte[] buffer, final int start, final int end) {
		this.buffer = buffer;
		this.pos = start;
		this.end = end;
	}

	public static List<Flag> parse(final String input) throws ParseException {
		return parse(new BufferedString(input));
	}

	public static List<Flag> parse(final BufferedString input) throws ParseException {
		return parse(input.buffer, input.start, input.end);
	}

	public static List<Flag> parse(final byte[] buffer, final int start, final int end) throws ParseException {
		return new FlagParser(buffer, start, end).run();
	}

	public static boolean isPrefix(final int c) {
		return c == '@' || c == '~';
	}

	private List<Flag> run() throws ParseException {
		List<Flag> result = null;

		while (pos < end) {
			// skip whitespace/newlines between flags
			int c = buffer[pos];
			if (isWhitespace(c) || isNewLine(c)) {
				pos++;
				continue;
			}

			if (!isPrefix(c)) throw new ParseException("Expected '@' or '~'", pos);
			pos++; // consume prefix

			int text_start = pos;
			int depth = 0;
			while (pos < end) {
				int b = buffer[pos];
				if (b == '{') {
					depth++;
				} else if (b == '}') {
					if (depth > 0) depth--;
				} else if (depth == 0 && (isPrefix(b) || isNewLine(b))) break;
				pos++;
			}
			if (depth != 0) throw new ParseException("Missing '}'", pos);

			// trim trailing whitespace
			int text_end = pos;
			while (text_end > text_start && isWhitespace(buffer[text_end - 1])) text_end--;

			if (result == null) result = new ArrayList<>(2);
			result.add(new Flag(c, new BufferedString(buffer, text_start, text_end)));
		}

		return result == null ? List.of() : result;
	}

	public record Flag(int c, BufferedString text) {
		@Override
		public @NonNull String toString() {
			return "[" + ((char)c) + "] " + text.toString();
		}
	}
}