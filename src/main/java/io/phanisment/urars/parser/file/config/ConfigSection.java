package io.phanisment.urars.parser.file.config;

import java.util.*;

import io.phanisment.urars.util.BufferedString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ConfigSection implements IConfig {
	private final Map<BufferedString, Object> map;
	private final BufferedString lookup_key = BufferedString.empty();

	public ConfigSection(@NonNull Map<BufferedString, Object> map) {
		this.map = Objects.requireNonNull(map, "Config map cannot be null");
	}

	public ConfigSection(ConfigSection copy) {
		this.map = new HashMap<>(copy.map);
	}

	public Map<BufferedString, Object> map() {
		return map;
	}

	@Override
	public <T> @Nullable T get(String key, Class<T> type) {
		return get(key, null, type);
	}

	@Override
	public <T> T get(String key, T def, Class<T> type) {
		Object value = get(key);
		return type.isInstance(value) ? type.cast(value) : def;
	}

	public <T> T get(BufferedString key, Class<T> type) {
		return get(key, null, type);
	}

	public <T> T get(BufferedString key, T def, Class<T> type) {
		Object value = map.get(key);
		return value == null ? def : type.cast(value);
	}

	@Override
	public @Nullable Object get(String key) {
		lookup_key.reset(key.getBytes());
		return map.get(lookup_key);
	}

	@Override
	public Object get(String key, Object def) {
		lookup_key.reset(key.getBytes());
		return map.getOrDefault(lookup_key, def);
	}

	@Override
	public @Nullable BufferedString getBufferedString(String key) {
		return getBufferedString(key, null);
	}

	@Override
	public BufferedString getBufferedString(String key, BufferedString def) {
		return get(key, def, BufferedString.class);
	}

	@Override
	public @Nullable String getString(String key) {
		return getString(key, null);
	}

	@Override
	public String getString(String key, String def) {
		BufferedString value = getBufferedString(key);
		return value == null ? def : value.toString();
	}

	@Override
	public @Nullable Number getNumber(String key) {
		return null;
	}

	@Override
	public Number getNumber(String key, Number def) {
		BufferedString value = getBufferedString(key);
		return value == null ? def : value.toNumber();
	}

	@Override
	public @Nullable Integer getInteger(String key) {
		return getInteger(key, null);
	}

	@Override
	public Integer getInteger(String key, Integer def) {
		BufferedString value = getBufferedString(key);
		return value == null ? def : value.toInteger();
	}

	@Override
	public @Nullable Float getFloat(String key) {
		return getFloat(key, null);
	}

	@Override
	public Float getFloat(String key, Float def) {
		BufferedString value = getBufferedString(key);
		return value == null ? def : value.toFloat();
	}

	@Override
	public @Nullable Double getDouble(String key) {
		return getDouble(key, null);
	}

	@Override
	public Double getDouble(String key, Double def) {
		BufferedString value = getBufferedString(key);
		return value == null ? def : value.toDouble();
	}

	@Override
	public @Nullable Long getLong(String key) {
		return getLong(key, null);
	}

	@Override
	public Long getLong(String key, Long def) {
		BufferedString value = getBufferedString(key);
		return value == null ? def : value.toLong();
	}

	@Override
	public @Nullable Boolean getBoolean(String key) {
		return getBoolean(key, null);
	}

	@Override
	public Boolean getBoolean(String key, Boolean def) {
		BufferedString value = getBufferedString(key);
		return value == null ? def : value.toBoolean();
	}

	@Override
	public @Nullable Map<BufferedString, Object> getMap(String key) {
		return (Map<BufferedString, Object>)get(key, Map.class);
	}

	@Override
	public @Nullable ConfigSection getSection(String key) {
		Map<BufferedString, Object> section = getMap(key);
		return section == null ? null : new ConfigSection(section);
	}

	@Override
	public @Nullable List<Object> getList(String key) {
		return (List<Object>)get(key, List.class);
	}

	@Override
	public @NonNull List<ConfigSection> getListSection(String key) {
		List<ConfigSection> result = new ArrayList<>();
		var list = getList(key);
		if (list != null) for (Object item : list) if (item instanceof Map<?,?> v) result.add(new ConfigSection((Map<BufferedString, Object>)v));
		return result;
	}

	@Override
	public @NonNull List<BufferedString> getListBufferedString(String key) {
		List<BufferedString> result = new ArrayList<>();
		var list = getList(key);
		if (list != null) for (Object item : list) if (item instanceof BufferedString v) result.add(v);
		return result;
	}

	@Override
	public @NonNull List<String> getListString(String key) {
		List<String> result = new ArrayList<>();
		var list = getList(key);
		if (list != null) for (Object item : list) if (item instanceof BufferedString v) result.add(v.toString());
		return result;
	}

	@Override
	public String toString() {
		return map.toString();
	}
}