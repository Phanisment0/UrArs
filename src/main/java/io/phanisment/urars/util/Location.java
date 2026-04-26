package io.phanisment.urars.util;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record Location(Level level, Vec3 pos) {
}