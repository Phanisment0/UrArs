package io.phanisment.urars.config;

/**
 * Basic interface used on config for provide getter
 * like String, Boolean, Number, and other.
 * 
 * General implementation for this class is {@link LineConfig} and {@link ConfigSection}
 * to get specific object inside the unknown object data.
 */
public interface IConfig {
	/**
	 * Get string value in key.
	 * @param key Target key want to get
	 * @return String value
	 */
	public String getString(String key);
	
	/**
	 * Get string value in key, if not found will return default value.
	 * @param key Target key want to get
	 * @param def Default value want to return
	 * @return String value
	 */
	public String getString(String key, String def);
	
	/**
	 * Get numer value in key.
	 * @param key Target key want to get
	 * @return Number value
	 */
	public Number getNumber(String key);
	
	/**
	 * Get number value in key, if not found will return default value.
	 * @param key Target key want to get
	 * @param def Default value want to return
	 * @return Number value
	 */
	public Number getNumber(String key, Number def);
	
	/**
	 * Get integer value in key.
	 * @param key Target key want to get
	 * @return Integer value
	 */
	public Integer getInt(String key);
	
	/**
	 * Get integer value in key, if not found will return default value.
	 * @param key Target key want to get
	 * @param def Default value want to return
	 * @return Integer value
	 */
	public Integer getInt(String key, Integer def);
	
	/**
	 * Get float value in key.
	 * @param key Target key want to get
	 * @return Float value
	 */
	public Float getFloat(String key);
	
	/**
	 * Get float value in key, if not found will return default value.
	 * @param key Target key want to get
	 * @param def Default value want to return
	 * @return Float value
	 */
	public Float getFloat(String key, Float def);
	
	/**
	 * Get double value in key.
	 * @param key Target key want to get
	 * @return Double value
	 */
	public Double getDouble(String key);
	
	/**
	 * Get double value in key, if not found will return default value.
	 * @param key Target key want to get
	 * @param def Default value want to return
	 * @return Double value
	 */
	public Double getDouble(String key, Double def);
	
	/**
	 * Get long value in key.
	 * @param key Target key want to get
	 * @return Long value
	 */
	public Long getLong(String key);
	
	/**
	 * Get long value in key, if not found will return default value.
	 * @param key Target key want to get
	 * @param def Default value want to return
	 * @return Long value
	 */
	public Long getLong(String key, Long def);
	
	/**
	 * Get boolean value in key.
	 * @param key Target key want to get
	 * @return Boolean value
	 */
	public Boolean getBoolean(String key);
	
	/**
	 * Get boolean value in key, if not found will return default value.
	 * @param key Target key want to get
	 * @param def Default value want to return
	 * @return Boolean value
	 */
	public Boolean getBoolean(String key, Boolean def);
}