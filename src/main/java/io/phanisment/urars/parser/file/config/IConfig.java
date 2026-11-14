package io.phanisment.urars.parser.file.config;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import io.phanisment.urars.util.BufferedString;

/**
 * Represents a read-only configuration section.
 *
 * <p>Implementations provide typed access to configuration values by key.
 * All {@code String} overloads are convenience methods that internally
 * delegate to the corresponding {@link BufferedString} overloads to avoid
 * unnecessary object creation when working with parsed data.</p>
 *
 * <p>Implementations may lazily parse nested sections and therefore
 * {@link #getSection(String)} may throw a {@link ParseException}
 * if the underlying configuration is malformed.</p>
 */
public interface IConfig {

	Object get(String key);
	Object get(String key, Object def);

	<T> T get(String key, Class<T> type);
	<T> T get(String key, T def, Class<T> type);

	BufferedString getBufferedString(String key);
	BufferedString getBufferedString(String key, BufferedString def);

	String getString(String key);
	String getString(String key, String def);

	Number getNumber(String key);
	Number getNumber(String key, Number def);

	Integer getInteger(String key);
	Integer getInteger(String key, Integer def);

	Float getFloat(String key);
	Float getFloat(String key, Float def);

	Double getDouble(String key);
	Double getDouble(String key, Double def);

	Long getLong(String key);
	Long getLong(String key, Long def);

	Boolean getBoolean(String key);
	Boolean getBoolean(String key, Boolean def);

	Map<BufferedString, Object> getMap(String key);
	ConfigSection getSection(String key);

	List<Object> getList(String key);
	List<ConfigSection> getListSection(String key);
	List<BufferedString> getListBufferedString(String key);
	List<String> getListString(String key);
}