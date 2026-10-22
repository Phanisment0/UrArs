package io.phanisment.urars.mob;

import net.minecraft.world.entity.Mob;

public interface IGoalCondition {
 default boolean isPass(Mob mob) {
	 return true;
 }
}