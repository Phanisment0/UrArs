package io.phanisment.urars.skill.template;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.mod.platform.ModAdapter;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import net.minecraft.world.entity.Entity;

import static io.phanisment.urars.util.ActionUtils.aliases;

public abstract class ModeledMechanic extends SkillMechanic implements IEntityTarget {
	protected final Node model;

	public ModeledMechanic(SkillLineConfig config) {
		super(config);
		model = config.getMathString(aliases("model", "m", "id", "i"), "");
	}

	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		var registry = BetterModel.registry(ModAdapter.adapt(target));
		if (registry.isEmpty()) return;

		String model_str = model.evalAsString(ctx.evalContext());
		String model_id = model_str.isEmpty() ? ctx.getStorage("model", String.class) : model_str;
		if (model_id == null || model_id.isEmpty()) return;

		var tracker = registry.get().tracker(model_id);
		if (tracker == null) return;

		update(ctx, target, registry.get(), model_id, tracker);
	}

	public abstract void update(SkillContext ctx, Entity target, EntityTrackerRegistry registry, String model_id, EntityTracker tracker);
}
