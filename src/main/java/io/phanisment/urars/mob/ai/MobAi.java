package io.phanisment.urars.mob.ai;

import io.phanisment.urars.mixin.MobAccessor;
import io.phanisment.urars.mob.IGoalCondition;
import io.phanisment.urars.mob.MobManager;
import io.phanisment.urars.parser.file.parser.LineParser;
import io.phanisment.urars.skill.config.SkillConfigSection;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.BufferedString;
import io.phanisment.urars.util.UrArsDebugger;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class MobAi {
	private static final int DEFAULT_PRIORITY = 5;

	private final boolean clear_goals;
	private final boolean clear_targets;

	private final List<Entry<Goal>> goals;
	private final List<Entry<TargetGoal>> targets;

	public MobAi(SkillConfigSection config) {
		this.clear_goals = config.getBoolean("clear-goals", false);
		this.clear_targets = config.getBoolean("clear-targets", false);
		this.goals = load(config, "goals", "goal", c -> MobManager.getGoal(c.key()));
		this.targets = load(config, "target-goals", "target", c -> MobManager.getTargetGoal(c.key()));
	}

	public boolean isEmpty() {
		return !clear_goals && !clear_targets && goals.isEmpty() && targets.isEmpty();
	}

	public void apply(Entity entity) {
		if (!(entity instanceof Mob mob)) return;

		var accessor = (MobAccessor)mob;
		GoalSelector goal_selector = accessor.urars$goalSelector();
		GoalSelector target_selector = accessor.urars$targetSelector();

		if (clear_goals) goal_selector.removeAllGoals(g -> true);
		if (clear_targets) target_selector.removeAllGoals(g -> true);

		add(mob, goal_selector, goals);
		add(mob, target_selector, targets);
	}

	private <T extends Goal> List<Entry<T>> load(SkillConfigSection config, String path, String label, Function<SkillLineConfig, AiGoalFactory<T>> lookup) {
		List<Entry<T>> results = new ArrayList<>();
		try {
			for (LineParser parsed : config.getLine(path)) {
				try {
					var line = new SkillLineConfig(parsed.config());
					AiGoalFactory<T> factory = lookup.apply(line);
					if (factory == null) {
						UrArsDebugger.warn("Unknown AI {} '{}'", label, line.key());
						continue;
					}
					results.add(new Entry<>(parsePriority(line.context()), factory, line));
				} catch (Exception e) {
					UrArsDebugger.warn("Failed to parse AI line in '" + path + "'", e);
				}
			}
		} catch (ParseException e) {
			throw new IllegalStateException("Failed to read '" + path + "'", e);
		}
		return results;
	}

	private <T extends Goal> void add(Mob mob, GoalSelector selector, List<Entry<T>> entries) {
		for (Entry<T> entry : entries) {
			try {
				T goal = entry.factory().create(mob, entry.config());

				if (goal instanceof IGoalCondition condition && !condition.isPass(mob)) {
					UrArsDebugger.warn("AI goal '{}' is not supported on {}", entry.config().key(), mob.getType());
					continue;
				}
				selector.addGoal(entry.priority(), goal);
			} catch (Exception e) {
				UrArsDebugger.warn("Failed to create AI goal '{}'", entry.config().key(), e);
			}
		}
	}

	private static int parsePriority(BufferedString context) {
		if (context == null || context.isBlank()) return DEFAULT_PRIORITY;
		try {
			return context.toInteger();
		} catch (NumberFormatException e) {
			return DEFAULT_PRIORITY;
		}
	}

	private record Entry<T extends Goal>(int priority, AiGoalFactory<T> factory, SkillLineConfig config) {
	}
}