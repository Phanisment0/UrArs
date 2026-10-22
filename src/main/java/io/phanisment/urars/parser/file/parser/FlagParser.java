package io.phanisment.urars.parser.file.parser;

import java.util.ArrayList;
import java.util.List;

import io.phanisment.urars.util.BufferedString;
import io.phanisment.urars.util.ParserUtils;
import org.jspecify.annotations.NonNull;

public class FlagParser {
	private final BufferedString input;
	private int pos = 0;

	public FlagParser(BufferedString input) {
		this.input = input;
		this.pos = input.start;
	}

	private byte next() {
		if (pos >= input.end) return -1;
		return input.buffer[pos++];
	}

	private byte peek() {
		if (pos >= input.end) return -1;
		return input.buffer[pos];
	}

	private void skipWhitespace() {
		while (peek() != -1 && ParserUtils.isWhitespace(peek())) next();
	}

	public List<Flag> parse() {
		List<Flag> result = new ArrayList<>();
		skipWhitespace();
		if (peek() == -1) return result;

		while (pos < input.end) {
			byte prefix = next();
			if (peek() == -1) break;

			int start = pos;
			int depth = 0;
			while (pos < input.end) {
				byte c = peek();

				if (c == '{') depth++;
				else if (c == '}') if (depth > 0) depth--;
				else if (depth == 0 && (isPrefix(c) || ParserUtils.isNewLine(c))) break;

				next();
			}
			int end = pos;

			result.add(new Flag(prefix, new BufferedString(input.buffer, start, end)));
		}
		return result;
	}

	public boolean isPrefix(byte c) {
		return c == '@'
				|| c == '~';
	}

	public static record Flag(byte c, BufferedString text) {
		@Override
		public @NonNull String toString() {
			return "[" + ((char)c) + "] " + text.toString();
		}
	}
}