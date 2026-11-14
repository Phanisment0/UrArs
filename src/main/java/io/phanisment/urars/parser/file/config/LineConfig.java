package io.phanisment.urars.parser.file.config;

import io.phanisment.urars.parser.file.LineParser;
import io.phanisment.urars.util.BufferedString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Represents a configuration line containing a key, arguments, and context.
 *
 * <p>Unlike {@link ConfigSection}, this class works with raw
 * {@link BufferedString} values rather than nested configuration sections.
 */
public class LineConfig extends ConfigSection {
	private final String key;
	private final BufferedString context;

	public LineConfig(String key, Map<BufferedString, Object> arguments, BufferedString context) {
		super(arguments);
		this.key = key;
		this.context = context;
	}

	public String key() {
		return key;
	}

	public BufferedString context() {
		return context;
	}

	public @Nullable BufferedString getBufferedString(String... keys) {
		return find(keys);
	}

	public BufferedString getBufferedString(String[] keys, BufferedString def) {
		var value = find(keys);
		return value == null ? def : value;
	}

	public @Nullable String getString(String... keys) {
		return map(keys, BufferedString::toString, null);
	}

	public @NonNull String getString(String[] keys, String def) {
		return map(keys, BufferedString::toString, def);
	}

	public @Nullable Number getNumber(String... keys) {
		return map(keys, BufferedString::toNumber, null);
	}

	public @NonNull Number getNumber(String[] keys, Number def) {
		return map(keys, BufferedString::toNumber, def);
	}

	public @Nullable Long getLong(String... keys) {
		return getLong(keys, null);
	}

	public @NonNull Long getLong(String[] keys, Long def) {
		return map(keys, BufferedString::toLong, def);
	}

	public @Nullable Integer getInteger(String... keys) {
		return getInteger(keys, null);
	}

	public @NonNull Integer getInteger(String[] keys, Integer def) {
		return map(keys, BufferedString::toInteger, def);
	}

	public @Nullable Float getFloat(String... keys) {
		return getFloat(keys, null);
	}

	public @NonNull Float getFloat(String[] keys, Float def) {
		return map(keys, BufferedString::toFloat, def);
	}

	public @Nullable Double getDouble(String... keys) {
		return getDouble(keys, null);
	}

	public @NonNull Double getDouble(String[] keys, Double def) {
		return map(keys, BufferedString::toDouble, def);
	}

	public @Nullable Boolean getBoolean(String... keys) {
		return getBoolean(keys, null);
	}

	public @NonNull Boolean getBoolean(String[] keys, Boolean def) {
		return map(keys, BufferedString::toBoolean, def);
	}

	public @Nullable List<BufferedString> getListVector(String... keys) {
		var value = getBufferedString(keys);
		if (value == null) return null;
		BufferedString[] part = value.splitByDepth(',', new BufferedString.DepthSeparator('[', ']'));
		return Arrays.asList(part);
	}

	public @Nullable List<BufferedString> getBufferedList(String... keys) {
		return map(keys, this::parseList, null);
	}

	/**
	 * Parses a list configuration value into individual line parsers.
	 *
	 * @param keys possible argument keys
	 * @return parsed line parsers, or null if the argument does not exist
	 * @throws ParseException if a line cannot be parsed
	 */
	public @Nullable List<LineParser> getLine(String... keys) throws ParseException {
		BufferedString raw = find(keys);
		return raw == null ? null : toLineParsers(parseList(raw));
	}

	/**
	 * Unsupported because line configurations do not contain nested maps.
	 */
	@Deprecated
	@Override
	public Map<BufferedString, Object> getMap(String key) {
		throw unsupported("getMap");
	}

	/**
	 * Unsupported because line configurations do not contain nested sections.
	 */
	@Deprecated
	@Override
	public ConfigSection getSection(String key) {
		throw unsupported("getSection");
	}

	/**
	 * Unsupported because line configurations use {@link #getBufferedList(String...)}.
	 */
	@Deprecated
	@Override
	public List<Object> getList(String key) {
		throw unsupported("getList");
	}

	/**
	 * Unsupported because line configurations do not contain section lists.
	 */
	@Deprecated
	@Override
	public @NonNull List<ConfigSection> getListSection(String key) {
		throw unsupported("getListSection");
	}

	/**
	 * Unsupported because line configurations use {@link #getBufferedList(String...)}.
	 */
	@Deprecated
	@Override
	public @NonNull List<BufferedString> getListBufferedString(String key) {
		throw unsupported("getListBufferedString");
	}

	/**
	 * Unsupported because line configurations do not provide converted string lists.
	 */
	@Deprecated
	@Override
	public @NonNull List<String> getListString(String key) {
		throw unsupported("getListString");
	}

	/**
	 * Converts non-blank input strings into line parsers.
	 *
	 * @param items input strings
	 * @return parsed line parsers
	 * @throws ParseException if a line cannot be parsed
	 */
	protected List<LineParser> toLineParsers(List<BufferedString> items) throws ParseException {
		List<LineParser> result = new ArrayList<>(items.size());

		for (BufferedString item : items) {
			if (item.isBlank()) continue;

			LineParser parser = new LineParser(item);
			parser.parse();
			result.add(parser);
		}

		return result;
	}

	protected <T> T map(String[] keys, Function<BufferedString, T> mapper, T def) {
		BufferedString value = find(keys);
		return value != null ? mapper.apply(value) : def;
	}

	protected @Nullable BufferedString find(String... keys) {
		for (String k : keys) {
			BufferedString value = get(k, BufferedString.class);
			if (value != null) return value;
		}
		return null;
	}

	/**
	 * Splits a bracketed list into individual non-blank elements.
	 *
	 * <p>Separators inside nested braces or brackets are ignored.
	 * Empty elements and elements containing only whitespace are excluded.
	 *
	 * @param block bracketed list input
	 * @return non-blank list elements
	 */
	public List<BufferedString> parseList(BufferedString block) {
		List<BufferedString> result = new ArrayList<>();

		if (block.isBlank()) return result;
		if (block.buffer[block.start] != '[' || block.buffer[block.end - 1] != ']') throw new IllegalArgumentException("List must start with '[' and end with ']'");
		if (block.length() == 2) return result;

		final int end = block.end - 1;
		int current = block.start + 1;
		int depth = 0;

		for (int i = current; i < end; i++) {
			byte b = block.buffer[i];

			if (b == '{' || b == '[') {
				depth++;
			} else if (b == '}' || b == ']') {
				depth--;
			} else if (depth == 0 && isSeparator(b)) {
				addIfNotBlank(result, block.buffer, current, i);
				current = i + 1;
			}
		}

		addIfNotBlank(result, block.buffer, current, end);

		return result;
	}

	private static void addIfNotBlank(List<BufferedString> result, byte[] buffer, int start, int end) {
		if (start >= end) return;
		BufferedString item = new BufferedString(buffer, start, end);
		if (!item.isBlank()) result.add(item);
	}

	private static boolean isSeparator(byte c) {
		return c == ';' || c == '-';
	}

	private static UnsupportedOperationException unsupported(String method) {
		return new UnsupportedOperationException(method + " is not supported by LineConfig");
	}
}
