package io.phanisment.urars.util;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public record Location(World world, Vec3d pos) {
}