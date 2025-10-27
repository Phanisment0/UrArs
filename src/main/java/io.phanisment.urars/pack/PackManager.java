package io.phanisment.urars.pack;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;

public final class PackManager {
	private static final Map<String, Pack> handler = new HashMap<>();
	private static final File loc = new File("config/urars");
	
	public static void load() {
		handler.clear();
		handler.put("", new Pack(loc));
		for (File file : getPackFolder().listFiles()) {
			if (!file.isDirectory()) continue;
			var pack = new Pack(file);
			handler.put(pack.name(), pack);
		}
	}
	
	public static void unload() {
		handler.clear();
	}
	
	private static File getPackFolder() {
		File folder = new File(loc, "packs");
		if (!folder.exists()) folder.mkdirs();
		return folder;
	}
	
	public static Collection<Pack> getPacks() {
		return handler.values();
	}
	
	public static Pack getPack(String name) {
		return handler.get(name);
	}
}