package io.phanisment.urars.parser.file;

import io.phanisment.urars.util.BufferedString;
import io.phanisment.urars.util.ParserUtils;
import org.jspecify.annotations.NonNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class FlagParser {
	private final BufferedString input;

	public FlagParser(BufferedString input) {
		this.input = input;
	}

	public List<Flag> parse() throws ParseException {
		final byte[] buf = input.buffer;
		final int limit = input.end;
		int i = input.start;

		List<Flag> result = null;

		while (i < limit) {
			// skip whitespace/newlines between flags
			int c = buf[i] & 0xFF;
			if (ParserUtils.isWhitespace(c) || ParserUtils.isNewLine(c)) {
				i++;
				continue;
			}

			if (!isPrefix(c)) throw new ParseException("Expected '@' or '~'", i);
			i++; // consume prefix

			int start = i;
			int depth = 0;
			while (i < limit) {
				int b = buf[i] & 0xFF;
				if (b == '{') {
					depth++;
				} else if (b == '}') {
					if (depth > 0) depth--;
				} else if (depth == 0 && (isPrefix(b) || ParserUtils.isNewLine(b))) break;
				i++;
			}
			if (depth != 0) throw new ParseException("Missing '}'", i);

			// trim trailing whitespace
			int end = i;
			while (end > start && ParserUtils.isWhitespace(buf[end - 1] & 0xFF)) end--;

			if (result == null) result = new ArrayList<>(2);
			result.add(new Flag(c, new BufferedString(buf, start, end)));
		}

		return result == null ? List.of() : result;
	}

	public boolean isPrefix(int c) {
		return c == '@'
				|| c == '~';
	}

	public record Flag(int c, BufferedString text) {
		@Override
		public @NonNull String toString() {
			return "[" + ((char)c) + "] " + text.toString();
		}
	}
}