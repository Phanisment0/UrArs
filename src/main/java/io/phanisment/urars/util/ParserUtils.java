package io.phanisment.urars.util;

public final class ParserUtils {
	public static boolean isWhitespace(int c) {
		return c == ' '
				|| c == '\t'
				|| c == '\n'
				|| c == '\r';
	}

	public static boolean isIndent(int c) {
		return c == ' '
				|| c == '\t'
				|| c == '\r';
	}

	public static boolean isNewLine(int c) {
		return c == '\n'
				|| c == '\r';
	}
}
