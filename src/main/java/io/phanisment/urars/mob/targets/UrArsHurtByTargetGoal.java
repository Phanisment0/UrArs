package io.phanisment.urars.mob.targets;

import io.phanisment.urars.mob.IGoalCondition;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

@RegistryInfo(
		author = "Phanisment",
		namespace = "minecraft",
		key = "hurtByTarget"
)
public class UrArsHurtByTargetGoal extends HurtByTargetGoal implements IGoalCondition {
	public UrArsHurtByTargetGoal(Mob mob, SkillLineConfig c) {
		super((PathfinderMob)mob);
	}

	@Override
	public boolean isPass(Mob mob) {
		return mob instanceof PathfinderMob;
	}
}