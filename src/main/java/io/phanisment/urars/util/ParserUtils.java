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

	public static boolean isLetter(int c) {
		return (c >= 'A' && c <= 'Z')
				|| (c >= 'a' && c <= 'z');
	}

	public static int toUppercase(int c) {
		if (c >= 'a' && c <= 'z') return c - 32;
		return c;
	}

	public static int toLowercase(int c) {
		if (c >= 'A' && c <= 'Z') return c + 32;
		return c;
	}
}
