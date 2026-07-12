package io.phanisment.urars.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public record Location(ServerLevel level, Vec3 pos) {
}