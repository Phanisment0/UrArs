package io.phanisment.urars.mob.goals;

import io.phanisment.urars.mob.IGoalCondition;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		namespace = "minecraft",
		key = "fleeSun"
)
public class UrArsFleeSunGoal extends FleeSunGoal  implements IGoalCondition {
	public UrArsFleeSunGoal(Mob mob, SkillLineConfig config) {
		super((PathfinderMob)mob, config.getDouble(aliases("speed", "s"), 0));
	}

	@Override
	public boolean isPass(Mob mob) {
		return mob instanceof PathfinderMob;
	}
}
