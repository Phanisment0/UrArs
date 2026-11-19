package io.phanisment.urars.parser.file.config;

import io.phanisment.urars.parser.file.LineParser;
import io.phanisment.urars.parser.BufferedString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a configuration line containing a key, arguments, and context.
 *
 * <p>Argument values are {@link BufferedString} or {@code List<BufferedString>}.
 * An argument without a name is stored under the {@code null} key, so any
 * {@code String} key parameter may be {@code null}.
 */
public class LineConfig {
	private final String key;
	private final Map<BufferedString, Object> arguments;
	private final BufferedString context;
	private final BufferedString lookup_key = BufferedString.empty();

	public LineConfig(String key, Map<BufferedString, Object> arguments, BufferedString context) {
		this.key = key;
		this.arguments = Objects.requireNonNull(arguments, "Arguments cannot be null");
		this.context = context;
	}

	public String key() {
		return key;
	}

	public BufferedString context() {
		return context;
	}

	public Map<BufferedString, Object> map() {
		return arguments;
	}

	public @Nullable Object get(@Nullable String key) {
		if (key == null) return arguments.get(null);
		lookup_key.reset(key.getBytes());
		return arguments.get(lookup_key);
	}

	public Object get(@Nullable String key, Object def) {
		if (key == null) return arguments.getOrDefault(null, def);
		lookup_key.reset(key.getBytes());
		return arguments.getOrDefault(lookup_key, def);
	}

	public <T> @Nullable T get(@Nullable String key, Class<T> type) {
		return get(key, null, type);
	}

	public <T> T get(@Nullable String key, T def, Class<T> type) {
		Object value = get(key);
		return type.isInstance(value) ? type.cast(value) : def;
	}

	public boolean has(@Nullable String key) {
		return get(key) != null;
	}


	public @Nullable BufferedString getBufferedString(String... keys) {
		return find(keys, BufferedString.class);
	}

	public BufferedString getBufferedString(String[] keys, BufferedString def) {
		BufferedString value = find(keys, BufferedString.class);
		return value != null ? value : def;
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

	/**
	 * Returns the already-parsed list for the first matching key,
	 * or null if no argument holds a list.
	 */
	public @Nullable List<BufferedString> getList(String... keys) {
		return asList(find(keys));
	}

	public @Nullable List<LineConfig> getLine(String... keys) throws ParseException {
		List<BufferedString> list = getList(keys);
		return list == null ? null : toLine(list);
	}

	protected List<LineConfig> toLine(List<BufferedString> items) throws ParseException {
		List<LineConfig> result = new ArrayList<>(items.size());
		for (BufferedString item : items) {
			if (item.isBlank()) continue;
			LineConfig parser = LineParser.parse(item);
			result.add(parser);
		}
		return result;
	}

	protected <T> T map(String[] keys, Function<BufferedString, T> mapper, T def) {
		BufferedString value = find(keys, BufferedString.class);
		return value != null ? mapper.apply(value) : def;
	}

	/**
	 * Finds the value associated with the first matching key.
	 *
	 * @param keys the keys to search, or null to search the unnamed argument
	 * @return the first matching value, or null
	 */
	protected @Nullable Object find(String... keys) {
		if (keys == null) return get(null);
		for (String key : keys) {
			Object value = get(key);
			if (value != null) return value;
		}
		return null;
	}

	/**
	 * Finds the first matching value and returns it if it matches the given type.
	 *
	 * @param keys the keys to search
	 * @param type the expected value type
	 * @return the matching value, or null if the value has a different type
	 */
	protected <T> @Nullable T find(String[] keys, Class<T> type) {
		Object value = find(keys);
		return type.isInstance(value) ? type.cast(value) : null;
	}

	protected static @Nullable List<BufferedString> asList(Object value) {
		return isList(value) ? (List<BufferedString>)value : null; // list item are always BufferedString
	}

	protected static boolean isList(Object value) {
		return value instanceof List<?>;
	}

	@Override
	public String toString() {
		return key + " " + arguments + " " + context;
	}
}