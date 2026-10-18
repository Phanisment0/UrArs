package io.phanisment.urars.mob.targets;

import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.GoalUtils;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

@RegistryInfo(
		author = "Phanisment",
		namespace = "minecraft",
		key = "hurtByTarget"
)
public class UrArsHurtByTargetGoal extends HurtByTargetGoal {
	public UrArsHurtByTargetGoal(Mob mob, SkillLineConfig c) {
		super(GoalUtils.pathfinder(mob, "hurt_by_target"));
	}
}