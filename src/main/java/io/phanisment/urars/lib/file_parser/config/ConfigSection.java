package io.phanisment.urars.lib.file_parser.config;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.phanisment.urars.lib.file_parser.UrArsFileParser;
import io.phanisment.urars.lib.file_parser.parser.LineParser;
import io.phanisment.urars.lib.file_parser.util.BufferedString;

public class ConfigSection implements IConfig {
	private final Map<BufferedString, BufferedString> map;

	public ConfigSection(Map<BufferedString, BufferedString> map) {
		this.map = map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BufferedString getBufferedString(BufferedString key) {
		return map.get(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BufferedString getBufferedString(BufferedString key, BufferedString def) {
		return map.getOrDefault(key, def);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getString(BufferedString key) {
		return getString(key, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getString(BufferedString key, String def) {
		BufferedString value = map.get(key);
		if (value == null) return def;
		return value.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInt(BufferedString key) {
		return getInt(key, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInt(BufferedString key, int def) {
		BufferedString value = map.get(key);
		if (value == null) return def;
		return value.toInteger();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getFloat(BufferedString key) {
		return getFloat(key, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getFloat(BufferedString key, float def) {
		BufferedString value = map.get(key);
		if (value == null) return def;
		return value.toInteger();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getDouble(BufferedString key) {
		return getDouble(key, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getDouble(BufferedString key, double def) {
		BufferedString value = map.get(key);
		if (value == null) return def;
		return value.toDouble();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getLong(BufferedString key) {
		return getLong(key, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getLong(BufferedString key, long def) {
		BufferedString value = map.get(key);
		if (value == null) return def;
		return value.toLong();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getBoolean(BufferedString key) {
		return getBoolean(key, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getBoolean(BufferedString key, boolean def) {
		BufferedString value = map.get(key);
		if (value == null) return def;
		return value.toBoolean();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConfigSection getSection(BufferedString key) throws ParseException {
		return getSection(key, new ConfigSection(new HashMap<>()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConfigSection getSection(BufferedString key, IConfig def) throws ParseException {
		BufferedString value = map.get(key);
		if (value == null) return (ConfigSection)def;
		return UrArsFileParser.load(value);
	}

	public List<BufferedString> getList(String key) {
		return getList(new BufferedString(key));
	}

	public List<BufferedString> getList(String key, List<BufferedString> def) {
		return getList(new BufferedString(key), def);
	}

	public List<BufferedString> getList(BufferedString key) {
		return getList(key, new ArrayList<>());
	}

	public List<BufferedString> getList(BufferedString key, List<BufferedString> def) {
		BufferedString value = map.get(key);
		if (value == null) return def;
		return parseList(value);
	}

	protected List<BufferedString> parseList(BufferedString block) {
		if (block.length() == 0) return new ArrayList<>();
		
		List<BufferedString> result = new ArrayList<>();
		int current = block.start;
		int depth = 0;
		for (int i = block.start; i < block.end; i++) {
			int b = block.buffer[i] & 0xFF;
			if (b == '{') depth++;
			else if (b == '}') depth--;
			if (depth <= 0 && (b == ';' || b == '-')) {
				if (i > current) result.add(new BufferedString(block.buffer, current, i));
				current = i + 1;
				depth = 0;
			}
		}

		if (current < block.end) result.add(new BufferedString(block.buffer, current, block.end));

		return result;
	}

	public List<LineParser> getLine(String key) throws ParseException {
		return getLine(new BufferedString(key));
	}

	public List<LineParser> getLine(String key, List<LineParser> def) throws ParseException {
		return getLine(new BufferedString(key), def);
	}

	public List<LineParser> getLine(BufferedString key) throws ParseException {
		return getLine(key, new ArrayList<>());
	}

	public List<LineParser> getLine(BufferedString key, List<LineParser> def) throws ParseException {
		List<BufferedString> value = getList(key);
		if (value == null) return def;

		List<LineParser> list = new ArrayList<>();
		for (BufferedString line : value) {
			if (line.isBlank()) continue;
			var parser = new LineParser(line);
			parser.parse();
			list.add(parser);
		}
		return list;
	}

	public Map<BufferedString, BufferedString> getMapSection(String key) throws ParseException {
		return getMapSection(new BufferedString(key));
	}

	public Map<BufferedString, BufferedString> getMapSection(String key, Map<BufferedString, BufferedString> def) throws ParseException {
		return getMapSection(new BufferedString(key), def);
	}

		public Map<BufferedString, BufferedString> getMapSection(BufferedString key) throws ParseException {
		return getMapSection(key, new HashMap<>());
	}

	public Map<BufferedString, BufferedString> getMapSection(BufferedString key, Map<BufferedString, BufferedString> def) throws ParseException {
		BufferedString value = map.get(key);
		if (value == null) return def;
		return UrArsFileParser.loadMap(value);
	}

	public static BufferedString value(String value) {
		return new BufferedString(value);
	}

	public Map<BufferedString, BufferedString> map() {
		return map;
	}
}