package io.phanisment.urars.mob;

import io.phanisment.urars.skill.config.SkillLineConfig;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

import static io.phanisment.urars.util.ActionUtils.aliases;

public abstract class AbstractTargetGoal extends TargetGoal implements IGoalCondition {
	public AbstractTargetGoal(Mob mob, SkillLineConfig config) {
		super(mob, config.getBoolean(aliases("mustSee", "ms"), false));
	}
}