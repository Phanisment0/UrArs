package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;

import static io.phanisment.urars.util.ActionUtils.aliases;

import io.phanisment.urars.skill.template.ModeledMechanic;

import kr.toxicity.model.api.animation.AnimationModifier;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import kr.toxicity.model.api.util.function.FloatSupplier;

import net.minecraft.world.entity.Entity;

@RegistryInfo(
		author = "Phanisment",
		key = "state",
		namespace = "bettermodel",
		mods = { "bettermodel" }
)
public class ModelStateMechanic extends ModeledMechanic {
	protected final Node model;
	protected final Node state;
	protected final Node lerp_in;
	protected final Node lerp_out;
	protected final Node speed;
	protected final Node remove;
	protected final Node priority;

	public ModelStateMechanic(SkillLineConfig config) {
		super(config);
		model = config.getMathString(aliases("model", "m", "id", "i"), "");
		state = config.getMathString(aliases("state", "s"), "");

		lerp_in = config.getMathNumber(aliases("lerpIn", "li"), 1);
		lerp_out = config.getMathNumber(aliases("lerpOut", "lo"), 0);

		speed = config.getMathNumber(aliases("speed", "sp"), 1f);

		remove = config.getMathBoolean(aliases("remove", "r"), false);
		priority = config.getMathNumber(aliases("priority", "p", "pr"), 0);
	}

	@Override
	public void update(SkillContext ctx, Entity target, EntityTrackerRegistry registry, String model_id, EntityTracker tracker) {
		String state_id = state.evalAsString(ctx.evalContext());
		if (state_id.isEmpty()) return;

		if (remove.evalAsBoolean(ctx.evalContext())) {
			tracker.stopAnimation(state_id);
			return;
		}

		float animation_speed = speed.evalAsFloat(ctx.evalContext());

		AnimationModifier modifier = AnimationModifier.builder()
				.start(lerp_in.evalAsInt(ctx.evalContext()))
				.end(lerp_out.evalAsInt(ctx.evalContext()))
				.speed(FloatSupplier.of(animation_speed))
				.priority(priority.evalAsInt(ctx.evalContext()))
				.build();

		tracker.animate(state_id, modifier);
	}
}