package io.phanisment.urars.parser;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.phanisment.urars.util.ParserUtils.*;

/**
 * A lightweight string representation backed by a byte array.
 *
 * <p>Unlike {@link String}, this class supports zero-copy slicing.
 * Operations such as {@link #slice(int, int)}, {@link #trim()}, and
 * {@link #subSequence(int, int)} share the original buffer instead
 * of allocating a new byte array.</p>
 *
 * <p>The actual string content is determined by three fields:</p>
 * <ul>
 *     <li>{@code buffer}: The underlying byte array.</li>
 *     <li>{@code start}: The inclusive starting index.</li>
 *     <li>{@code end}: The exclusive ending index.</li>
 * </ul>
 *
 * <p>For example, a buffer containing {@code "Hello World"} can represent
 * {@code "Hello"} using {@code start = 0} and {@code end = 5} without
 * creating another array.</p>
 *
 * <p>This class also supports comparison with {@link String} instances.
 * This is useful when using {@code BufferedString} as a key in a map
 * whose keys are regular strings:</p>
 *
 * <pre>{@code
 * var map = Map.of("Example", true);
 * map.get(new BufferedString("Example")); // true
 * }</pre>
 *
 * <p>However, the reverse lookup does not work because
 * {@link String#equals(Object)} does not recognize {@code BufferedString}.</p>
 *
 * <p><b>Important:</b> This class is mutable. Its buffer and range can be
 * changed through {@link #reset(byte[])}, {@link #reset(byte[], int, int)},
 * or direct field access. Do not modify instances while they are being
 * used as map keys.</p>
 *
 * <p>The byte-oriented operations assume that the input represents
 * single-byte characters. This implementation is primarily intended
 * for parser input rather than general Unicode text processing.</p>
 */
public class BufferedString implements CharSequence, Comparable<BufferedString>, Cloneable {
	private static final double[] POW10 = {
			1e0, 1e1, 1e2, 1e3, 1e4, 1e5, 1e6, 1e7, 1e8, 1e9, 1e10, 1e11,
			1e12, 1e13, 1e14, 1e15, 1e16, 1e17, 1e18, 1e19, 1e20, 1e21, 1e22
	};

	public byte[] buffer;
	public int start;
	public int end;

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

	/**
	 * Replaces the underlying buffer and resets the range to cover the
	 * entire supplied array.
	 *
	 * <p>A null input is treated as an empty byte array.</p>
	 *
	 * @param buffer the new byte array
	 * @return this instance for method chaining
	 */
	public BufferedString reset(byte @Nullable [] buffer) {
		return reset(buffer == null ? new byte[0] : buffer, 0, buffer == null ? 0 : buffer.length);
	}

	/**
	 * Replaces the underlying buffer and its active range.
	 *
	 * <p>No data is copied. The instance will directly reference
	 * the supplied array.</p>
	 *
	 * @param buffer the new byte array
	 * @param start the inclusive starting index
	 * @param end the exclusive ending index
	 * @return this instance for method chaining
	 */
	public BufferedString reset(byte @NonNull [] buffer, int start, int end) {
		this.buffer = buffer;
		this.start = start;
		this.end = end;
		return this;
	}

	// From here is utility method, used for other developer.

	public BufferedString slice(int start) throws IndexOutOfBoundsException {
		return slice(start, length());
	}

	public BufferedString slice(int start,  int end) throws IndexOutOfBoundsException {
		if (start < 0 || start > end || end > length()) throw new IndexOutOfBoundsException(start);
		return new BufferedString(buffer, this.start + start, this.start + end);
	}

	public BufferedString[] split(char separator) {
		return split(separator, Integer.MAX_VALUE);
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

	public BufferedString[] splitByDepth(char separator, DepthSeparator... depths) {
		if (this.length() == 0) return new BufferedString[]{ this };

		List<BufferedString> result = new ArrayList<>();
		byte target = (byte) separator;
		int current = this.start;
		int depth = 0;

		for (int i = this.start; i < this.end; i++) {
			byte b = buffer[i];
			for (DepthSeparator d : depths) {
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

	public boolean startWith(BufferedString prefix) {
		if (prefix.length() > length()) return false;
		for (int i = 0; i < prefix.length(); i++) if (byteAt(i) != prefix.byteAt(i)) return false;
		return true;
	}

	public BufferedString unescape() {
		if (!contains((byte)'\\')) return this;
		byte[] out = new byte[length()];
		int n = 0;
		for (int i = start; i < end; i++) {
			byte b = buffer[i];
			if (b == '\\' && i + 1 < end) b = buffer[++i];
			out[n++] = b;
		}
		return new BufferedString(out, 0, n);
	}

	public BufferedString trim() {
		int start = this.start;
		int end = this.end;
		while (start < end && isWhitespace(buffer[start])) start++;
		while (end > start && isWhitespace(buffer[end - 1])) end--;
		return new BufferedString(buffer, start, end);
	}

	public BufferedString trimStart() {
		int start = this.start;
		while (start < this.end && isWhitespace(buffer[start])) start++;
		return new BufferedString(buffer, start, this.end);
	}

	public BufferedString trimEnd() {
		int end = this.end;
		while (end > this.start && isWhitespace(buffer[end - 1])) end--;
		return new BufferedString(buffer, this.start, end);
	}

	@Override
	public @NonNull BufferedString subSequence(int start, int end) {
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
		if (buffer[start] == '-') {
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
	
	public BufferedString copy() {
		return new BufferedString(buffer, start, end);
	}

	@Override
	public BufferedString clone() throws CloneNotSupportedException {
		return (BufferedString)super.clone();
	}

	@Override
	public @NonNull String toString() {
		if (isEmpty()) return "";
		return new String(buffer, start, length());
	}

	public int toColor() throws NumberFormatException {
		if (isEmpty()) throw new NumberFormatException("Empty color");
		int r, g, b, a = 255;
		if (byteAt(0) == '#') {
			int digits = length() - 1;
			if (digits != 6 && digits != 8) throw new NumberFormatException("Invalid hex color length: \"" + this + "\"");
			r = hexByteAt(1);
			g = hexByteAt(3);
			b = hexByteAt(5);
			if (digits == 8) a = hexByteAt(7);
		} else {
			BufferedString[] parts = split(',');
			if (parts.length != 3 && parts.length != 4) throw new NumberFormatException("Invalid color, expected 3 or 4 components: \"" + this + "\"");
			r = colorComponent(parts[0]);
			g = colorComponent(parts[1]);
			b = colorComponent(parts[2]);
			if (parts.length == 4) a = colorComponent(parts[3]);
		}
		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	private static int colorComponent(BufferedString part) throws NumberFormatException {
		if (part.isEmpty()) throw new NumberFormatException("Empty color component");
		long v = part.toLong();
		if (v < 0 || v > 255) throw new NumberFormatException("Color component out of range (0-255): " + v);
		return (int) v;
	}

	// Reads 2 hex digits starting at index -> 0..255
	private int hexByteAt(int index) throws NumberFormatException {
		return (hexDigit(byteAt(index)) << 4) | hexDigit(byteAt(index + 1));
	}

	private static int hexDigit(byte c) throws NumberFormatException {
		if (c >= '0' && c <= '9') return c - '0';
		if (c >= 'a' && c <= 'f') return c - 'a' + 10;
		if (c >= 'A' && c <= 'F') return c - 'A' + 10;
		throw new NumberFormatException("Invalid hex digit: '" + (char)c + "'");
	}

	public Number toNumber() throws NumberFormatException {
		return toNumber(buffer, start, end);
	}

	public static Number toNumber(byte[] buffer, int start, int end) throws NumberFormatException {
		if (start < 0 || end < start || end > buffer.length) throw new IndexOutOfBoundsException();
		if (start == end) throw new NumberFormatException("Empty number");

		byte last = buffer[end - 1];

		// Explicit Long: 1L
		if (last == 'l' || last == 'L') return toLong(buffer, start, end - 1);

		// Explicit Double: 1.0D or 1D
		if (last == 'd' || last == 'D') return toDouble(buffer, start, end - 1);

		// Explicit Float: 1.0F or 1F
		if (last == 'f' || last == 'F') return toFloat(buffer, start, end - 1);

		// Default type inference
		for (int i = start; i < end; i++) if (buffer[i] == '.') return toDouble(buffer, start, end);

		long value = toLong(buffer, start, end);
		if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) return (int)value;
		return value;
	}

	public int toInteger() throws NumberFormatException {
		return toInteger(buffer, start, end);
	}

	public static int toInteger(byte[] buffer, int start, int end) throws NumberFormatException {
		long v = toLong(buffer, start, end);
		if (v < Integer.MIN_VALUE || v > Integer.MAX_VALUE) throw new NumberFormatException("Int overflow: " + v);
		return (int)v;
	}

	public float toFloat() throws NumberFormatException {
		return (float)toDouble();
	}

	public static float toFloat(byte[] buffer, int start, int end) throws NumberFormatException {
		return (float)toDouble(buffer, start, end);
	}

	public double toDouble() throws NumberFormatException {
		return toDouble(this.buffer, this.start, this.end);
	}

	public static double toDouble(byte[] buffer, int start, int end) throws NumberFormatException {
		if (start == end) throw new NumberFormatException("Empty number");

		int pos = start;
		boolean negative = false;
		if (buffer[pos] == '-') {
			negative = true;
			pos++;
		}

		long mantissa = 0;
		int scale = 0;
		boolean dot = false, any_digit = false;

		for (; pos < end; pos++) {
			int c = buffer[pos];
			if (c == '.') {
				if (dot) throw invalidNumber(buffer, start, end, pos, "double");
				dot = true;
				continue;
			}
			if (c == ',' || c == '_') continue;
			if (c < '0' || c > '9') throw invalidNumber(buffer, start, end, pos, "double");
			any_digit = true;
			mantissa = mantissa * 10 + (c - '0');
			if (dot) scale++;
			if (mantissa > 1L << 53) return slowDouble(buffer, start, end);
		}

		if (!any_digit) throw new NumberFormatException("No digits: \"" + new String(buffer, start, end - start) + "\"");
		if (scale > 22) return slowDouble(buffer, start, end);

		double v = scale == 0 ? (double)mantissa : mantissa / POW10[scale];
		return negative ? -v : v;
	}

	private static double slowDouble(byte[] buffer, int start, int end) {
		boolean hasSep = false;
		for (int i = start; i < end; i++) {
			byte c = buffer[i];
			if (c == ',' || c == '_') {
				hasSep = true;
				break;
			}
		}
		if (!hasSep) return Double.parseDouble(new String(buffer, start, end - start, java.nio.charset.StandardCharsets.ISO_8859_1));

		// otherwise strip separators
		StringBuilder sb = new StringBuilder(end - start);
		for (int i = start; i < end; i++) {
			char c = (char) (buffer[i] & 0xFF);
			if (c != ',' && c != '_') sb.append(c);
		}
		return Double.parseDouble(sb.toString());
	}

	public long toLong() throws NumberFormatException {
		return toLong(this.buffer, this.start, this.end);
	}

	public static long toLong(byte[] buffer, int start, int end) throws NumberFormatException {
		if (start == end) throw new NumberFormatException("Empty number");

		int pos = start;
		boolean negative = false;
		if (buffer[pos] == '-') {
			negative = true;
			pos++;
		}

		long value = 0;
		boolean any_digit = false;
		final long limit = negative ? Long.MIN_VALUE : -Long.MAX_VALUE;

		for (; pos < end; pos++) {
			int c = buffer[pos];
			if (c == ',' || c == '_') continue;
			if (c < '0' || c > '9') throw invalidNumber(buffer, start, end, pos, "long");
			int d = c - '0';
			any_digit = true;
			if (value < (limit + d) / 10) throw new NumberFormatException("Long overflow: \"" + new String(buffer, start, end - start) + "\"");
			value = value * 10 - d;
		}
		if (!any_digit) throw new NumberFormatException("No digits");
		return negative ? value : -value;
	}

	private static NumberFormatException invalidNumber(byte[] buffer, int start, int end, int index, String type) {
		return new NumberFormatException("Invalid character '" + ((char)buffer[index]) + "' at index " + index + " while parsing " + type + ": \"" + new String(buffer, start, end - start) + "\"");
	}

	public boolean toBoolean() {
		if (length() == 4) return isTrue();
		if (length() == 3) return isYes();
		if (length() == 1) return buffer[start] == '1';
		return false;
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

	public record DepthSeparator(char start, char end) {
	}
}