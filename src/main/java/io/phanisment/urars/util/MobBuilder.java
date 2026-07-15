package io.phanisment.urars.util;

import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

public final class MobBuilder {
	@NonNull
	private final EntityType<?> type;
	@NonNull
	private Location location;

	private Consumer<Entity> before_spawn;
	private boolean silent_spawn = false;

	private MobBuilder(EntityType<?> type, Location location) {
		this.type = type;
		this.location = location;
	}

	public static MobBuilder create(final EntityType<?> id, Location location) {
		return new MobBuilder(id, location);
	}

	public static MobBuilder create(final Identifier id, Location location) throws IllegalArgumentException {
		EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElseThrow(() -> new IllegalArgumentException("Unknown entity type: " + id));
		return new MobBuilder(type, location);
	}

	public MobBuilder location(final Location location) {
		this.location = location;
		return this;
	}

	public MobBuilder silentSpawn(final boolean silent) {
		this.silent_spawn = silent;
		return this;
	}

	public MobBuilder beforeSpawn(final Consumer<Entity> event) {
		this.before_spawn = event;
		return this;
	}

	@Nullable
	public Entity build(final EntitySpawnReason reason) {
		Entity entity = type.create(location.level(), reason);
		if (entity == null) return null;

		entity.snapTo(location.pos());
		if (entity instanceof Mob mob) {
			mob.yHeadRot = mob.getYRot();
			mob.yBodyRot = mob.getYRot();
			mob.finalizeSpawn(location.level(), location.level().getCurrentDifficultyAt(mob.blockPosition()), reason, null);
		}

		if (before_spawn != null) this.before_spawn.accept(entity);

		return entity;
	}

	@Nullable
	public Entity spawn() {
		return this.spawn(EntitySpawnReason.COMMAND);
	}

	@Nullable
	public Entity spawn(final EntitySpawnReason reason) {
		return this.spawn(this.build(reason));
	}

	public Entity spawn(final Entity entity) {
		location.level().addFreshEntityWithPassengers(entity);
		if ((entity instanceof Mob mob) && !silent_spawn) mob.playAmbientSound();
		return entity;
	}

	public static Entity spawn(final Entity entity, final Location location) {
		location.level().addFreshEntityWithPassengers(entity);
		if (entity instanceof Mob mob) mob.playAmbientSound();
		return entity;
	}
}