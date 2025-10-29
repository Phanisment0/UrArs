package io.phanisment.urars.config;

import io.phanisment.urars.skill.config.SkillLineConfig;

import static io.phanisment.urars.UrArs.LOGGER;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.text.NumberFormat;
import java.text.ParseException;

public class LineConfig {
	protected Map<String, String> args = new HashMap<>();
	protected String line_raw;
	protected String context;
	protected String key;
	
	public LineConfig(String line_raw, String key, String context, Map<String, String> args) {
		this.line_raw = line_raw;
		this.key = key;
		this.context = context;
		this.args = args;
	}
	
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
	
	public String getString(String[] key) {
		return this.getString(key, null);
	}
	
	public String getString(String key) {
		return this.getString(key, null);
	}
	
	public String getString(String[] key, String def) {
		for (String k : key) {
			if (!this.args.containsKey(k)) continue;
			return this.getString(k, def);
		}
		return def;
	}
	
	public String getString(String key, String def) {
		if (this.args.containsKey(key)) return this.args.get(key);
		return def;
	}
	
	public boolean getBoolean(String[] key) {
		return this.getBoolean(key, false);
	}
	
	public boolean getBoolean(String key) {
		return this.getBoolean(key, false);
	}
	
	public boolean getBoolean(String[] key, boolean def) {
		for (String k : key) {
			if (!this.args.containsKey(k)) continue;
			return this.getBoolean(k, def);
		}
		return def;
	}
	
	public boolean getBoolean(String key, boolean def) {
		if (!this.args.containsKey(key)) return def;
		String value = this.args.get(key);
		return value.equalsIgnoreCase("true");
	}
	
	public Number getNumber(String[] key) {
		return this.getNumber(key, 0);
	}
	
	public Number getNumber(String key) {
		return this.getNumber(key, 0);
	}
	
	public Number getNumber(String[] key, Number def) {
		for (String k : key) {
			if (!this.args.containsKey(k)) continue;
			return this.getNumber(k, def);
		}
		return def;
	}
	
	public Number getNumber(String key, Number def) {
		if (!this.args.containsKey(key)) return def;
		String value = this.args.get(key);
		try {
			return NumberFormat.getInstance().parse(value);
		} catch (ParseException e) {
			return def;
		}
	}
	
	public List<String> getList(String[] key) {
		return this.getList(key, new ArrayList<>());
	}
	
	public List<String> getList(String key) {
		return this.getList(key, new ArrayList<>());
	}
	
	public List<String> getList(String[] key, List<String> def) {
		for (var k : key) {
			if (!this.args.containsKey(k)) continue;
			return this.getList(k, def);
		}
		return def;
	}
	
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
	
	public List<LineConfig> getLineList(String[] key) {
		return this.getLineList(key, new ArrayList<>());
	}
	
	public List<LineConfig> getLineList(String key) {
		return this.getLineList(key, new ArrayList<>());
	}
	
	public List<LineConfig> getLineList(String[] key, List<LineConfig> def) {
		for (String k : key) {
			if (!this.args.containsKey(k)) continue;
			return this.getLineList(k, def);
		}
		return def;
	}
	
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
	
	public Map<String, String> getArgs() {
		return this.args;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getContext() {
		return this.context;
	}
	
	public String getRaw() {
		return this.line_raw;
	}
	
	public SkillLineConfig getAsSkillLine() {
		return new SkillLineConfig(line_raw, key, context, args);
	}
	
	public boolean isList(String key) {
		String value = this.args.get(key);
		return value != null && value.startsWith("[") && value.endsWith("]");
	}
	
	@Override
	public String toString() {
		return "{key=" + key  + ", args=" + args + ", context=" + context + "}";
	}
}