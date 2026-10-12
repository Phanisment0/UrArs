package io.phanisment.urars.mob;

import static io.phanisment.urars.UrArs.LOGGER;

import io.phanisment.urars.lib.file_parser.config.ConfigSection;
import org.jspecify.annotations.Nullable;

import io.phanisment.urars.Constant;
import io.phanisment.urars.UrArsComponents;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.TriggerType;
import io.phanisment.urars.skill.config.SkillConfigSection;
import io.phanisment.urars.util.Location;
import io.phanisment.urars.util.MobBuilder;

import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.mod.platform.ModAdapter;
import kr.toxicity.model.api.tracker.EntityTracker;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.Entity;

import java.io.File;

public class Mob {
	private final Identifier id;

	private final File file;

	private final SkillConfigSection config;

	private final Identifier type;
	private MechanicsExecutor mechanics = new MechanicsExecutor();
	@Nullable
	private MobModelData entity_model;

	public Mob(final Identifier id, final File file, final SkillConfigSection config) {
		this.id = id;
		this.file = file;
		this.config = config;

		this.type = Identifier.tryParse(config.getString("type", "minecraft:zombie"));
		try {
			this.mechanics = config.getMechanics("mechanics");
			if (Constant.BETTER_MODEL_LOADED) {
				var section = config.getSection("model");
				if (section != null) this.entity_model = new MobModelData(section);
			}
		} catch (Exception e) {
			// TODO: Make it verbose
		}
	}

	public void spawn(final Location loc) {
		Entity entity = MobBuilder.create(type, loc).beforeSpawn(e -> {
			UrArsComponents.MOB.get(e).id(id.toString());
			mechanics.execute(TriggerType.ON_PRE_SPAWN, new SkillContext(e));
		}).spawn();
		if (Constant.BETTER_MODEL_LOADED && entity_model != null) entity_model.apply(entity);
		mechanics.execute(TriggerType.ON_SPAWN, new SkillContext(entity));
	}

	public static class MobModelData {
		private final String model;
		public MobModelData(ConfigSection config) {
			this.model = config.getString("id");
		}

		public void apply(Entity target) {
			if (model == null) return;
			LOGGER.info("Apply model {}", model);
			EntityTracker tracker = BetterModel.model(model).map(r -> r.getOrCreate(ModAdapter.adapt(target))).orElseGet(null);
		}
	}
}