package io.phanisment.urars.parser.file.config;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.phanisment.urars.parser.file.parser.LineParser;
import io.phanisment.urars.util.BufferedString;
import org.jspecify.annotations.Nullable;

public class LineConfig extends ConfigSection {
	private final String key;
	private final BufferedString context;

	public LineConfig(String key, Map<String, BufferedString> arguments, BufferedString context) {
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

	public @Nullable BufferedString getBufferedString(String[] key) {
		return getBufferedString(key,null);
	}

	public BufferedString getBufferedString(String[] key, BufferedString def) {
		return findValue(key);
	}

	public @Nullable String getString(String[] key) {
		return getString(key, null);
	}

	public String getString(String[] key, String def) {
		BufferedString value = findValue(key);
		return value != null ? value.toString() : def;
	}

	public @Nullable Number getNumber(String[] key) {
		return getNumber(key, null);
	}

	public Number getNumber(String[] key, Number def) {
		BufferedString value = findValue(key);
		return value != null ? value.toNumber() : def;
	}

	public long getLong(String[] key) {
		return getLong(key, 0);
	}

	public long getLong(String[] key, long def) {
		BufferedString value = findValue(key);
		return value != null ? value.toLong() : def;
	}

	public int getInt(String[] key) {
		return getInt(key, 0);
	}

	public int getInt(String[] key, int def) {
		BufferedString value = findValue(key);
		return value != null ? value.toInteger() : def;
	}

	public float getFloat(String[] key) {
		return getFloat(key, 0);
	}

	public float getFloat(String[] key, float def) {
		BufferedString value = findValue(key);
		return value != null ? value.toFloat() : def;
	}

	public double getDouble(String[] key) {
		return getDouble(key, 0);
	}

	public double getDouble(String[] key, double def) {
		BufferedString value = findValue(key);
		return value != null ? value.toDouble() : def;
	}

	public boolean getBoolean(String[] key) {
		return getBoolean(key, false);
	}

	public boolean getBoolean(String[] key, boolean def) {
		BufferedString value = findValue(key);
		return value != null ? value.toBoolean() : def;
	}

	public @Nullable List<BufferedString> getList(String[] key) {
		return getList(key, new ArrayList<>());
	}

	public List<BufferedString> getList(String[] key, List<BufferedString> def) {
		BufferedString value = findValue(key);
		return value != null ? parseList(value) : def;
	}

	public List<LineParser> getLine(String[] key) throws ParseException {
		return getLine(key, new ArrayList<>());
	}

	public List<LineParser> getLine(String[] key, List<LineParser> def) throws ParseException {
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

	private BufferedString findValue(String... keys) {
		for (String key : keys) {
			BufferedString value = map().get(key);
			if (value != null) return value;
		}
		return null;
	}

	@Override
	protected List<BufferedString> parseList(BufferedString block) {
		if (block.isEmpty()) return new ArrayList<>();

		if (block.buffer[block.start] != '[' || block.buffer[block.end - 1] != ']') throw new IllegalArgumentException("List must start with '[' and end with ']'");

		if (block.length() == 2) return new ArrayList<>();

		List<BufferedString> result = new ArrayList<>();

		int start = block.start + 1;
		int end = block.end - 1;

		int current = start;
		int depth = 0;

		for (int i = start; i < end; i++) {
			byte b = block.buffer[i];
			if (b == '{' || b == '[') depth++;
			else if (b == '}' || b == ']') depth--;
			else if (depth == 0 && (b == ';' || b == '-')) {
				if (i > current) result.add(new BufferedString(block.buffer, current, i));
				current = i + 1;
			}
		}
		if (current < end) result.add(new BufferedString(block.buffer, current, end));
		return result;
	}
}