package io.phanisment.urars.skill.template.particle;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.Location;
import io.phanisment.urars.util.ShapeMath;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.List;
import java.util.function.Supplier;

import static io.phanisment.urars.util.ActionUtils.*;

/**
 * Base class for shape mechanics (sphere, ring, box, ...).
 *
 * Adds on top of AbstractParticleMechanic:
 *   zoom             uniform scale
 *   rotX/rotY/rotZ   rotation in degrees
 *   orient           true: the shape's +Y axis follows the direction of the target Location
 *
 * A subclass only implements {@link #shape(ParticleCastData)} and returns the points
 * centered on (0,0,0). Moving, rotating and spawning is handled here.
 */
public abstract class AbstractShapeMechanic extends AbstractParticleMechanic {
	protected static final int MAX_POINTS = 4096;
	protected static final double GOLDEN_ANGLE = Math.PI * (3 - Math.sqrt(5));
	private static final Vector3d UP = new Vector3d(0, 1, 0);

	protected final Node zoom;
	protected final Node rot_x;
	protected final Node rot_y;
	protected final Node rot_z;
	protected final Node orient;

	private record Memo(String key, List<Vec3> shape) {}
	private volatile Memo memo;

	protected AbstractShapeMechanic(SkillLineConfig config) {
		super(config);
		this.zoom   = config.getMathNumber(aliases("zoom"), 1);
		this.rot_x  = config.getMathNumber(aliases("rotX"), 0);
		this.rot_y  = config.getMathNumber(aliases("rotY"), 0);
		this.rot_z  = config.getMathNumber(aliases("rotZ"), 0);
		this.orient = config.getMathBoolean(aliases("orient"), false);
	}

	protected abstract List<Vec3> shape(ParticleCastData cast);

	@Override
	protected final void emit(ParticleCastData cast) {
		var eval = cast.eval();

		List<Vec3> base = shape(cast);
		if (base == null || base.isEmpty()) return;

		Location at = cast.origin();
		Matrix4d m = new Matrix4d().translation(at.x(), at.y(), at.z());

		if (orient.evalAsBoolean(eval)) m.rotate(new Quaterniond().rotationTo(UP, direction(at)));

		m.rotateXYZ(Math.toRadians(rot_x.evalAsDouble(eval)), Math.toRadians(rot_y.evalAsDouble(eval)), Math.toRadians(rot_z.evalAsDouble(eval)));
		m.scale(zoom.evalAsDouble(eval));

		cast.spawn(ShapeMath.transform(base, m));
	}

	private static Vector3d direction(Location at) {
		double yaw = Math.toRadians(at.y_rot());
		double pitch = Math.toRadians(at.x_rot());
		double h = Math.cos(pitch);
		return new Vector3d(-Math.sin(yaw) * h, -Math.sin(pitch), Math.cos(yaw) * h);
	}

	protected List<Vec3> baked(String key, Supplier<List<Vec3>> bake) {
		Memo m = this.memo;
		if (m != null && m.key().equals(key)) return m.shape();
		List<Vec3> shape = bake.get();
		this.memo = new Memo(key, shape);
		return shape;
	}

	protected static int limit(int points) {
		return Math.clamp(points, 1, MAX_POINTS);
	}

	protected static List<Vec3> edge(Vec3 a, Vec3 b, int points) {
		return ShapeMath.shape(points, t -> new Vec3(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t, a.z + (b.z - a.z) * t));
	}
}