package io.phanisment.urars.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Oriented bounding box. Immutable: every transform returns a new instance.
 * Rotations use Minecraft's convention (yaw/pitch in degrees, same as entities).
 */
public final class OBB {
	private static final double EPSILON = 1.0E-6;

	private final Vec3 center;
	private final Vec3 half;                      // half size along local axes
	private final Vec3 axisX, axisY, axisZ;       // orthonormal basis (world space)

	private OBB(Vec3 center, Vec3 half, Vec3 axisX, Vec3 axisY, Vec3 axisZ) {
		this.center = center;
		this.half = half;
		this.axisX = axisX;
		this.axisY = axisY;
		this.axisZ = axisZ;
	}

	// ------------------------------------------------------------------ factory

	public static OBB of(AABB box) {
		return new OBB(
				box.getCenter(),
				new Vec3(box.getXsize() / 2, box.getYsize() / 2, box.getZsize() / 2),
				new Vec3(1, 0, 0), new Vec3(0, 1, 0), new Vec3(0, 0, 1)
		);
	}

	/** Box rotated around its own center. */
	public static OBB of(AABB box, float yaw, float pitch, float roll) {
		return of(box).rotate(yaw, pitch, roll);
	}

	/** Box rotated around a custom pivot (world space). */
	public static OBB of(AABB box, Vec3 pivot, float yaw, float pitch, float roll) {
		return of(box).rotate(pivot, yaw, pitch, roll);
	}

	/** Box from a center and full size (not half size). */
	public static OBB of(Vec3 center, double sizeX, double sizeY, double sizeZ) {
		return new OBB(center, new Vec3(sizeX / 2, sizeY / 2, sizeZ / 2),
				new Vec3(1, 0, 0), new Vec3(0, 1, 0), new Vec3(0, 0, 1));
	}

	/** Box from two opposite corners (world space, any order). */
	public static OBB of(Vec3 cornerA, Vec3 cornerB) {
		Vec3 center = cornerA.add(cornerB).scale(0.5);
		Vec3 half = new Vec3(
				Math.abs(cornerB.x - cornerA.x) / 2,
				Math.abs(cornerB.y - cornerA.y) / 2,
				Math.abs(cornerB.z - cornerA.z) / 2
		);
		return new OBB(center, half, new Vec3(1, 0, 0), new Vec3(0, 1, 0), new Vec3(0, 0, 1));
	}

	/** Two world-space corners, rotated around the box's own center. */
	public static OBB of(Vec3 cornerA, Vec3 cornerB, float yaw, float pitch, float roll) {
		return of(cornerA, cornerB).rotate(yaw, pitch, roll);
	}

	/** Two world-space corners, rotated around a world-space pivot. */
	public static OBB of(Vec3 cornerA, Vec3 cornerB, Vec3 pivot, float yaw, float pitch, float roll) {
		return of(cornerA, cornerB).rotate(pivot, yaw, pitch, roll);
	}

	/**
	 * Two corners given as offsets from the pivot (local, before rotation).
	 * Handy for skills: the pivot is the caster, the corners describe the hitbox
	 * relative to them, and the whole box swings with yaw/pitch.
	 */
	public static OBB ofRelative(Vec3 pivot, Vec3 cornerA, Vec3 cornerB, float yaw, float pitch, float roll) {
		return of(pivot.add(cornerA), pivot.add(cornerB)).rotate(pivot, yaw, pitch, roll);
	}

	// --------------------------------------------------------------- transforms

	/** Rotate around the box's own center. */
	public OBB rotate(float yaw, float pitch, float roll) {
		return rotate(center, yaw, pitch, roll);
	}

	/**
	 * Rotate around a pivot in world space. Both the center and the axes rotate,
	 * so the box orbits the pivot. Order: roll, then pitch, then yaw.
	 */
	public OBB rotate(Vec3 pivot, float yaw, float pitch, float roll) {
		Vec3 newCenter = pivot.add(rotateVec(center.subtract(pivot), yaw, pitch, roll));
		return new OBB(
				newCenter, half,
				rotateVec(axisX, yaw, pitch, roll),
				rotateVec(axisY, yaw, pitch, roll),
				rotateVec(axisZ, yaw, pitch, roll)
		);
	}

	public OBB translate(Vec3 offset) {
		return new OBB(center.add(offset), half, axisX, axisY, axisZ);
	}

	public OBB translate(double x, double y, double z) {
		return translate(new Vec3(x, y, z));
	}

	/** Grow the box by `amount` on every side (in local space). */
	public OBB inflate(double amount) {
		return new OBB(center, half.add(amount, amount, amount), axisX, axisY, axisZ);
	}

	private static Vec3 rotateVec(Vec3 v, float yaw, float pitch, float roll) {
		return v.zRot((float) Math.toRadians(-roll))
				.xRot((float) Math.toRadians(-pitch))
				.yRot((float) Math.toRadians(-yaw));
	}

	// -------------------------------------------------------------------- space

	public Vec3 toLocal(Vec3 world) {
		Vec3 d = world.subtract(center);
		return new Vec3(d.dot(axisX), d.dot(axisY), d.dot(axisZ));
	}

	public Vec3 toWorld(Vec3 local) {
		return center
				.add(axisX.scale(local.x))
				.add(axisY.scale(local.y))
				.add(axisZ.scale(local.z));
	}

	// ------------------------------------------------------------------- queries

	public boolean contains(Vec3 point) {
		Vec3 l = toLocal(point);
		return Math.abs(l.x) <= half.x && Math.abs(l.y) <= half.y && Math.abs(l.z) <= half.z;
	}

	public Vec3 closestPoint(Vec3 point) {
		Vec3 l = toLocal(point);
		return toWorld(new Vec3(
				clamp(l.x, -half.x, half.x),
				clamp(l.y, -half.y, half.y),
				clamp(l.z, -half.z, half.z)
		));
	}

	public double distanceTo(Vec3 point) {
		return closestPoint(point).distanceTo(point);
	}

	public boolean intersects(AABB box) {
		return intersects(of(box));
	}

	public boolean intersects(Entity entity) {
		return intersects(entity.getBoundingBox());
	}

	/** Separating Axis Theorem: 3 + 3 face axes and 9 edge cross-product axes. */
	public boolean intersects(OBB other) {
		Vec3[] a = {axisX, axisY, axisZ};
		Vec3[] b = {other.axisX, other.axisY, other.axisZ};
		Vec3 t = other.center.subtract(center);

		for (Vec3 axis : a) if (separated(axis, t, other, a, b)) return false;
		for (Vec3 axis : b) if (separated(axis, t, other, a, b)) return false;

		for (Vec3 u : a) {
			for (Vec3 v : b) {
				Vec3 axis = u.cross(v);
				if (axis.lengthSqr() < EPSILON) continue; // parallel edges, skip
				if (separated(axis.normalize(), t, other, a, b)) return false;
			}
		}
		return true;
	}

	private boolean separated(Vec3 axis, Vec3 t, OBB other, Vec3[] a, Vec3[] b) {
		double ra = half.x * Math.abs(a[0].dot(axis))
				+ half.y * Math.abs(a[1].dot(axis))
				+ half.z * Math.abs(a[2].dot(axis));
		double rb = other.half.x * Math.abs(b[0].dot(axis))
				+ other.half.y * Math.abs(b[1].dot(axis))
				+ other.half.z * Math.abs(b[2].dot(axis));
		return Math.abs(t.dot(axis)) > ra + rb;
	}

	/** Raycast against the box. Returns the world-space hit point of the segment from -> to. */
	public Optional<Vec3> clip(Vec3 from, Vec3 to) {
		AABB local = new AABB(-half.x, -half.y, -half.z, half.x, half.y, half.z);
		return local.clip(toLocal(from), toLocal(to)).map(this::toWorld);
	}

	// ------------------------------------------------------------------ entities

	/** World-space AABB that fully encloses this box (broad phase). */
	public AABB getBounds() {
		double ex = Math.abs(axisX.x) * half.x + Math.abs(axisY.x) * half.y + Math.abs(axisZ.x) * half.z;
		double ey = Math.abs(axisX.y) * half.x + Math.abs(axisY.y) * half.y + Math.abs(axisZ.y) * half.z;
		double ez = Math.abs(axisX.z) * half.x + Math.abs(axisY.z) * half.y + Math.abs(axisZ.z) * half.z;
		return new AABB(center.x - ex, center.y - ey, center.z - ez, center.x + ex, center.y + ey, center.z + ez);
	}

	/** Entities whose hitbox really touches the rotated box. */
	public List<Entity> getEntities(Level level, Predicate<Entity> filter) {
		return level.getEntities((Entity) null, getBounds(), e -> filter.test(e) && intersects(e));
	}

	public List<Entity> getEntities(Level level) {
		return getEntities(level, e -> true);
	}

	// ------------------------------------------------------------------- getters

	public Vec3 getCenter() {
		return center;
	}

	public Vec3 getHalfSize() {
		return half;
	}

	public Vec3 getAxisX() {
		return axisX;
	}

	public Vec3 getAxisY() {
		return axisY;
	}

	public Vec3 getAxisZ() {
		return axisZ;
	}

	/** The 8 corners, handy for debug particles. */
	public Vec3[] getCorners() {
		Vec3[] out = new Vec3[8];
		int i = 0;
		for (int x = -1; x <= 1; x += 2)
			for (int y = -1; y <= 1; y += 2)
				for (int z = -1; z <= 1; z += 2)
					out[i++] = toWorld(new Vec3(x * half.x, y * half.y, z * half.z));
		return out;
	}

	private static double clamp(double v, double min, double max) {
		return Math.max(min, Math.min(max, v));
	}
}