package io.phanisment.urars.skill.target;

import net.minecraft.entity.Entity;
import io.phanisment.urars.skill.SkillContext;

/**
 * Implements with class {@link io.phanisment.urars.skill.SkillMechanic} to set mechanic target type.
 */
public interface IEntityTarget {
	public void castAtEntity(SkillContext ctx, Entity target);
}