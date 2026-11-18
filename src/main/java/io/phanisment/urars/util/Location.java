package io.phanisment.urars.util;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import org.jspecify.annotations.NonNull;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("resources")
public record Location(@NonNull Level level, double x, double y, double z, float x_rot, float y_rot) {
	public Location(@NonNull Level level, double x, double y, double z) {
		this(level, x, y, z, 0f, 0f);
	}

	public Location(@NonNull Level level, @NonNull Vec3 pos) {
		this(level, pos, 0f, 0f);
	}

	public Location(@NonNull Level level, @NonNull Vec3 pos, float x_rot, float y_rot) {
		this(level, pos.x, pos.y, pos.z, x_rot, y_rot);
	}

	public ServerLevel serverLevel() {
		return (ServerLevel)level;
	}
	
	public Vec3 pos() {
		return new Vec3(this.x, this.y, this.z);
	}

	public Vec2 rot() {
		return new Vec2(x_rot, y_rot);
	}

	/**
	 * 0 is x_rot
	 * 1 is y_rot
	 *
	 * @return float array with length 2
	 */
	public float[] rotArray() {
		return new float[]{x_rot, y_rot};
	}

	/**
	 * 0 is x
	 * 1 is y
	 * 2 is z
	 *
	 * @return double array with length 3
	 */
	public  double[] posArray() {
		return new double[]{x,y,z};
	}
}