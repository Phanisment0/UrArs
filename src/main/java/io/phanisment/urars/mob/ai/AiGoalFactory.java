package io.phanisment.urars.mob.ai;

import io.phanisment.urars.skill.config.SkillLineConfig;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

@FunctionalInterface
public interface AiGoalFactory<T extends Goal> {
	T create(Mob mob, SkillLineConfig config);
}