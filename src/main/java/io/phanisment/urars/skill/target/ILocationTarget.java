package io.phanisment.urars.skill.target;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import io.phanisment.urars.skill.SkillContext;

/**
 * Implements with class {@link io.phanisment.urars.skill.SkillMechanic} to set mechanic target type.
 */
public interface ILocationTarget {
	public void castAtLocation(SkillContext ctx, Level level, Vec3 pos);
}