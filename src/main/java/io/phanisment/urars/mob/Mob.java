
package io.phanisment.urars.mob;

import java.io.File;

import org.jspecify.annotations.Nullable;

import io.phanisment.urars.Constant;
import io.phanisment.urars.UrArsComponents;
import io.phanisment.urars.lib.file_parser.config.ConfigSection;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.TriggerType;
import io.phanisment.urars.skill.config.SkillConfigSection;
import io.phanisment.urars.util.Location;
import io.phanisment.urars.util.MobBuilder;
import io.phanisment.urars.util.UrArsDebugger;

import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.mod.platform.ModAdapter;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public class Mob {
	private final Identifier id;
	private final Identifier type;
	private final MechanicsExecutor mechanics;

	@Nullable
	private final MobModelData entity_model;

	public Mob(final Identifier id, final File file, final SkillConfigSection config) {
		this.id = id;
		this.type = parseType(id, config.getString("type", "minecraft:zombie"));
		this.mechanics = loadMechanics(id, config);
		this.entity_model = loadModel(id, config);

		UrArsDebugger.debug("Loaded mob '{}': type='{}', model={}", id, type, entity_model != null);
	}

	private static Identifier parseType(final Identifier id, final String raw_type) {
		String cleaned = raw_type.strip().toLowerCase();
		Identifier type = Identifier.tryParse(cleaned);
		if (type == null) throw new IllegalArgumentException("Invalid entity type '" + raw_type.replace("\r", "\\r").replace("\n", "\\n") + "' for mob '" + id + "'");
		return type;
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
		mechanics.execute(TriggerType.ON_PRE_SPAWN, new SkillContext(entity));
	}

	public static class MobModelData {
		@Nullable
		private final String model;

		public MobModelData(final ConfigSection config) {
			this.model = config.getString("id").strip();
		}

		public void apply(final Entity target) {
			if (model == null || model.isBlank()) {
				UrArsDebugger.trace("Skipping empty model for entity '{}'", target.getUUID());
				return;
			}

			UrArsDebugger.debug("Applying model '{}' to entity '{}'", model, target.getUUID());
			BetterModel.model(model).ifPresentOrElse(resource -> resource.getOrCreate(ModAdapter.adapt(target)), () -> UrArsDebugger.warn("Cannot apply model '{}': model was not found", model));
		}
	}
}