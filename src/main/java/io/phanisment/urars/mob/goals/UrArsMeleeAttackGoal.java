package io.phanisment.urars.mob.goals;

import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.GoalUtils;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		namespace = "minecraft",
		key = "meleeAttack"
)
public class UrArsMeleeAttackGoal extends MeleeAttackGoal {
	public UrArsMeleeAttackGoal(Mob mob, SkillLineConfig c) {
		super(GoalUtils.pathfinder(mob, "meleeAttack"), c.getDouble(aliases("speed", "s"), 1.0), c.getBoolean(aliases("followUnseen", "fu"), true));
	}
}