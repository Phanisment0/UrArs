package io.phanisment.urars.skill.target;

import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import io.phanisment.urars.skill.SkillContext;

public interface ILocationTarget {
	public void castAtLocation(SkillContext ctx, World world, Vec3d pos);
}