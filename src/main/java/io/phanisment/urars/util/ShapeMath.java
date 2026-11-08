package io.phanisment.urars.util;

import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class ShapeMath {

	private ShapeMath() {
	}

	@FunctionalInterface
	public interface ShapeFunction {
		Vec3 at(double t);
	}

	@FunctionalInterface
	public interface SurfaceFunction {
		Vec3 at(double u, double v);
	}

	@FunctionalInterface
	public interface PointMapper {
		Vec3 map(double x, double y, double z);
	}

	public static List<Vec3> shape(int points, ShapeFunction fn) {
		List<Vec3> out = new ArrayList<>(points);
		for (int i = 0; i < points; i++) {
			double t = points == 1 ? 0 : (double) i / (points - 1);
			out.add(fn.at(t));
		}
		return Collections.unmodifiableList(out);
	}

	public static List<Vec3> loop(int points, ShapeFunction fn) {
		List<Vec3> out = new ArrayList<>(points);
		for (int i = 0; i < points; i++) {
			out.add(fn.at((double) i / points));
		}
		return Collections.unmodifiableList(out);
	}

	public static List<Vec3> surface(int uPoints, int vPoints, SurfaceFunction fn) {
		List<Vec3> out = new ArrayList<>(uPoints * vPoints);
		for (int i = 0; i < uPoints; i++) {
			double u = (double) i / uPoints;
			for (int j = 0; j < vPoints; j++) {
				out.add(fn.at(u, (double) j / vPoints));
			}
		}
		return Collections.unmodifiableList(out);
	}

	public static List<Vec3> map(List<Vec3> shape, PointMapper mapper) {
		List<Vec3> out = new ArrayList<>(shape.size());
		for (Vec3 p : shape) out.add(mapper.map(p.x, p.y, p.z));
		return Collections.unmodifiableList(out);
	}

	public static List<Vec3> transform(List<Vec3> shape, Matrix4dc matrix) {
		List<Vec3> out = new ArrayList<>(shape.size());
		Vector3d tmp = new Vector3d();
		for (Vec3 p : shape) {
			matrix.transformPosition(tmp.set(p.x, p.y, p.z));
			out.add(new Vec3(tmp.x, tmp.y, tmp.z));
		}
		return Collections.unmodifiableList(out);
	}

	public static List<Vec3> rotate(List<Vec3> shape, Quaterniondc rotation) {
		return transform(shape, new Matrix4d().rotation(rotation));
	}

	public static List<Vec3> rotateX(List<Vec3> s, double radians) {
		return transform(s, new Matrix4d().rotateX(radians));
	}

	public static List<Vec3> rotateY(List<Vec3> s, double radians) {
		return transform(s, new Matrix4d().rotateY(radians));
	}

	public static List<Vec3> rotateZ(List<Vec3> s, double radians) {
		return transform(s, new Matrix4d().rotateZ(radians));
	}

	public static List<Vec3> scale(List<Vec3> s, double factor) {
		return transform(s, new Matrix4d().scale(factor));
	}

	public static List<Vec3> translate(List<Vec3> s, double dx, double dy, double dz) {
		return transform(s, new Matrix4d().translation(dx, dy, dz));
	}

	public static List<Vec3> translate(List<Vec3> s, Vec3 offset) {
		return translate(s, offset.x, offset.y, offset.z);
	}

	public static List<Vec3> orient(List<Vec3> shape, Vec3 direction) {
		Vector3d dir = new Vector3d(direction.x, direction.y, direction.z);
		if (dir.lengthSquared() == 0) return shape;
		Quaterniond q = new Quaterniond().rotationTo(new Vector3d(0, 1, 0), dir.normalize());
		return rotate(shape, q);
	}

	@SafeVarargs
	public static List<Vec3> combine(List<Vec3>... shapes) {
		List<Vec3> out = new ArrayList<>();
		for (List<Vec3> s : shapes) out.addAll(s);
		return Collections.unmodifiableList(out);
	}

	private static final Map<String, List<Vec3>> CACHE = new ConcurrentHashMap<>();

	public static List<Vec3> cached(String key, Supplier<List<Vec3>> bake) {
		return CACHE.computeIfAbsent(key, k -> bake.get());
	}

	public static void clearCache() {
		CACHE.clear();
	}

	public static void save(Path file, List<Vec3> shape) throws IOException {
		try (DataOutputStream out = new DataOutputStream(
				new BufferedOutputStream(Files.newOutputStream(file)))) {
			out.writeInt(shape.size());
			for (Vec3 p : shape) {
				out.writeDouble(p.x);
				out.writeDouble(p.y);
				out.writeDouble(p.z);
			}
		}
	}

	public static List<Vec3> load(Path file) throws IOException {
		try (DataInputStream in = new DataInputStream(
				new BufferedInputStream(Files.newInputStream(file)))) {
			int n = in.readInt();
			List<Vec3> out = new ArrayList<>(n);
			for (int i = 0; i < n; i++) {
				out.add(new Vec3(in.readDouble(), in.readDouble(), in.readDouble()));
			}
			return Collections.unmodifiableList(out);
		}
	}
}