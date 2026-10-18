package io.phanisment.urars.util;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;

public final class GoalUtils {
	private GoalUtils() {}

	public static PathfinderMob pathfinder(Mob mob, String goal) {
		if (mob instanceof PathfinderMob p) return p;
		throw new IllegalArgumentException(goal + " requires a PathfinderMob, got " + mob.getType());
	}
}