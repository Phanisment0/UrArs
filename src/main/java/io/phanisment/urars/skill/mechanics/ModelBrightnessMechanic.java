package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;

import io.phanisment.urars.skill.template.ModeledMechanic;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import kr.toxicity.model.api.tracker.TrackerUpdateAction;
import net.minecraft.world.entity.Entity;

@RegistryInfo(
		author = "Phanisment",
		key = "brightness",
		namespace = "bettermodel",
		mods = { "bettermodel" }
)
public class ModelBrightnessMechanic extends ModeledMechanic {
	protected final Node sky;
	protected final Node block;

	public ModelBrightnessMechanic(SkillLineConfig config) {
		super(config);
		sky = config.getMathNumber("sky", "s");
		block = config.getMathNumber("block", "b");
	}

	@Override
	public void update(SkillContext ctx, Entity target, EntityTrackerRegistry registry, String model_id, EntityTracker tracker) {
		tracker.update(TrackerUpdateAction.brightness(block.evalAsInt(ctx.evalContext()), sky.evalAsInt(ctx.evalContext())));
	}
}
