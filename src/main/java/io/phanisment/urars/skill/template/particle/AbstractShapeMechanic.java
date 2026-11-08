package io.phanisment.urars.skill.template.particle;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.ShapeMath;
import net.minecraft.world.entity.Entity;
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
 *   orient           true: the shape's +Y axis follows the caster's look direction
 *
 * A subclass only implements {@link #shape(Cast)} and returns the points
 * centered on (0,0,0). Moving, rotating and spawning is handled here.
 */
public abstract class AbstractShapeMechanic extends AbstractParticleMechanic {
	protected static final int MAX_POINTS = 4096;

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

	/** Return the untransformed points, centered on (0,0,0). */
	protected abstract List<Vec3> shape(Cast cast);

	@Override
	protected final void emit(Cast cast) {
		var eval = cast.eval();

		List<Vec3> base = shape(cast);
		if (base == null || base.isEmpty()) return;

		// scale -> user rotation -> orient -> translate, applied in one pass
		Vec3 o = cast.origin();
		Matrix4d m = new Matrix4d().translation(o.x, o.y, o.z);

		Entity caster = cast.caster();
		if (caster != null && orient.evalAsBoolean(eval)) {
			Vec3 look = caster.getLookAngle();
			Vector3d dir = new Vector3d(look.x, look.y, look.z);
			if (dir.lengthSquared() > 1.0E-8) {
				m.rotate(new Quaterniond().rotationTo(new Vector3d(0, 1, 0), dir.normalize()));
			}
		}
		m.rotateXYZ(
				Math.toRadians(rot_x.evalAsDouble(eval)),
				Math.toRadians(rot_y.evalAsDouble(eval)),
				Math.toRadians(rot_z.evalAsDouble(eval)));
		m.scale(zoom.evalAsDouble(eval));

		cast.spawn(ShapeMath.transform(base, m));
	}

	/**
	 * Remembers the last baked shape of this mechanic. If the key (your shape
	 * parameters as text) is unchanged the math is skipped. Only one entry is
	 * kept, so formulas with changing values can never grow memory.
	 */
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

	/** Straight run of points from a to b (inclusive). */
	protected static List<Vec3> edge(Vec3 a, Vec3 b, int points) {
		return ShapeMath.shape(points, t -> new Vec3(
				a.x + (b.x - a.x) * t,
				a.y + (b.y - a.y) * t,
				a.z + (b.z - a.z) * t));
	}
}