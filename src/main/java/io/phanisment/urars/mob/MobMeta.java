
package io.phanisment.urars.mob;

import java.io.File;
import java.text.ParseException;

import io.phanisment.urars.mob.ai.MobAi;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import io.phanisment.urars.Constant;
import io.phanisment.urars.UrArsComponents;
import io.phanisment.urars.parser.file.config.ConfigSection;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.TriggerType;
import io.phanisment.urars.skill.config.SkillConfigSection;
import io.phanisment.urars.util.Location;
import io.phanisment.urars.util.MobBuilder;
import io.phanisment.urars.util.UrArsDebugger;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public class MobMeta {
	private final Identifier id;
	private final Identifier type;
	private final MechanicsExecutor mechanics;

	@Nullable
	private final String name;
	private boolean silent;
	private boolean invulnerable;
	private boolean no_gravity;

	@Nullable
	private final MobModelData entity_model;
	@Nullable
	private final MobAi ai;

	public MobMeta(final Identifier id, final File file, final SkillConfigSection config) {
		this.id = id;
		this.type = parseType(id, config.getString("type", "minecraft:zombie"));
		this.mechanics = loadMechanics(id, config);
		this.ai = loadAi(id, config);

		this.name = config.getString("name", null);

		try {
			ConfigSection settings = config.getSection("settings");
			this.silent = settings.getBoolean("silent", false);
			this.invulnerable = settings.getBoolean("invulnerable", false);
			this.no_gravity = settings.getBoolean("no-gravity", false);
		} catch (ParseException e) {
			UrArsDebugger.error("Failed to parse section 'settings' in file " + file.getPath() + "'", e);
		}

		this.entity_model = loadModel(id, config);
		UrArsDebugger.debug("Loaded mob '{}': type='{}', model={}", id, type, entity_model != null);
	}

	private static Identifier parseType(final Identifier id, final String raw_type) {
		String cleaned = raw_type.strip().toLowerCase();
		Identifier type = Identifier.tryParse(cleaned);
		if (type == null) throw new IllegalArgumentException("Invalid entity type '" + raw_type.replace("\r", "\\r").replace("\n", "\\n") + "' for mob '" + id + "'");
		return type;
	}

	private static MobAi loadAi(Identifier id, SkillConfigSection config) {
		try {
			return new MobAi(config);
		} catch (Exception e) {
			UrArsDebugger.error("Failed to load AI for mob '" + id + "'", e);
			return null;
		}
	}

	private static MechanicsExecutor loadMechanics(final Identifier id, final SkillConfigSection config) {
		try {
			return config.getMechanics("mechanics");
		} catch (Exception e) {
			UrArsDebugger.error("Failed to load mechanics for mob '" + id + "'", e);
			return new MechanicsExecutor();
		}
	}

	@Nullable
	private static MobModelData loadModel(final Identifier id, final SkillConfigSection config) {
		if (!Constant.BETTER_MODEL_LOADED) return null;
		try {
			ConfigSection section = config.getSection("model");
			return section == null ? null : new MobModelData(section);
		} catch (Exception e) {
			UrArsDebugger.error("Failed to load model configuration for mob '" + id + "'", e);
			return null;
		}
	}

	public boolean spawn(final Location loc) {
		UrArsDebugger.debug("Spawning mob '{}': type='{}', position={}", id, type, loc.pos());

		MobBuilder builder = MobBuilder.create(type, loc);

		if (builder == null) {
			UrArsDebugger.warn("Cannot spawn mob '{}': entity type '{}' is not registered", id, type);
			return false;
		}

		Entity entity = builder.beforeSpawn(this::beforeSpawn).spawn();

		if (entity == null) {
			UrArsDebugger.warn("Cannot spawn mob '{}': failed to create entity of type '{}'", id, type);
			return false;
		}

		if (entity_model != null) entity_model.apply(entity);
		mechanics.execute(TriggerType.ON_SPAWN, new SkillContext(entity));
		UrArsDebugger.debug("Spawned mob '{}': entity='{}', uuid={}", id, entity.getType(), entity.getUUID());
		return true;
	}

	private void beforeSpawn(final Entity entity) {
		UrArsDebugger.trace("Preparing mob '{}' before spawn: entity={}", id, entity.getUUID());
		UrArsComponents.MOB.get(entity).id(id.toString());

		if (name != null) entity.setCustomName(Component.literal(name));

		entity.setSilent(silent);
		entity.setInvulnerable(invulnerable);
		entity.setNoGravity(no_gravity);

		if (mechanics != null) mechanics.execute(TriggerType.ON_PRE_SPAWN, new SkillContext(entity));
		applyAi(entity);
	}

	public void applyAi(Entity entity) {
		if (ai != null && !ai.isEmpty()) ai.apply(entity);
	}
}