package io.phanisment.urars.util;

import org.jspecify.annotations.NonNull;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public record Location(@NonNull ServerLevel level, double x, double y, double z) {
	public Location(@NonNull ServerLevel level, @NonNull Vec3 pos) {
		this(level, pos.x, pos.y, pos.z);
	}
	
	public Vec3 pos() {
		return new Vec3(this.x, this.y, this.z);
	}
	
	public  double[] posArray() {
		return new double[]{x,y,z};
	}
}