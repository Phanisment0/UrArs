package io.phanisment.urars.lib.file_parser.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BufferedString implements CharSequence, Comparable<BufferedString>, Cloneable {
	public byte[] buffer = new byte[0];
	public int start = 0;
	public int end = 0;

	public BufferedString(final String buffer) {
		this(buffer.getBytes());
	}

	public BufferedString(final byte[] buffer) {
		this(buffer, 0, buffer != null ? buffer.length : 0);
	}

	public BufferedString(final byte[] buffer, final int start, final int end) {
		this.buffer = buffer == null ? new byte[0] : buffer;
		this.start = start;
		this.end = end;
	}

	public byte[] buffer() {
		return buffer;
	}

	public int start() {
		return start;
	}

	public int end() {
		return end;
	}
	
	@Override
	public int length() {
		return end - start;
	}

	public static BufferedString empty() {
		return new BufferedString(new byte[0], 0, 0);
	}

	public void reset(byte[] buffer) {
		this.buffer =  buffer == null ? new byte[0] : buffer;
		this.start = 0;
		this.end = buffer == null ? 0 : buffer.length;
	}

	// From here is utility method, used for other developer.

	public BufferedString slice(int start) throws IndexOutOfBoundsException {
		return slice(start, length());
	}

	public BufferedString slice(int start,  int end) throws IndexOutOfBoundsException {
		if (start < 0 || start > end || end > length()) throw new IndexOutOfBoundsException(start);
		return new BufferedString(buffer, this.start + start, this.start + end);
	}

	public BufferedString[] split(char sparator) {
		return split(sparator, Integer.MAX_VALUE);
	}

	public BufferedString[] split(char separator, int max) {
		if (max <= 0) return new BufferedString[0];
		if (this.length() == 0) return new BufferedString[]{ this };

		List<BufferedString> result = new ArrayList<>();
		int current = this.start;

		for (int i = this.start; i < this.end; i++) {
			if (result.size() == max - 1) break;
			if (buffer[i] == separator) {
				result.add(new BufferedString(buffer, current, i));
				current = i + 1;
			}
		}
		result.add(new BufferedString(buffer, current, this.end));
		return result.toArray(new BufferedString[0]);
	}

	public BufferedString[] splitByDepth(char separator, DepthSparator... depths) {
		if (this.length() == 0) return new BufferedString[]{ this };

		List<BufferedString> result = new ArrayList<>();
		byte target = (byte) separator;
		int current = this.start;
		int depth = 0;

		for (int i = this.start; i < this.end; i++) {
			byte b = buffer[i];
			for (DepthSparator d : depths) {
				if (b == (byte)d.start()) {
					depth++;
					break;
				} else if (b == (byte)d.end()) {
					depth--;
					break;
				}
			}

			if (depth <= 0 && b == target) {
				result.add(new BufferedString(buffer, current, i));
				current = i + 1;
				depth = 0;
			}
		}

		result.add(new BufferedString(buffer, current, this.end));
		return result.toArray(new BufferedString[0]);
	}

	public boolean contains(byte c) {
		int pos = start;
		while (pos < end) if (buffer[pos++] == c) return true;
		return false;
	}
	
	public int intAt(int index) throws IndexOutOfBoundsException {
		return byteAt(index) & 0xFF;
	}
	
	@Override
	public char charAt(int index) throws IndexOutOfBoundsException {
		return (char)intAt(index);
	}
	
	public byte byteAt(int index) throws IndexOutOfBoundsException {
		if (index < 0 || index >= length()) throw new IndexOutOfBoundsException(index);
		return buffer[start + index];
	}

	public int indexOf(byte c) {
		int pos = this.start;
		while (pos < end) {
			if (buffer[pos] == c) return pos - start;
			pos++;
		}
		return -1;
	}

	public boolean equalsIgnoreCase(Object o) {
		if (this == o) return true;
		if (o instanceof String str) {
			if (this.length() != str.length()) return false;
			for (int i = 0; i < this.length(); i++) {
				int c_0 = this.buffer[this.start + i] & 0xFF;
				char c_1 = str.charAt(i);
				if (toLowercase(c_0) != toLowercase(c_1)) return false;
			}
			return true;
		}
		if (o instanceof BufferedString that) {
			if (this.length() != that.length()) return false;
			for (int i = 0; i < this.length(); i++) {
				int c_0 = this.buffer[this.start + i] & 0xFF;
				int c_1 = that.buffer[that.start + i] & 0xFF;
				if (toLowercase(c_0) != toLowercase(c_1)) return false;
			}
			return true;
		}
		return false;
	}

	public boolean startsWith(BufferedString prefix) {
		if (prefix.length() > length()) return false;
		for (int i = 0; i < prefix.length(); i++) if (byteAt(i) != prefix.byteAt(i)) return false;
		return true;
	}

	public BufferedString trim() {
		int start = this.start;
		int end = this.end;
		while (start < end && ParserUtils.isWhitespace(buffer[start])) start++;
		while (end > start && ParserUtils.isWhitespace(buffer[end - 1])) end--;
		return new BufferedString(buffer, start, end);
	}

	public BufferedString trimStart() {
		int start = this.start;
		while (start < this.end && ParserUtils.isWhitespace(buffer[start])) start++;
		return new BufferedString(buffer, start, this.end);
	}

	public BufferedString trimEnd() {
		int end = this.end;
		while (end > this.start && ParserUtils.isWhitespace(buffer[end - 1])) end--;
		return new BufferedString(buffer, this.start, end);
	}

	@Override
	public BufferedString subSequence(int start, int end) {
		return slice(start, end);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o instanceof String str) {
			if (this.length() != str.length()) return false;
			for (int i = 0; i < this.length(); i++) if (this.buffer[this.start + i] != str.charAt(i)) return false;
			return true;
		}
		
		if (!(o instanceof BufferedString that)) return false;
		if (this.length() != that.length()) return false;
		for (int i = 0; i < this.length(); i++) if (this.buffer[this.start + i] != that.buffer[that.start + i]) return false;
		return true;
	}

	@Override
	public int hashCode() {
		int h = 0;
		for (int i = 0; i < this.length(); i++) h = 31 * h + buffer[this.start + i]; 
		return h;
	}

	@Override
	public int compareTo(BufferedString o) {
		int len_0 = length();
		int len_1 = o.length();

		for (int i = 0; i < Math.min(len_0, len_1); i++) {
			int c_0 = intAt(i);
			int c_1 = o.intAt(i);
			if (c_0 != c_1) return c_0 - c_1;
		}

		return len_0 - len_1;
	}

	public boolean isBlank() {
		if (isEmpty()) return true;
		for (int i = start; i < end; i++) {
			int c = buffer[i] & 0xFF;
			if (c == ' ' || c == '\t' || c == '\r' || c == '\n') continue;
			return false;
		}
		return true;
	}

	public boolean isEmpty() {
		return length() == 0;
	}

	public boolean isNumber() {
		if (length() == 0) return false;
		int i = 0;
		if (buffer[start] == '+' || buffer[start] == '-') {
			if (length() == 1) return false;
			i++;
		}
		boolean dot = false;
		for (; i < length(); i++) {
			byte c = buffer[start + i];
			if (c == '.') {
				if (dot) return false;
				dot = true;
				continue;
			}
			if (c == ',' || c == '_') continue;
			if (c < '0' || c > '9') return false;
		}
		return true;
	}

	public byte[] copyBuffer() {
		return Arrays.copyOf(buffer, buffer.length);
	}

	@Override
	public BufferedString clone() {
		return new BufferedString(buffer, start, end);
	}

	@Override
	public String toString() {
		if (this.length() == 0) return "";
		return new String(buffer, start, length());
	}

	public int toInteger() {
		return (int)toLong();
	}

	public static int toInteger(byte[] buffer, int start, int end) {
		return (int)toLong(buffer, start, end);
	}

	public float toFloat() {
		return (float)toDouble();
	}

	public static float toFloat(byte[] buffer, int start, int end) {
		return (float)toDouble(buffer, start, end);
	}

	public double toDouble() {
		return toDouble(this.buffer, this.start, this.end);
	}

	public static double toDouble(byte[] buffer, int start, int end) {
		if ((end - start) == 0) return 0.0;

		int pos = start;
		boolean negative = false;

		if (pos < end && buffer[pos] == '-') {
			negative = true;
			pos++;
		} else if (pos < end && buffer[pos] == '+') pos++;
		double value = 0;
		while (pos < end) {
			int c = buffer[pos] & 0xFF;
			if (c == '.') break;
			if (c == ',' || c == '_') {
				pos++;
				continue;
			}
			if (c < '0' || c > '9') break;
			value = value * 10 + (c - '0');
			pos++;
		}

		if (pos < end && buffer[pos] == '.') {
			pos++;
			double decimal = 0.1;
			while (pos < end) {
				int c = buffer[pos] & 0xFF;
				if (c == ',' || c == '_') {
					pos++;
					continue;
				}
				if (c >= '0' && c <= '9') {
					value += (c - '0') * decimal;
					decimal *= 0.1;
				}
				pos++;
			}
		}
		return negative ? -value : value;
	}

	public long toLong() {
		return toLong(this.buffer, this.start, this.end);
	}

	public static long toLong(byte[] buffer, int start, int end) {
		if ((end - start) == 0) return 0;

		long value = 0;
		int pos = start;
		boolean negative = false;

		if (pos < end && buffer[pos] == '-') {
			negative = true;
			pos++;
		} else if (pos < end && buffer[pos] == '+') pos++;
		while (pos < end) {
			int c = buffer[pos] & 0xFF;
			if (c == ',' || c == '_') {
				pos++;
				continue;
			}
			if (c < '0' || c > '9') break;
			value = value * 10 + (c - '0');
			pos++;
		}
		return negative ? -value : value;
	}

	public boolean toBoolean() {
		if (length() == 4) return isTrue();
		if (length() == 3) return isYes();
		if (length() == 1) return buffer[start] == '1';
		return false;
	}

	public static int toUppercase(int c) {
		if (c >= 'a' && c <= 'z') return c - 32;
		return c;
	}

	public static int toLowercase(int c) {
		if (c >= 'A' && c <= 'Z') return c + 32;
		return c;
	}

	public boolean isYes() {
		if (length() != 3) return false;
		return ((buffer[start    ] == 'y') || (buffer[start    ] == 'Y'))
				&& ((buffer[start + 1] == 'e') || (buffer[start + 1] == 'E'))
				&& ((buffer[start + 2] == 's') || (buffer[start + 2] == 'S'));
	}

	public boolean isNo() {
		if (length() != 2) return false;
		return ((buffer[start    ] == 'n') || (buffer[start    ] == 'N'))
				&& ((buffer[start + 1] == 'o') || (buffer[start + 1] == 'O'));
	}

	public boolean isTrue() {
		if (length() != 4) return false;
		return ((buffer[start    ] == 't') || (buffer[start    ] == 'T'))
				&& ((buffer[start + 1] == 'r') || (buffer[start + 1] == 'R'))
				&& ((buffer[start + 2] == 'u') || (buffer[start + 2] == 'U'))
				&& ((buffer[start + 3] == 'e') || (buffer[start + 3] == 'E'));
	}

	public boolean isFalse() {
		if (length() != 5) return false;
		return ((buffer[start    ] == 'f') || (buffer[start    ] == 'F'))
				&& ((buffer[start + 1] == 'a') || (buffer[start + 1] == 'A'))
				&& ((buffer[start + 2] == 'l') || (buffer[start + 2] == 'L'))
				&& ((buffer[start + 3] == 's') || (buffer[start + 3] == 'S'))
				&& ((buffer[start + 4] == 'e') || (buffer[start + 4] == 'E'));
	}

	public static record DepthSparator(char start, char end) {
	}
}