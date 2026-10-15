package io.phanisment.urars.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import io.phanisment.urars.config.pack.Pack;
import io.phanisment.urars.config.pack.Pack.PackType;
import io.phanisment.urars.config.pack.PackFolderRegistry;

public final class UrArsPackConfig {
	private static final String PACKS_DIR = "packs";
	private static final String PACK_SUFFIX = ".urars";

	private final File location;
	private final PackFolderRegistry registry;

	private Pack global;
	private final List<Pack> packs = new ArrayList<>();

	public UrArsPackConfig(File location, PackFolderRegistry registry) {
		this.location = location;
		this.registry = registry;
	}

	public void load() {
		packs.clear();
		global = null;

		createDir(location);

		global = new Pack(PackType.GLOBAL, "urars", location, registry.loaders());
		global.load();

		File packs_folder = createDir(new File(location, PACKS_DIR));
		File[] dirs = packs_folder.listFiles(f -> f.isDirectory() && !f.isHidden());
		if (dirs == null) return;

		Arrays.sort(dirs, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
		for (File dir : dirs) {
			Pack pack = new Pack(PackType.PACK, packName(dir), dir, registry.loaders());
			pack.load();
			packs.add(pack);
		}
	}

	private static String packName(File dir) {
		String name = dir.getName().toLowerCase(Locale.ROOT);
		return name.endsWith(PACK_SUFFIX) ? name.substring(0, name.length() - PACK_SUFFIX.length()) : name;
	}

	private static File createDir(File file) {
		if (!file.exists() && !file.mkdirs()) throw new IllegalStateException("Could not create folder: " + file.getAbsolutePath());
		return file;
	}

	public Pack global() {
		return global;
	}

	/**
	 * Only the named packs under /packs (does not include global).
	 */
	public List<Pack> packs() {
		return Collections.unmodifiableList(packs);
	}

	/**
	 * Global first, then every named pack.
	 */
	public List<Pack> all() {
		List<Pack> all = new ArrayList<>(packs.size() + 1);
		if (global != null) all.add(global);
		all.addAll(packs);
		return all;
	}
}