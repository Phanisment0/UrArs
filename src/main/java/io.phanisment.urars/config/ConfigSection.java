package io.phanisment.urars.config;

import net.minecraft.util.Identifier;

import io.phanisment.urars.skill.config.SkillConfigSection;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConfigSection {
	protected final Map<String, Object> data = new HashMap<>();
	
	public boolean contains(String key) {
		return data.containsKey(key);
	}
	
	public Object get(String key) {
		return data.get(key);
	}
	
	public Identifier getIdentifier(String key) {
		return this.getIdentifier(key, null);
	}
	
	public Identifier getIdentifier(String key, Identifier def) {
		String value = this.getString(key);
		if (value != null) return Identifier.of(value);
		return def;
	}
	
	public List<LineConfig> getLineList(String key) {
		return this.getLineList(key, new ArrayList<>());
	}
	
	public List<LineConfig> getLineList(String key, List<LineConfig> def) {
		if (this.get(key) instanceof List<?> list) {
			List<LineConfig> result = new ArrayList<>();
			for (Object value : list) {
				if (value instanceof String s) result.add(new LineConfig(s));
				else result.add(new LineConfig(String.valueOf(value)));
			}
			return result;
		}
		return def;
	}
	
	public List<ConfigSection> getSectionList(String key) {
		return this.getSectionList(key, new ArrayList<>());
	}
	
	public List<ConfigSection> getSectionList(String key, List<ConfigSection> def) {
		if (this.get(key) instanceof List<?> list) {
			List<ConfigSection> result = new ArrayList<>();
			for (Object value : list) {
				if (value instanceof Map<?, ?> map) {
					var section = new ConfigSection();
					section.data.putAll((Map<String, Object>)map);
					result.add(section);
				}
			}
			return result;
		}
		return def;
	}
	
	public List<String> getStringList(String key) {
		return this.getStringList(key, new ArrayList<>());
	}
	
	public List<String> getStringList(String key, List<String> def) {
		if (this.get(key) instanceof List<?> list) {
			List<String> result = new ArrayList<>();
			for (Object value : list) {
				if (value instanceof String s) result.add(s);
				else result.add(String.valueOf(value));
			}
			return result;
		}
		return def;
	}
	
	public Integer getInt(String key) {
		return this.getInt(key, 0);
	}
	
	public Integer getInt(String key, Integer def) {
		return this.getNumber(key, def).intValue();
	}
	
	public Float getFloat(String key) {
		return this.getFloat(key, 0.0f);
	}
	
	public Float getFloat(String key, Float def) {
		return this.getNumber(key, def).floatValue();
	}
	
	public Double getDouble(String key) {
		return this.getDouble(key, 0.0d);
	}
	
	public Double getDouble(String key, Double def) {
		return this.getNumber(key, def).doubleValue();
	}
	
	public Long getLong(String key) {
		return this.getLong(key, 0l);
	}
	
	public Long getLong(String key, Long def) {
		return this.getNumber(key, def).longValue();
	}
	
	public String getString(String key) {
		return this.getString(key, null);
	}
	
	public String getString(String key, String def) {
		if (this.get(key) instanceof String value) return value;
		return def;
	}
	
	public Number getNumber(String key) {
		return this.getNumber(key, 0);
	}
	
	public Number getNumber(String key, Number def) {
		if (this.get(key) instanceof Number value) return value;
		return def;
	}
	
	public Boolean getBoolean(String key) {
		return this.getBoolean(key, false);
	}
	
	public Boolean getBoolean(String key, Boolean def) {
		if (this.get(key) instanceof Boolean value) return value;
		return def;
	}
	
	public Set<String> getKeys() {
		return data.keySet();
	}
	
	public Collection<Object> getValues() {
		return data.values();
	}
	
	public ConfigSection getSection(String key) {
		if (this.get(key) instanceof Map map) {
			var section = new ConfigSection();
			section.data.putAll(map);
			return section;
		}
		return null;
	}
	
	public SkillConfigSection getAsSkillSection() {
		var section = new SkillConfigSection();
		section.serialize().putAll(this.data);
		return section;
	}
	
	public void set(String key, Object value) {
		data.put(key, value);
	}
	
	public Map<String, Object> serialize() {
		return data;
	}
	
	@Override
	public String toString() {
		return data.toString();
	}
}