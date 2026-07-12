package io.phanisment.urars.mobs;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.resources.Identifier;

public final class MobManager {
	private static final Map<Identifier, Mob> mobs = new ConcurrentHashMap<>();

	private MobManager() {

	}

	public static boolean registerMob(Identifier id, Mob mob) {
		return mobs.putIfAbsent(id, mob) == null;
	}

	public static Optional<Mob> getMob(Identifier id) {
		return Optional.ofNullable(mobs.get(id));
	}

	public static Set<Identifier> getMobs() {
		return mobs.keySet();
	}
}
