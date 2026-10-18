package io.phanisment.urars.mob.goals;

import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;

@RegistryInfo(
		author = "Phanisment",
		namespace = "minecraft",
		key = "randomLook"
)
public class UrArsRandomLookGoal extends RandomLookAroundGoal {
	public UrArsRandomLookGoal(Mob mob, SkillLineConfig c) {
		super(mob);
	}
}