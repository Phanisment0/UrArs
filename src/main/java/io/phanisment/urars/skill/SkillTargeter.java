package io.phanisment.urars.skill;

import net.minecraft.world.entity.Entity;

import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.Location;

import java.util.Collection;

// TODO: Add argument target condition, need some change in SkillMechanic.
public abstract class SkillTargeter implements ISkillAction {
	protected final SkillLineConfig config;

	public SkillTargeter(SkillLineConfig config) {
		this.config = config;
	}

	/** True if this targeter natively produces locations (not entities). */
	public abstract boolean isLocation();

	/** Entities to target. Location targeters derive these from a radius query. */
	public abstract Collection<Entity> getEntities(SkillContext ctx);

	/** Locations to target. Entity targeters derive these from entity positions. */
	public abstract Collection<Location> getLocations(SkillContext ctx);
}