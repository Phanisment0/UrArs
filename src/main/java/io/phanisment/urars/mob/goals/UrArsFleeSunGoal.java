package io.phanisment.urars.mob.goals;

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
public class UrArsFleeSunGoal extends FleeSunGoal {
	public UrArsFleeSunGoal(Mob mob, SkillLineConfig config) {
		super(asPathfinder(mob), config.getDouble(aliases("speed", "s"), 0));
	}

	private static PathfinderMob asPathfinder(Mob mob) {
		if (mob instanceof PathfinderMob p) return p;
		throw new IllegalArgumentException("flee_sun requires a PathfinderMob, got " + mob.getType());
	}
}
