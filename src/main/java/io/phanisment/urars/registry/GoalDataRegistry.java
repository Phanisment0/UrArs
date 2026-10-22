package io.phanisment.urars.registry;

import io.phanisment.urars.mob.ai.AiGoalFactory;
import net.minecraft.world.entity.ai.goal.Goal;

public class GoalDataRegistry<T extends Goal> extends AnnotatedRegistry<T, AiGoalFactory<T>> {
}