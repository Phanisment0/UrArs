package io.phanisment.urars.mob;

import java.io.File;
import java.text.ParseException;
import java.util.Locale;
import java.util.Objects;

import io.phanisment.urars.mob.ai.MobAi;
import io.phanisment.urars.util.BufferedString;
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
	private final Settings settings;

	private final @Nullable String name;
	private final @Nullable MobModelData entity_model;
	private final @Nullable MobAi ai;

	public MobMeta(final Identifier id, final File file, final SkillConfigSection config) {
		this.id = id;

		this.type = parseType(config.getBufferedString("type", new BufferedString("minecraft:zombie")));
		this.name = config.getString("name", null);

		this.mechanics = loadMechanics(id, config);
		this.ai = loadAi(id, config);
		this.settings = loadSettings(file, config);
		this.entity_model = loadModel(id, config);

		UrArsDebugger.debug("Loaded mob '{}': type='{}', model={}, mechanic(s)={}", id, type, entity_model != null, mechanics.size());
	}

	public MechanicsExecutor mechanics() {
		return mechanics;
	}

	private Identifier parseType(final BufferedString raw_type) {
		String input = raw_type.toString();
		Identifier type = Identifier.tryParse(input);
		if (type == null) throw new IllegalArgumentException("Invalid entity type '" + input + "' for mob '" + id + "'");
		return type;
	}

	private static Settings loadSettings(final File file, final SkillConfigSection config) {
		try {
			ConfigSection section = config.getSection("settings");
			if (section == null) return new Settings();
			return new Settings()
					.silent(section.getBoolean("silent", false))
					.invulnerable(section.getBoolean("invulnerable", false))
					.no_gravity(section.getBoolean("no-gravity", false));
		} catch (ParseException e) {
			UrArsDebugger.error("Failed to parse section 'settings' in file '" + file.getPath() + "'", e);
			return new Settings();
		}
	}

	@Nullable
	private static MobAi loadAi(final Identifier id, final SkillConfigSection config) {
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

		apply(entity);
		mechanics.execute(TriggerType.ON_PRE_SPAWN, new SkillContext(entity));
	}

	public void apply(final Entity entity) {
		if (name != null) entity.setCustomName(Component.literal(name));
		settings.apply(entity);
		applyAi(entity);
	}

	public void applyAi(final Entity entity) {
		if (ai != null && !ai.isEmpty()) ai.apply(entity);
	}

	private static final class Settings {
		private boolean silent = false;
		private boolean invulnerable = false;
		private boolean no_gravity = false;

		public Settings silent(boolean value) {
			silent = value;
			return this;
		}

		public Settings invulnerable(boolean value) {
			invulnerable = value;
			return this;
		}

		public Settings no_gravity(boolean value) {
			no_gravity = value;
			return this;
		}

		public void apply(Entity entity) {
			entity.setSilent(silent);
			entity.setInvulnerable(invulnerable);
			entity.setNoGravity(no_gravity);
		}
	}
}