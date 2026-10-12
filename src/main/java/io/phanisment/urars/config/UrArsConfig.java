package io.phanisment.urars.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.phanisment.urars.config.pack.Pack;
import io.phanisment.urars.config.pack.PackFolderRegistry;

public final class UrArsConfig {
	private final File location;
	private final PackFolderRegistry registry;
	private final List<Pack> packs = new ArrayList<>();

	public UrArsConfig(File location, PackFolderRegistry registry) {
		this.location = location;
		this.registry = registry;
	}

	public void load() {
		packs.clear();

		createDir(location);

		File packs_folder = createDir(new File(location, "packs"));

		File[] dirs = packs_folder.listFiles(File::isDirectory);
		if (dirs != null) for (var p : dirs) {
			var cfg = new Pack(p, registry.loaders());
			cfg.load();
			packs.add(cfg);
		}
	}

	private File createDir(File file) {
		if (!file.exists()) file.mkdirs();
		return file;
	}

	public List<Pack> packs() {
		return packs;
	}
}