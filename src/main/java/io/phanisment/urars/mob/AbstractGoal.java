package io.phanisment.urars.mob;

import io.phanisment.urars.skill.config.SkillLineConfig;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class AbstractGoal extends Goal implements IGoalCondition {
	public AbstractGoal(Mob mob, SkillLineConfig config) {
	}
}
