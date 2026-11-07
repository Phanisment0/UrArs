package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.template.ModeledMechanic;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import net.minecraft.world.entity.Entity;

@RegistryInfo(
		author = "Phanisment",
		key = "removeModel",
		namespace = "bettermodel",
		mods = { "bettermodel" }
)
public class ModelRemoveMechanic extends ModeledMechanic {
	public ModelRemoveMechanic(SkillLineConfig config) {
		super(config);
	}

	@Override
	public void update(SkillContext ctx, Entity target, EntityTrackerRegistry registry, String model_id, EntityTracker tracker) {
		tracker.despawn();
	}
}
