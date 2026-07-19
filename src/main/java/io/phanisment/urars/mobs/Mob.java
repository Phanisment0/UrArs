package io.phanisment.urars.mobs;

import static io.phanisment.urars.UrArs.LOGGER;

import org.jspecify.annotations.Nullable;

import io.phanisment.urars.Constant;
import io.phanisment.urars.UrArsComponents;
import io.phanisment.urars.config.ConfigSection;
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

public class Mob {
	private final Identifier id;
	@SuppressWarnings("unused")
	private final Resource resource;
	@SuppressWarnings("unused")
	private final SkillConfigSection config;

	private Identifier type;
	private MechanicsExecutor mechanics;
	@Nullable
	private MobModelData entity_model;

	public Mob(final Identifier id, final Resource resource, final SkillConfigSection config) {
		this.id = id;
		this.resource = resource;
		this.config = config;

		this.type = config.getIdentifier("type", Identifier.fromNamespaceAndPath("minecraft", "zombie"));
		this.mechanics = config.getMechanics("mechanics");
		if (Constant.BETTER_MODEL_LOADED) {
			var section = config.getSection("model");
			if (section != null) this.entity_model = new MobModelData(section);
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
		private String model;
		public MobModelData(ConfigSection config) {
			this.model = config.getString("id");
		}

		public void apply(Entity target) {
			if (model == null) return;
			LOGGER.info("Apply model " + model);
			EntityTracker tracker = BetterModel.model(model).map(r -> r.getOrCreate(ModAdapter.adapt(target))).orElseGet(null);
		}
	}
}