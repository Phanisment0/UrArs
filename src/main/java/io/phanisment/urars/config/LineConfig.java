package io.phanisment.urars.config;

import io.phanisment.urars.skill.config.SkillLineConfig;

import static io.phanisment.urars.UrArs.LOGGER;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Represents a configuration line parsed from a text-based configuration format.
 * 
 * A line can contain:
 * <ul>
 *   <li>A {@code key} (identifier)</li>
 *   <li>An optional set of arguments in curly brackets: {@code {key1=value1;key2=value2}}</li>
 *   <li>An optional {@code context} value (remaining part after arguments)</li>
 * </ul>
 *
 * Example format:
 * <pre>
 * fireball{power=3;speed=1.2} @target
 * </pre>
 *
 * This class can parse such a line and provide easy access to values as strings,
 * numbers, booleans, lists, or even sub-config lines.
 */
public class LineConfig implements IConfig {
	protected Map<String, String> args = new HashMap<>();
	protected String line_raw;
	protected String context;
	protected String key;
	
	/**
	 * Constructs a {@link LineConfig} with explicit parameters.
	 *
	 * @param line_raw Raw line string before parsing
	 * @param key The main key or identifier
	 * @param context Additional context after the arguments
	 * @param args Map of argument key-value pairs
	 */
	public LineConfig(String line_raw, String key, String context, Map<String, String> args) {
		this.line_raw = line_raw;
		this.key = key;
		this.context = context;
		this.args = args;
	}
	
	/**
	 * Parse the string to useable data.
	 * 
	 * @param line String that want to parse
	 */
	public LineConfig(String line) {
		this.line_raw = line;
		int depth = 0;
		int last_bracket = -1;
		int first_bracket = line.indexOf("{");
		
		if (first_bracket != -1 && line.contains("}")) {
			for (int i = 0; i < line.length(); i++) {
				char c = line.charAt(i);
				if (c == '{') {
					depth++;
				} else if (c == '}') {
					depth--;
					if (depth == 0) {
						last_bracket = i;
						break;
					}
				}
			}
			if (depth != 0) LOGGER.warn("LineConfig - Bracket unbalance! Line='{}'", line);
			
			if (last_bracket + 1 < line.length()) this.context = line.substring(last_bracket + 1).trim();
			this.key = line.substring(0, first_bracket).trim();
			String arguments = line.substring(first_bracket + 1, last_bracket);
			
			for (String pair : split(arguments, ';')) {
				if (!pair.contains("=")) continue;
				String[] entry = pair.split("=", 2);
				String key = entry[0].trim();
				String value = entry[1].trim();
				this.args.put(key , value);
			}
			return;
		}
		String[] split = line.split(" ", 2);
		this.key = split[0].trim();
		if (split.length > 1) this.context = split[1].trim();
	}
	
	/**
	 * Split string inside bracket with ignoring bracket inside it.
	 * 
	 * @param input String to split 
	 * @param by Character used to split
	 * @return List of split string
	 */
	public static List<String> split(String input, char by) {
		List<String> part = new ArrayList<>();
		var curr = new StringBuilder();
		int depth = 0;
		
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c == '{' || c == '[') {
				depth++;
			} else if (c == '}' || c == ']') {
				depth--;
			}
			
			if (c == by && depth == 0) {
				part.add(curr.toString().trim());
				curr.setLength(0);
			} else {
				curr.append(c);
			}
		}
		
		if (curr.length() > 0) part.add(curr.toString().trim());
		return part;
	}
	
	// String ////////////////
	/**
	 * Get with aliases.
	 * @param key String array that want to get 
	 * @return The first array slot key in data
	 */
	public String getString(String[] key) {
		return this.getString(key, null);
	}
	
	/** {@inheritDoc} */
	@Override
	public String getString(String key) {
		return this.getString(key, null);
	}
	
	/**
	 * Get with aliases.
	 * @param key String array that want to get 
	 * @param def Default value want to return
	 * @return The first array slot key in data
	 */
	public String getString(String[] key, String def) {
		for (String k : key) {
			if (!this.args.containsKey(k)) continue;
			return this.getString(k, def);
		}
		return def;
	}
	
	/** {@inheritDoc} */
	@Override
	public String getString(String key, String def) {
		if (this.args.containsKey(key)) return this.args.get(key);
		return def;
	}
	
	// Boolean ////////////////
	/**
	 * Get with aliases.
	 * @param key String array that want to get 
	 * @return The first array slot key in data
	 */
	public Boolean getBoolean(String[] key) {
		return this.getBoolean(key, false);
	}
	
	/** {@inheritDoc} */
	@Override
	public Boolean getBoolean(String key) {
		return this.getBoolean(key, false);
	}
	
	/**
	 * Get with aliases.
	 * @param key String array that want to get 
	 * @param def Default value want to return
	 * @return The first array slot key in data
	 */
	public Boolean getBoolean(String[] key, Boolean def) {
		for (String k : key) {
			if (!this.args.containsKey(k)) continue;
			return this.getBoolean(k, def);
		}
		return def;
	}
	
	/** {@inheritDoc} */
	@Override
	public Boolean getBoolean(String key, Boolean def) {
		if (!this.args.containsKey(key)) return def;
		String value = this.args.get(key);
		return Boolean.valueOf(value);
	}
	
	// Number ////////////////
	/**
	 * Get with aliases.
	 * @param key String array that want to get 
	 * @return The first array slot key in data
	 */
	public Number getNumber(String[] key) {
		return this.getNumber(key, 0);
	}
	
	/** {@inheritDoc} */
	@Override
	public Number getNumber(String key) {
		return this.getNumber(key, 0);
	}
	
	/**
	 * Get with aliases.
	 * @param key String array that want to get 
	 * @param def Default value want to return
	 * @return The first array slot key in data
	 */
	public Number getNumber(String[] key, Number def) {
		for (String k : key) {
			if (!this.args.containsKey(k)) continue;
			return this.getNumber(k, def);
		}
		return def;
	}
	
	/** {@inheritDoc} */
	@Override
	public Number getNumber(String key, Number def) {
		if (!this.args.containsKey(key)) return def;
		String value = this.args.get(key);
		try {
			return NumberFormat.getInstance().parse(value);
		} catch (ParseException e) {
			return def;
		}
	}
	
	// Integer ////////////////
	/**
	 * Get with aliases.
	 * @param key String array that want to get 
	 * @return The first array slot key in data
	 */
	public Integer getInt(String[] key) {
		return this.getInt(key, 0);
	}
	
	/** {@inheritDoc} */
	@Override
	public Integer getInt(String key) {
		return this.getInt(key, 0);
	}
	
	/**
	 * Get with aliases.
	 * @param key String array that want to get 
	 * @param def Default value want to return
	 * @return The first array slot key in data
	 */
	public Integer getInt(String[] key, Integer def) {
		return this.getNumber(key, def).intValue();
	}
	
	/** {@inheritDoc} */
	@Override
	public Integer getInt(String key, Integer def) {
		return this.getNumber(key, def).intValue();
	}
	
	// Float ////////////////
	/**
	 * Get with aliases.
	 * @param key String array that want to get 
	 * @return The first array slot key in data
	 */
	public Float getFloat(String[] key) {
		return this.getFloat(key, 0.0f);
	}
	
	/** {@inheritDoc} */
	@Override
	public Float getFloat(String key) {
		return this.getFloat(key, 0.0f);
	}
	
	/**
	 * Get with aliases.
	 * @param key String array that want to get 
	 * @param def Default value want to return
	 * @return The first array slot key in data
	 */
	public Float getFloat(String[] key, Float def) {
		return this.getNumber(key, def).floatValue();
	}
	
	/** {@inheritDoc} */
	@Override
	public Float getFloat(String key, Float def) {
		return this.getNumber(key, def).floatValue();
	}
	
	// Double ////////////////
	/**
	 * Get with aliases.
	 * @param key String array that want to get 
	 * @return The first array slot key in data
	 */
	public Double getDouble(String[] key) {
		return this.getDouble(key, 0.0d);
	}
	
	/** {@inheritDoc} */
	@Override
	public Double getDouble(String key) {
		return this.getDouble(key, 0.0d);
	}
	
	/**
	 * Get with aliases.
	 * @param key String array that want to get 
	 * @param def Default value want to return
	 * @return The first array slot key in data
	 */
	public Double getDouble(String[] key, Double def) {
		return this.getNumber(key, def).doubleValue();
	}
	
	/** {@inheritDoc} */
	@Override
	public Double getDouble(String key, Double def) {
		return this.getNumber(key, def).doubleValue();
	}
	
	// Long ////////////////
	/**
	 * Get with aliases.
	 * @param key String array that want to get 
	 * @return The first array slot key in data
	 */
	public Long getLong(String[] key) {
		return this.getLong(key, 0l);
	}
	
	/** {@inheritDoc} */
	@Override
	public Long getLong(String key) {
		return this.getLong(key, 0l);
	}
	
	/**
	 * Get with aliases.
	 * @param key String array that want to get 
	 * @param def Default value want to return
	 * @return The first array slot key in data
	 */
	public Long getLong(String[] key, Long def) {
		return this.getNumber(key, def).longValue();
	}
	
	/** {@inheritDoc} */
	@Override
	public Long getLong(String key, Long def) {
		return this.getNumber(key, def).longValue();
	}
	
	// List ////////////////
	/**
	 * Get with aliases.
	 * @param key String array that want to get 
	 * @return The first array slot key in data
	 */
	public List<String> getList(String[] key) {
		return this.getList(key, new ArrayList<>());
	}
	
	/**
	 * Get string line value in key.
	 * @param key Target key want to get
	 * @return String line value
	 */
	public List<String> getList(String key) {
		return this.getList(key, new ArrayList<>());
	}
	
	/**
	 * Get with aliases.
	 * @param key String array that want to get 
	 * @param def Default value want to return
	 * @return The first array slot key in data
	 */
	public List<String> getList(String[] key, List<String> def) {
		for (var k : key) {
			if (!this.args.containsKey(k)) continue;
			return this.getList(k, def);
		}
		return def;
	}
	
	/**
	 * Get string line value in key.
	 * @param key Target key want to get
	 * @param def Default value want to return
	 * @return String line value
	 */
	public List<String> getList(String key, List<String> def) {
		String value = this.args.get(key);
		if (value == null || value.isEmpty()) return def;
		value = value.trim();
		if (!value.startsWith("[") && !value.endsWith("]")) return def;
		
		value = value.substring(1, value.length() - 1);
		List<String> list = new ArrayList<>();
		for (String part : split(value, ',')) {
			if (part != null && !part.isEmpty()) list.add(part.trim());
		}
		return list;
	}
	
	// LineList ////////////////
	/**
	 * Get with aliases.
	 * @param key String array that want to get 
	 * @return The first array slot key in data
	 */
	public List<LineConfig> getLineList(String[] key) {
		return this.getLineList(key, new ArrayList<>());
	}
	
	/**
	 * Get string value in key then convert to {@link LineConfig}.
	 * @param key Target key want to get
	 * @return String line value
	 */
	public List<LineConfig> getLineList(String key) {
		return this.getLineList(key, new ArrayList<>());
	}
	
	/**
	 * Get with aliases.
	 * @param key String array that want to get 
	 * @param def Default value want to return
	 * @return The first array slot key in data
	 */
	public List<LineConfig> getLineList(String[] key, List<LineConfig> def) {
		for (String k : key) {
			if (!this.args.containsKey(k)) continue;
			return this.getLineList(k, def);
		}
		return def;
	}
	
	/**
	 * Get string value in key then convert to {@link LineConfig}.
	 * @param key Target key want to get
	 * @param def Default value want to return
	 * @return String line value
	 */
	public List<LineConfig> getLineList(String key, List<LineConfig> def) {
		String value = this.args.get(key);
		if (value == null || value.isEmpty()) return def;
		value = value.trim();
		if (!value.startsWith("[") && !value.endsWith("]")) return def;
		
		value = value.substring(1, value.length() - 1);
		List<LineConfig> list = new ArrayList<>();
		for (String part : split(value, '-')) {
			if (part != null && !part.isEmpty()) list.add(new LineConfig(part.trim()));
		}
		return list;
	}
	
	/**
	 * Get result parsed of argument.
	 * @return Map of value
	 */
	public Map<String, String> getArgs() {
		return this.args;
	}
	
	/**
	 * Get the string before first bracket.
	 * @return First string of parsed string
	 */
	public String getKey() {
		return this.key;
	}
	
	/**
	 * Get the string after last bracket.
	 * @return Last string of parsed string
	 */
	public String getContext() {
		return this.context;
	}
	
	/**
	 * Get the unparsed string.
	 * @return Raw parse string
	 */
	public String getRaw() {
		return this.line_raw;
	}
	
	/**
	 * Convert to {@link SkillLineConfig}
	 * @return Copy this data to new instance inside the class {@link SkillLineConfig}
	 */
	public SkillLineConfig getAsSkillLine() {
		return new SkillLineConfig(line_raw, key, context, args);
	}
	
	/**
	 * Check if the argument is list.
	 * @return True if argument is list
	 */
	public boolean isList(String key) {
		String value = this.args.get(key);
		return value != null && value.startsWith("[") && value.endsWith("]");
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "{key=" + key  + ", args=" + args + ", context=" + context + "}";
	}
}