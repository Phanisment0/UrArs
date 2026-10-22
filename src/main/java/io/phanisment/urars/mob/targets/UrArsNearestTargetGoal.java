package io.phanisment.urars.mob.targets;

import io.phanisment.urars.mob.IGoalCondition;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.EntityTypeUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.function.Predicate;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		namespace = "minecraft",
		key = "nearestTarget",
		aliases = {"nearest"}
)
public class UrArsNearestTargetGoal extends NearestAttackableTargetGoal<LivingEntity> implements IGoalCondition {
	public UrArsNearestTargetGoal(Mob mob, SkillLineConfig c) {
		super(mob, LivingEntity.class,
				c.getInt(aliases("interval", "i"), 10),
				c.getBoolean(aliases("mustSee", "see"), true),
				c.getBoolean(aliases("mustReach", "reach"), false),
				filter(c));
	}

	private static TargetingConditions.Selector filter(SkillLineConfig c) {
		Predicate<Entity> matcher = EntityTypeUtils.matcher(c.getString(aliases("target", "t"), "player"));
		return (entity, level) -> matcher.test(entity);
	}
}