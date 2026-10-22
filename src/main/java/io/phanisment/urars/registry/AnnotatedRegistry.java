package io.phanisment.urars.registry;

import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.util.UrArsDebugger;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AnnotatedRegistry<T, F> {
	private final Map<Identifier, F> registry;
	private final Map<String, Identifier> aliases;

	public AnnotatedRegistry() {
		this.registry = new ConcurrentHashMap<>();
		this.aliases = new ConcurrentHashMap<>();
	}

	public AnnotatedRegistry(Map<Identifier, F> registry, Map<String, Identifier> aliases) {
		this.registry = registry;
		this.aliases = aliases;
	}

	public int size() {
		return registry.size();
	}

	public int aliasSize() {
		return aliases.size();
	}

	@Nullable
	public F get(Identifier id) {
		return registry.get(id);
	}

	@Nullable
	public F get(String alias) {
		Identifier id = aliases.get(normalize(alias));
		return id == null ? null : registry.get(id);
	}

	public Map<Identifier, F> entries() {
		return Collections.unmodifiableMap(registry);
	}

	public boolean register(Class<? extends T> clazz, F factory) {
		RegistryInfo info = clazz.getAnnotation(RegistryInfo.class);
		if (info == null) {
			UrArsDebugger.warn("Class '{}' is missing @RegistryInfo, skipping", clazz.getSimpleName());
			return false;
		}

		Identifier id = resolveId(info);
		if (registry.containsKey(id)) {
			UrArsDebugger.warn("Duplicate registry id '{}' on class '{}', skipping", id, clazz.getSimpleName());
			return false;
		}

		registry.put(id, factory);
		addAlias(info.key(), id);
		addAlias(id.toString(), id);
		for (String alias : info.aliases()) {
			addAlias(alias, id);
		}
		return true;
	}

	private static Identifier resolveId(RegistryInfo info) {
		String key = normalize(info.key());
		String namespace = normalize(info.namespace());
		String prefix = namespace.isBlank() ? normalize(info.author()) : namespace;
		return Identifier.fromNamespaceAndPath(prefix, key);
	}

	private void addAlias(String alias, Identifier id) {
		String key = normalize(alias);
		Identifier previous = aliases.put(key, id);
		if (previous != null && !previous.equals(id)) {
			UrArsDebugger.warn("Alias '{}' was '{}', now overwritten by '{}'", key, previous, id);
		} else {
			UrArsDebugger.trace("Alias '{}' -> '{}'", key, id);
		}
	}

	private static String normalize(String s) {
		return s.toLowerCase(Locale.ROOT);
	}
}