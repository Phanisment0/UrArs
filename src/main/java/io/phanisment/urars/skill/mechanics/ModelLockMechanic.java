package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.template.ModeledMechanic;

import static io.phanisment.urars.util.ActionUtils.aliases;

import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import net.minecraft.world.entity.Entity;

@RegistryInfo(
		author = "Phanisment",
		key = "lockModel",
		namespace = "bettermodel",
		mods = { "bettermodel" }
)
public class ModelLockMechanic extends ModeledMechanic {
	protected final Node lock;

	public ModelLockMechanic(SkillLineConfig config) {
		super(config);
		lock = config.getMathBoolean(aliases("lock", "l"), true);
	}

	@Override
	public void update(SkillContext ctx, Entity target, EntityTrackerRegistry registry, String model_id, EntityTracker tracker) {
		tracker.bodyRotator().lockRotation(lock.evalAsBoolean(ctx.evalContext()));
	}
}
