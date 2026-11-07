package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;

import io.phanisment.urars.skill.template.ModeledMechanic;
import kr.toxicity.model.api.platform.PlatformBillboard;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
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
public class ModelBillboardMechanic extends ModeledMechanic {
	private final Node bone;
	private final Node billboard;

	public ModelBillboardMechanic(SkillLineConfig config) {
		super(config);
		bone = config.getMathString(aliases("bone", "b"), "");
		billboard = config.getMathString(aliases("billboard", "bb"), "fixed");
	}

	@Override
	public void update(SkillContext ctx, Entity target, EntityTrackerRegistry registry, String model_id, EntityTracker tracker) {
		String type = billboard.evalAsString(ctx.evalContext());
		PlatformBillboard billboard_action;
		try {
			billboard_action = PlatformBillboard.valueOf(type.toUpperCase());
		} catch (IllegalArgumentException e) {
			billboard_action = PlatformBillboard.FIXED;
		}

		var action = TrackerUpdateAction.billboard(billboard_action);

		String bone_name = bone.evalAsString(ctx.evalContext());

		if (bone_name.isBlank()) tracker.update(action);
		else tracker.update(action, BonePredicate.name(bone_name));
	}
}