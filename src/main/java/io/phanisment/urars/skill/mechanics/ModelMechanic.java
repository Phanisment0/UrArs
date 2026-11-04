package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;

import static io.phanisment.urars.util.ActionUtils.aliases;
import static io.phanisment.urars.util.ActionUtils.aliasNull;

import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.mod.platform.ModAdapter;
import kr.toxicity.model.api.tracker.ModelScaler;
import kr.toxicity.model.api.tracker.TrackerModifier;
import net.minecraft.world.entity.Entity;

@RegistryInfo(
		author = "Phanisment",
		key = "model",
		namespace = "bettermodel",
		mods = { "bettermodel" }
)
public class ModelMechanic extends SkillMechanic implements IEntityTarget {
	protected final Node model_id;
	protected final Node scale;
	protected final Node sight_trace;
	protected final Node damage_animation;
	protected final Node damage_tint;

	public ModelMechanic(SkillLineConfig config) {
		super(config);
		if (config.singleArgument()) model_id = config.getMathString(aliasNull(), "");
		else model_id = config.getMathString(aliases("model", "m", "id", "i"), "");

		scale = config.getMathNumber(aliases("scale", "s"), 1f);
		sight_trace = config.getMathBoolean(aliases("sightTrace", "st"), true);
		damage_animation = config.getMathBoolean(aliases("damageAnimation", "da"), false);
		damage_tint = config.getMathBoolean(aliases("damageTint", "dt"), true);
	}

	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		String model_str = model_id.evalAsString(ctx.evalContext());
		if (model_str.isEmpty()) return;

		var renderer = BetterModel.modelOrNull(model_str);
		if (renderer == null) return;

		var tracker = renderer.getOrCreate(ModAdapter.adapt(target), new TrackerModifier(
				sight_trace.evalAsBoolean(ctx.evalContext()),
				damage_animation.evalAsBoolean(ctx.evalContext()),
				damage_tint.evalAsBoolean(ctx.evalContext())
		));

		tracker.scaler(ModelScaler.entity().multiply(scale.evalAsFloat(ctx.evalContext())));

		ctx.setStorage("model", model_str);
	}
}