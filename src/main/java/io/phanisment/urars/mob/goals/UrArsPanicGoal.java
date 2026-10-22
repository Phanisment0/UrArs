package io.phanisment.urars.mob.goals;

import io.phanisment.urars.mob.IGoalCondition;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		namespace = "minecraft",
		key = "panic"
)
public class UrArsPanicGoal extends PanicGoal implements IGoalCondition {
	public UrArsPanicGoal(Mob mob, SkillLineConfig c) {
		super((PathfinderMob)mob, c.getDouble(aliases("speed", "s"), 1.25));
	}

	@Override
	public boolean isPass(Mob mob) {
		return mob instanceof PathfinderMob;
	}
}