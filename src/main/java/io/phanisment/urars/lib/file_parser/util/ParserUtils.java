package io.phanisment.urars.lib.file_parser.util;

public final class ParserUtils {
	public static boolean isWhitespace(byte c) {
		return c == ' '
				|| c == '\t'
				|| c == '\n'
				|| c == '\r';
	}

	public static boolean isIndent(byte c) {
		return c == ' '
				|| c == '\t'
				|| c == '\r';
	}

	public static boolean isNewLine(byte c) {
		return c == '\n'
				|| c == '\r';
	}
}