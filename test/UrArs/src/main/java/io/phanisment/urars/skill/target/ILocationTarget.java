package io.phanisment.urars.skill.target;

import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.util.Location;

/**
 * Implements with class {@link io.phanisment.urars.skill.SkillMechanic} to set mechanic target type.
 */
public interface ILocationTarget {
	public void castAtLocation(SkillContext ctx, Location location);
}