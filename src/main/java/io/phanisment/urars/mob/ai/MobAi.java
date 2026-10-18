package io.phanisment.urars.mob.ai;

import io.phanisment.urars.mixin.MobAccessor;
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

public final class MobAi {
	private final boolean clear_goals;
	private final boolean clear_targets;

	private final List<Entry<Goal>> goals;
	private final List<Entry<TargetGoal>> targets;

	public MobAi(SkillConfigSection config) {
		this.clear_goals = config.getBoolean("clear-goals", false);
		this.clear_targets = config.getBoolean("clear-targets", false);
		this.goals = loadGoals(config);
		this.targets = loadTargetGoals(config);
	}

	public boolean isEmpty() {
		return !clear_goals && !clear_targets && goals.isEmpty() && targets.isEmpty();
	}

	public List<Entry<Goal>> loadGoals(SkillConfigSection config) {
		List<Entry<Goal>> results = new ArrayList<>();
		try {
			for (LineParser parsed : config.getLine("goals")) try {
				var c = new SkillLineConfig((parsed.config()));
				AiGoalFactory<Goal> factory = MobManager.getGoal(c.key());
				if (factory == null) {
					UrArsDebugger.warn("Unknown AI goal '{}'", c.key());
					continue;
				}
				results.add(new Entry<>(parseContext(c.context(), 5), factory, c));
			} catch (Exception e) {
				UrArsDebugger.warn("Failed to parse AI line", e);
			}
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return results;
	}

	public List<Entry<TargetGoal>> loadTargetGoals(SkillConfigSection config) {
		List<Entry<TargetGoal>> results = new ArrayList<>();
		try {
			for (LineParser parsed : config.getLine("target-goals")) try {
				var c = new SkillLineConfig((parsed.config()));
				AiGoalFactory<TargetGoal> factory = MobManager.getTargetGoal(c.key());
				if (factory == null) {
					UrArsDebugger.warn("Unknown AI target '{}'", c.key());
					continue;
				}
				results.add(new Entry<>(parseContext(c.context(), 5), factory, c));
			} catch (Exception e) {
				UrArsDebugger.warn("Failed to parse AI line", e);
			}
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return results;
	}

	public void apply(Entity entity) {
		if (!(entity instanceof Mob mob)) return;
		var accessor = (MobAccessor)mob;

		if (clear_goals) accessor.urars$goalSelector().removeAllGoals(g -> true);
		if (clear_targets) accessor.urars$targetSelector().removeAllGoals(g -> true);

		add(mob, accessor.urars$goalSelector(), goals);
		add(mob, accessor.urars$targetSelector(), targets);
		UrArsDebugger.info("AI applied to {}: goals={} targets={}", mob.getType(), accessor.urars$goalSelector().getAvailableGoals().size(), accessor.urars$targetSelector().getAvailableGoals().size());
	}

	private <T extends Goal> void add(Mob mob, GoalSelector selector, List<Entry<T>> entries) {
		for (Entry<T> entry : entries) try {
			T goal = entry.factory().create(mob, entry.config());
			if (goal == null) {
				UrArsDebugger.warn("AI goal '{}' is not supported on {}", entry.config().key(), mob.getType());
				continue;
			}
			selector.addGoal(entry.priority(), goal);
		} catch (Exception e) {
			UrArsDebugger.warn("Failed to create AI goal '{}'", entry.config().key(), e);
		}
	}

	private record Entry<T extends Goal>(int priority, AiGoalFactory<T> factory, SkillLineConfig config) {
	}

	private int parseContext(BufferedString context, int def) {
		UrArsDebugger.trace("AI priority context = '{}'", context);
		if (context == null || context.isBlank()) return def;
		try {
			return context.toInteger();
		} catch (NumberFormatException e) {
			return def;
		}
	}
}
