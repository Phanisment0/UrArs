package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.skill.SkillMechanic;

import java.util.List;

import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillCondition;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.INoTarget;
import net.minecraft.world.entity.Entity;
import io.phanisment.urars.skill.target.IEntityTarget;

@RegistryInfo(
	author = "Phanisment",
	key = "if"
)
public class IfMechanic extends SkillMechanic implements INoTarget, IEntityTarget {
	private final List<SkillCondition> conditions;
	private final MechanicsExecutor then;
	private final boolean at_target;
	public IfMechanic(SkillLineConfig config) {
		super(config);
		this.conditions = config.getConditions("conditions", "c");
		this.then = config.getMechanics("do");
		this.at_target = config.getBoolean(new String[] {"attarget", "at"}, false);
	}
	
	@Override
	public void cast(SkillContext ctx) {
		this.castAtEntity(ctx, ctx.entity());
	}
	
	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		if (at_target) {
			var target_context = new SkillContext(target);
			if (!conditions.stream().allMatch(c -> c.execute(target_context))) return;
			then.execute(target_context);
			return;
		}
		if (!conditions.stream().allMatch(c -> c.execute(ctx))) return;
		then.execute(ctx);
	}
}