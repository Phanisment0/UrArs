
package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;

import static io.phanisment.urars.util.ActionUtils.aliases;

import io.phanisment.urars.skill.template.ModeledMechanic;
import kr.toxicity.model.api.bone.BoneName;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import kr.toxicity.model.api.util.function.BonePredicate;
import kr.toxicity.model.script.ChangePartScript;

import net.minecraft.world.entity.Entity;

@RegistryInfo(
		author = "Phanisment",
		key = "changePart",
		namespace = "bettermodel",
		mods = { "bettermodel" }
)
public class ModelChangePartMechanic extends ModeledMechanic {
	protected final Node part;
	protected final Node new_model;
	protected final Node new_part;

	public ModelChangePartMechanic(SkillLineConfig config) {
		super(config);
		part = config.getMathString(aliases("part", "partId", "p", "pid"), "");
		new_model = config.getMathString(aliases("newModelId", "newModel", "nm", "nmid"), "");
		new_part = config.getMathString(aliases("newPart", "np", "npid"), "");
	}

	@Override
	public void update(SkillContext ctx, Entity target, EntityTrackerRegistry registry, String model_id, EntityTracker tracker) {
		String part_id = part.evalAsString(ctx.evalContext());
		String new_model_id = new_model.evalAsString(ctx.evalContext());
		String new_part_id = new_part.evalAsString(ctx.evalContext());
		if (part_id == null || new_model_id == null || new_part_id == null) return;

		String current_model = new_model_id.isEmpty() ? model_id : new_model_id;

		new ChangePartScript(BonePredicate.name(current_model).notSet(), current_model, BoneName.of(new_part_id)).accept(tracker);
	}
}
