package io.phanisment.urars.config.pack;

import java.io.File;
import java.util.Map;

public class Pack {
	private final PackType type;
	private final File dir;
	private final Map<String, PackLoader> registry;

	public Pack(File dir, Map<String, PackLoader> registry) {
		this(PackType.PACK, dir, registry);
	}

	public Pack(PackType type, File dir, Map<String, PackLoader> registry) {
		this.type = type;
		this.dir = dir;
		this.registry = registry;
	}

	public PackType type() {
		return type;
	}

	public void load() {
		if (registry == null) throw new NullPointerException("Registry is null");
		for (var entry : registry.entrySet()) {
			var folder = new File(dir, entry.getKey());
			if (!folder.exists()) folder.mkdirs();
			entry.getValue().load(folder);
		}
	}

	public enum PackType {
		GLOBAL,
		PACK
	}
}