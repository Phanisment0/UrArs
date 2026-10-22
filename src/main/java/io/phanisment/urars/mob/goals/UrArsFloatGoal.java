package io.phanisment.urars.mob.goals;

import io.phanisment.urars.mob.IGoalCondition;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;

@RegistryInfo(
		author = "Phanisment",
		namespace = "minecraft",
		key = "float"
)
public class UrArsFloatGoal extends FloatGoal implements IGoalCondition {
	public UrArsFloatGoal(Mob mob, SkillLineConfig config) {
		super(mob);
	}
}