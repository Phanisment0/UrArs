package io.phanisment.urars.parser.file.config;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.phanisment.urars.parser.file.UrArsFileParser;
import io.phanisment.urars.parser.file.parser.LineParser;
import io.phanisment.urars.util.BufferedString;
import io.phanisment.urars.util.ParserUtils;

public class ConfigSection implements IConfig {
	private final Map<String, BufferedString> map;

	public ConfigSection(Map<String, BufferedString> map) {
		this.map = map;
	}

	@Override
	public BufferedString getBufferedString(String key) {
		return map.get(key);
	}

	@Override
	public BufferedString getBufferedString(String key, BufferedString def) {
		return map.getOrDefault(key, def);
	}

	@Override
	public String getString(String key) {
		return getString(key, null);
	}

	@Override
	public String getString(String key, String def) {
		BufferedString value = map.get(key);
		if (value == null) return def;
		return value.toString();
	}

	@Override
	public int getInt(String key) {
		return getInt(key, 0);
	}

	@Override
	public int getInt(String key, int def) {
		BufferedString value = map.get(key);
		if (value == null) return def;
		return value.toInteger();
	}

	@Override
	public float getFloat(String key) {
		return getFloat(key, 0);
	}

	@Override
	public float getFloat(String key, float def) {
		BufferedString value = map.get(key);
		if (value == null) return def;
		return value.toInteger();
	}

	@Override
	public double getDouble(String key) {
		return getDouble(key, 0);
	}

	@Override
	public double getDouble(String key, double def) {
		BufferedString value = map.get(key);
		if (value == null) return def;
		return value.toDouble();
	}

	@Override
	public long getLong(String key) {
		return getLong(key, 0);
	}

	@Override
	public long getLong(String key, long def) {
		BufferedString value = map.get(key);
		if (value == null) return def;
		return value.toLong();
	}

	@Override
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	@Override
	public boolean getBoolean(String key, boolean def) {
		BufferedString value = map.get(key);
		if (value == null) return def;
		return value.toBoolean();
	}

	@Override
	public ConfigSection getSection(String key) throws ParseException {
		return getSection(key, new ConfigSection(new HashMap<>()));
	}

	@Override
	public ConfigSection getSection(String key, ConfigSection def) throws ParseException {
		BufferedString value = map.get(key);
		if (value == null) return def;
		return new ConfigSection(UrArsFileParser.load(value));
	}

	@Override
	public List<BufferedString> getList(String key) {
		return getList(key, new ArrayList<>());
	}

	@Override
	public List<BufferedString> getList(String key, List<BufferedString> def) {
		BufferedString value = map.get(key);
		if (value == null) return def;
		return parseList(value);
	}

	protected List<BufferedString> parseList(BufferedString block) {
		if (block.isEmpty()) return new ArrayList<>();
		
		List<BufferedString> result = new ArrayList<>();
		int current = block.start;
		int depth = 0;
		for (int i = block.start; i < block.end; i++) {
			byte b = block.buffer[i];

			if (depth == 0 && b == '#') {
				if (i < current) result.add(new BufferedString(block.buffer, current, i));
				while (i < block.end) {
					b = block.buffer[i];
					if (ParserUtils.isNewLine(b)) break;
					i++;
				}
				current = i + 1;
				continue;
			}

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
		return getLine(key, new ArrayList<>());
	}

	public List<LineParser> getLine(String key, List<LineParser> def) throws ParseException {
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

	public Map<String, BufferedString> getMapSection(String key) throws ParseException {
		return getMapSection(key, new HashMap<>());
	}

	public Map<String, BufferedString> getMapSection(String key, Map<String, BufferedString> def) throws ParseException {
		BufferedString value = map.get(key);
		if (value == null) return def;
		return UrArsFileParser.load(value);
	}

	public Map<String, BufferedString> map() {
		return map;
	}

	@Override
	public String toString() {
		return map.toString();
	}
}