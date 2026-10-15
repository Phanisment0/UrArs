package io.phanisment.urars.config.pack;

import java.io.File;
import java.util.Map;
import java.util.Objects;

public class Pack {
	private final PackType type;
	private final String name;
	private final File dir;
	private final Map<String, PackLoader> registry;

	public Pack(String name, File dir, Map<String, PackLoader> registry) {
		this(PackType.PACK, name, dir, registry);
	}

	public Pack(PackType type, String name, File dir, Map<String, PackLoader> registry) {
		this.type = Objects.requireNonNull(type, "type");
		this.name = Objects.requireNonNull(name, "name");
		this.dir = Objects.requireNonNull(dir, "dir");
		this.registry = Objects.requireNonNull(registry, "registry");
	}

	public PackType type() {
		return type;
	}

	public String name() {
		return name;
	}

	public File file() {
		return dir;
	}

	public File folder(String name) {
		return new File(dir, name);
	}

	public boolean isGlobal() {
		return type == PackType.GLOBAL;
	}

	public void load() {
		for (var entry : registry.entrySet()) {
			File folder = folder(entry.getKey());
			if (!folder.exists() && !folder.mkdirs()) throw new IllegalStateException("Could not create folder: " + folder.getAbsolutePath());
			entry.getValue().load(this);
		}
	}

	public enum PackType {
		GLOBAL,
		PACK
	}
}