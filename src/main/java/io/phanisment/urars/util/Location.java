package io.phanisment.urars.util;

import org.jspecify.annotations.NonNull;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public record Location(@NonNull ServerLevel level, @NonNull Vec3 pos) {
}