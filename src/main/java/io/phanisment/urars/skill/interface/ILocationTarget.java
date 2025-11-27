package io.phanisment.urars.skill.target;

import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import io.phanisment.urars.skill.SkillContext;

/**
 * Implements with class {@link io.phanisment.urars.skill.SkillMechanic} to set mechanic target type.
 */
public interface ILocationTarget {
	public void castAtLocation(SkillContext ctx, World world, Vec3d pos);
}