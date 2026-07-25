package io.phanisment.urars.lib.file_parser.config;

import java.text.ParseException;

import io.phanisment.urars.lib.file_parser.util.BufferedString;

/**
 * Represents a read-only configuration section.
 *
 * <p>Implementations provide typed access to configuration values by key.
 * All {@code String} overloads are convenience methods that internally
 * delegate to the corresponding {@link BufferedString} overloads to avoid
 * unnecessary object creation when working with parsed data.</p>
 *
 * <p>Implementations may lazily parse nested sections and therefore
 * {@link #getSection(BufferedString)} may throw a {@link ParseException}
 * if the underlying configuration is malformed.</p>
 */
public interface IConfig {
	default BufferedString getBufferedString(String key) { return getBufferedString(new BufferedString(key)); }
	default BufferedString getBufferedString(String key, String def) { return getBufferedString(new BufferedString(key), new BufferedString(def)); }

	/**
	 * Returns the raw value associated with the specified key.
	 *
	 * @param key the configuration key
	 * @return the value associated with the key, or {@code null} if the key does not exist
	 */
	BufferedString getBufferedString(BufferedString key);

	/**
	 * Returns the raw value associated with the specified key.
	 *
	 * @param key the configuration key
	 * @param def the value to return if the key does not exist
	 * @return the configuration value, or {@code def} if the key is not present
	 */
	BufferedString getBufferedString(BufferedString key, BufferedString def);

	default String getString(String key) { return getString(new BufferedString(key)); }
	default String getString(String key, String def) { return getString(new BufferedString(key), def); }

	/**
	 * Returns the raw value associated with the specified key.
	 *
	 * @param key the configuration key
	 * @return the value associated with the key, or {@code null} if the key does not exist
	 */
	String getString(BufferedString key);

	/**
 * Returns the raw value associated with the specified key.
 *
 * @param key the configuration key
 * @param def the value to return if the key does not exist
 * @return the configuration value, or {@code def} if the key is not present
 */
	String getString(BufferedString key, String def);

	default int getInt(String key) { return getInt(new BufferedString(key)); }
	default int getInt(String key, int def) { return getInt(new BufferedString(key), def); }

	/**
	 * Returns the raw value associated with the specified key.
	 *
	 * @param key the configuration key
	 * @return the value associated with the key, or {@code null} if the key does not exist
	 */
	int getInt(BufferedString key);

	/**
	 * Returns the raw value associated with the specified key.
	 *
	 * @param key the configuration key
	 * @return the value associated with the key, or {@code null} if the key does not exist
	 */
	int getInt(BufferedString key, int def);

	default	float getFloat(String key) { return getFloat(new BufferedString(key)); }
	default float getFloat(String key, float def) { return getFloat(new BufferedString(key), def); }

	/**
	 * Returns the raw value associated with the specified key.
	 *
	 * @param key the configuration key
	 * @return the value associated with the key, or {@code null} if the key does not exist
	 */
	float getFloat(BufferedString key);

	/**
	 * Returns the raw value associated with the specified key.
	 *
	 * @param key the configuration key
	 * @param def the value to return if the key does not exist
	 * @return the configuration value, or {@code def} if the key is not present
	 */
	float getFloat(BufferedString key, float def);
	
	default double getDouble(String key) { return getDouble(new BufferedString(key)); }
	default double getDouble(String key, double def) { return getDouble(new BufferedString(key), def); }

	/**
	 * Returns the raw value associated with the specified key.
	 *
	 * @param key the configuration key
	 * @param def the value to return if the key does not exist
	 * @return the configuration value, or {@code def} if the key is not present
	 */
	double getDouble(BufferedString key);

	/**
	 * Returns the raw value associated with the specified key.
	 *
	 * @param key the configuration key
	 * @param def the value to return if the key does not exist
	 * @return the configuration value, or {@code def} if the key is not present
	 */
	double getDouble(BufferedString key, double def);
	
	default long getLong(String key) { return getLong(new BufferedString(key)); }
	default long getLong(String key, long def) { return getLong(new BufferedString(key)); }

	/**
	 * Returns the raw value associated with the specified key.
	 *
	 * @param key the configuration key
	 * @param def the value to return if the key does not exist
	 * @return the configuration value, or {@code def} if the key is not present
	 */
	long getLong(BufferedString key);

	/**
	 * Returns the raw value associated with the specified key.
	 *
	 * @param key the configuration key
	 * @param def the value to return if the key does not exist
	 * @return the configuration value, or {@code def} if the key is not present
	 */
	long getLong(BufferedString key, long def);
	
	default boolean getBoolean(String key) { return getBoolean(new BufferedString(key)); }
	default boolean getBoolean(String key, boolean def) { return getBoolean(new BufferedString(key), def); }

	/**
	 * Returns the raw value associated with the specified key.
	 *
	 * @param key the configuration key
	 * @param def the value to return if the key does not exist
	 * @return the configuration value, or {@code def} if the key is not present
	 */
	boolean getBoolean(BufferedString key);

	/**
	 * Returns the raw value associated with the specified key.
	 *
	 * @param key the configuration key
	 * @param def the value to return if the key does not exist
	 * @return the configuration value, or {@code def} if the key is not present
	 */
	boolean getBoolean(BufferedString key, boolean def);

	default IConfig getSection(String key) throws ParseException { return getSection(new BufferedString(key)); }
	default IConfig getSection(String key, IConfig def) throws ParseException { return getSection(new BufferedString(key), def); }

	/**
	 * Returns the raw value associated with the specified key.
	 *
	 * @param key the configuration key
	 * @param def the value to return if the key does not exist
	 * @return the configuration value, or {@code def} if the key is not present
	 */
	IConfig getSection(BufferedString key) throws ParseException;

	/**
	 * Returns the raw value associated with the specified key.
	 *
	 * @param key the configuration key
	 * @param def the value to return if the key does not exist
	 * @return the configuration value, or {@code def} if the key is not present
	 */
	IConfig getSection(BufferedString key, IConfig def) throws ParseException;
}