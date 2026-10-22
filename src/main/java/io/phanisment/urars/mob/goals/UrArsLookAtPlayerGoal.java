package io.phanisment.urars.mob.goals;

import io.phanisment.urars.mob.IGoalCondition;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		namespace = "minecraft",
		key = "lookAtPlayer"
)
public class UrArsLookAtPlayerGoal extends LookAtPlayerGoal implements IGoalCondition {
	public UrArsLookAtPlayerGoal(Mob mob, SkillLineConfig c) {
		super(mob, Player.class, (float) c.getDouble(aliases("range", "r"), 8.0));
	}
}