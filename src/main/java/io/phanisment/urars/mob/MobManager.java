package io.phanisment.urars.mob;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;

import io.phanisment.urars.mob.type.ConfigurableEntity;

import static io.phanisment.urars.UrArs.ID;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

public final class MobManager {
	public static final EntityType<ConfigurableEntity> CONFIGURABLE_ENTITY;
	private static final Map<Identifier, Mob> mobs = new HashMap<>();
	
	private MobManager() {
	}
	
	/**
	 * Just to load in Initialzer
	 */
	public static void load() {
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
	
	private static <T extends Entity> EntityType<T> register(Identifier id, EntityType.Builder<T> type) {
		return (EntityType<T>)Registry.register(Registries.ENTITY_TYPE, id, type.build(id.getPath()));
	}
	
	static {
		CONFIGURABLE_ENTITY = register(Identifier.of(ID, "configurable_entity"), EntityType.Builder.create(ConfigurableEntity::new, SpawnGroup.MISC).dimensions(0.5F, 0.5F));
	}
}