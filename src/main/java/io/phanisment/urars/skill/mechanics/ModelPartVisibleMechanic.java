package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.template.ModeledMechanic;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import kr.toxicity.model.api.tracker.TrackerUpdateAction;
import kr.toxicity.model.api.util.function.BonePredicate;
import net.minecraft.world.entity.Entity;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		key = "partVisibility",
		aliases = { "partvis" },
		namespace = "bettermodel",
		mods = { "bettermodel" }
)
public class ModelPartVisibleMechanic extends ModeledMechanic {
	protected final Node part;
	protected final Node visibility;

	public ModelPartVisibleMechanic(SkillLineConfig config) {
		super(config);
		part = config.getMathString(aliases("partId", "part", "pid", "p"), null);
		visibility = config.getMathBoolean(aliases("visibility", "visible", "v"), false);
	}

	@Override
	public void update(SkillContext ctx, Entity target, EntityTrackerRegistry registry, String model_id, EntityTracker tracker) {
		var action = TrackerUpdateAction.togglePart(visibility.evalAsBoolean(ctx.evalContext()));
		String part_str = part.evalAsString(ctx.evalContext());

		if (part_str == null) tracker.update(action);
		else tracker.update(action, BonePredicate.name(part_str));
	}
}