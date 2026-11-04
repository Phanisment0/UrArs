package io.phanisment.urars.skill;

import net.minecraft.world.entity.Entity;

import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.Location;

import java.util.Collection;
import java.util.List;

public abstract class SkillTargeter implements ISkillAction {
	protected final SkillLineConfig config;
	protected List<SkillCondition> target_conditions;

	public SkillTargeter(SkillLineConfig config) {
		this.config = config;
		this.target_conditions = config.getConditions("targetConditions", "conditions");
	}

	public boolean conditionIsPass(SkillContext ctx) {
		return target_conditions.stream().allMatch(c -> c.execute(ctx));
	}

	/** True if this targeter natively produces locations (not entities). */
	public abstract boolean isLocation();

	/** Entities to target. Location targeters derive these from a radius query. */
	public abstract Collection<Entity> getEntities(SkillContext ctx);

	/** Locations to target. Entity targeters derive these from entity positions. */
	public abstract Collection<Location> getLocations(SkillContext ctx);
}