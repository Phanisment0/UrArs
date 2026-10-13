package io.phanisment.urars.parser.file.config;

import java.text.ParseException;
import java.util.List;

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
	BufferedString getBufferedString(String key);
	BufferedString getBufferedString(String key, BufferedString def);

	String getString(String key);
	String getString(String key, String def);

	int getInt(String key);
	int getInt(String key, int def);

	float getFloat(String key);
	float getFloat(String key, float def);
	
	double getDouble(String key);
	double getDouble(String key, double def);
	
	long getLong(String key);
	long getLong(String key, long def);
	
	boolean getBoolean(String key);
	boolean getBoolean(String key, boolean def);

	ConfigSection getSection(String key) throws ParseException;
	ConfigSection getSection(String key, ConfigSection def) throws ParseException;

	List<BufferedString> getList(String key);
	List<BufferedString> getList(String key, List<BufferedString> def);
}