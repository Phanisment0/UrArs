package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;

import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.mod.platform.ModAdapter;
import kr.toxicity.model.api.platform.PlatformBillboard;
import kr.toxicity.model.api.tracker.TrackerUpdateAction;
import kr.toxicity.model.api.util.function.BonePredicate;

import net.minecraft.world.entity.Entity;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		key = "billboard",
		namespace = "bettermodel",
		mods = { "bettermodel" }
)
public class ModelBillboardMechanic extends SkillMechanic implements IEntityTarget {
	private final Node model;
	private final Node bone;
	private final Node billboard;

	public ModelBillboardMechanic(SkillLineConfig config) {
		super(config);
		model = config.getMathString(aliases("model", "m"), "");
		bone = config.getMathString(aliases("bone", "b"), "");
		billboard = config.getMathString(aliases("billboard", "bb"), "fixed");
	}

	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		if (target == null) return;

		String model_name = model.evalAsString(ctx.evalContext());
		String bone_name = bone.evalAsString(ctx.evalContext());
		String type = billboard.evalAsString(ctx.evalContext());

		if (model_name.isBlank()) return;

		PlatformBillboard billboard;
		try {
			billboard = PlatformBillboard.valueOf(type.toUpperCase());
		} catch (IllegalArgumentException e) {
			billboard = PlatformBillboard.FIXED;
		}

		var action = TrackerUpdateAction.billboard(billboard);

		BetterModel.registry(ModAdapter.adapt(target)).map(reg -> reg.tracker(model_name)).ifPresent(tracker -> {
			if (bone_name.isBlank()) tracker.update(action);
			else tracker.update(action, BonePredicate.name(bone_name));
		});
	}
}