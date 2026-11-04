package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;

import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.mod.platform.ModAdapter;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.TrackerUpdateAction;
import net.minecraft.world.entity.Entity;

@RegistryInfo(
		author = "Phanisment",
		key = "brightness",
		namespace = "bettermodel",
		mods = { "bettermodel" }
)
public class ModelBrightnessMechanic extends SkillMechanic implements IEntityTarget {
	protected final Node model;
	protected final Node sky;
	protected final Node block;

	public ModelBrightnessMechanic(SkillLineConfig config) {
		super(config);
		model = config.getMathString("model", "m", "id", "i");
		sky = config.getMathNumber("sky", "s");
		block = config.getMathNumber("block", "b");
	}

	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		var entity = ModAdapter.adapt(target);
		String model_str = model.evalAsString(ctx.evalContext());
		var renderer = BetterModel.registryOrNull(entity);
		if (renderer == null) return;;

		EntityTracker tracker = renderer.getOrCreate(model_str, reg -> reg.tracker(model_str));
		if (tracker == null) return;

		tracker.update(TrackerUpdateAction.brightness(block.evalAsInt(ctx.evalContext()), sky.evalAsInt(ctx.evalContext())));
	}
}
