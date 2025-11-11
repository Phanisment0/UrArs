package io.phanisment.urars.skill.aura;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public final class AuraManager {
	private static final Map<UUID, Map<String, Aura>> handler = new HashMap<>();
	
	public static void set(UUID uuid, String name, Aura aura) {
		handler.computeIfAbsent(uuid, k -> new HashMap<>()).put(name, aura);
	}
	
	public static Aura get(UUID uuid, String name) {
		return handler.containsKey(uuid) ? handler.get(uuid).get(name) : null;
	}
	
	public static void remove(UUID uuid, String name) {
		
	}
	
	// casted on tick
	public void init() {
		
	}
}