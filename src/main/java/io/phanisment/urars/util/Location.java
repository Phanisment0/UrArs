package io.phanisment.urars.util;

import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public record Location(@NonNull Level level, double x, double y, double z) {
	public Location(@NonNull Level level, @NonNull Vec3 pos) {
		this(level, pos.x, pos.y, pos.z);
	}

	public ServerLevel serverLevel() {
		return (ServerLevel) level;
	}
	
	public Vec3 pos() {
		return new Vec3(this.x, this.y, this.z);
	}
	
	public  double[] posArray() {
		return new double[]{x,y,z};
	}
}