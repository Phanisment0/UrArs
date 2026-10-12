package io.phanisment.urars.config.pack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class PackFolderRegistry {
	private final Map<String, PackLoader>	loaders = new HashMap<>();

	public void register(String name, PackLoader loader) {
		loaders.put(name, loader);
	}
	
	public Map<String, PackLoader> loaders() {
		return Collections.unmodifiableMap(loaders);
	}
}