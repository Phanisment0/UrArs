package io.phanisment.urars.mob.targets;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.EntityTypeUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		key = "defend"
)
public class DefendTargetGoal extends TargetGoal {
	private final Predicate<Entity> protect;
	private final Predicate<Entity> attack;
	private final TargetingConditions protect_conditions;
	private final TargetingConditions attack_conditions;
	private final double radius_xz;
	private final double radius_y;
	private final int reputation;
	private final Mode mode;

	@Nullable
	private LivingEntity potential;

	public DefendTargetGoal(Mob mob, SkillLineConfig config) {
		super(mob, config.getBoolean(aliases("mustsee"), false), config.getBoolean(aliases("mustreach", "reach"), true));

		this.protect = EntityTypeUtils.matcher(config.getString(aliases("protect", "p"), "villager"));
		this.attack = EntityTypeUtils.matcher(config.getString(aliases("target", "t"), "player"));
		this.mode = parseMode(config.getString(aliases("mode", "m"), "reputation"));
		this.reputation = config.getInt(aliases("reputation", "rep"), -100);
		this.radius_xz = config.getDouble(aliases("radius", "r"), 10.0);
		this.radius_y = config.getDouble(aliases("radiusy", "ry"), 8.0);

		double range = config.getDouble(aliases("range"), 64.0);
		this.protect_conditions = TargetingConditions.forNonCombat().range(range);
		this.attack_conditions = TargetingConditions.forCombat().range(range);

		setFlags(EnumSet.of(Flag.TARGET));
	}

	private static Mode parseMode(String raw) {
		try {
			return Mode.valueOf(raw.strip().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Unknown defend mode '" + raw + "' (reputation, hurt_by, any)");
		}
	}

	@Override
	public boolean canUse() {
		this.potential = null; // never reuse a stale target

		ServerLevel level = getServerLevel(this.mob);
		AABB box = this.mob.getBoundingBox().inflate(radius_xz, radius_y, radius_xz);

		List<LivingEntity> protectees = new ArrayList<>();
		for (LivingEntity e : level.getNearbyEntities(LivingEntity.class, protect_conditions, this.mob, box)) {
			if (e != this.mob && protect.test(e)) protectees.add(e);
		}
		if (protectees.isEmpty()) return false;

		double best = Double.MAX_VALUE;
		for (LivingEntity candidate : level.getNearbyEntities(LivingEntity.class, attack_conditions, this.mob, box)) {
			if (candidate == this.mob || !attack.test(candidate)) continue;
			if (candidate instanceof Player player && (player.isSpectator() || player.isCreative())) continue;

			for (LivingEntity protectee : protectees) {
				if (protectee == candidate || !isHostile(protectee, candidate)) continue;
				double dist = this.mob.distanceToSqr(candidate);
				if (dist < best) {
					best = dist;
					this.potential = candidate;
				}
				break;
			}
		}
		return this.potential != null;
	}

	private boolean isHostile(LivingEntity protectee, LivingEntity candidate) {
		return switch (mode) {
			case REPUTATION -> protectee instanceof Villager villager
					&& candidate instanceof Player player
					&& villager.getPlayerReputation(player) <= reputation;
			case HURT_BY -> protectee.getLastHurtByMob() == candidate;
			case ANY -> true;
		};
	}

	@Override
	public void start() {
		this.mob.setTarget(this.potential);
		super.start();
	}

	public enum Mode {
		/** Villager-style: attack players whose reputation with the protected villager is too low. */
		REPUTATION,
		/** Attack whoever last hurt a protected entity. */
		HURT_BY,
		/** Attack any matching entity that is near a protected entity. */
		ANY
	}
}