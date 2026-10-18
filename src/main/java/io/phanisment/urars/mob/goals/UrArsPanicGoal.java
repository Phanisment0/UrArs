package io.phanisment.urars.mob.goals;

import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.GoalUtils;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.PanicGoal;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		namespace = "minecraft",
		key = "panic"
)
public class UrArsPanicGoal extends PanicGoal {
	public UrArsPanicGoal(Mob mob, SkillLineConfig c) {
		super(GoalUtils.pathfinder(mob, "panic"), c.getDouble(aliases("speed", "s"), 1.25));
	}
}