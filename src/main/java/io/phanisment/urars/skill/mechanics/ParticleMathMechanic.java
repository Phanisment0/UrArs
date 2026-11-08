package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.parser.math.eval.EvalContext;
import io.phanisment.urars.parser.math.value.NumberValue;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.template.particle.AbstractParticleMechanic;
import io.phanisment.urars.util.ShapeMath;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.List;

import static io.phanisment.urars.util.ActionUtils.*;

@RegistryInfo(
		author = "Phanisment",
		key = "particleMath",
		namespace = "fx"
)
public class ParticleMathMechanic extends AbstractParticleMechanic {
	private static final int MAX_POINTS = 4096;

	protected final Node fx;
	protected final Node fy;
	protected final Node fz;
	protected final Node points;
	protected final Node v_points;
	protected final Node closed;
	protected final Node bake;
	protected final Node zoom;
	protected final Node rot_x;
	protected final Node rot_y;
	protected final Node rot_z;
	protected final Node orient;

	private volatile List<Vec3> baked;

	public ParticleMathMechanic(SkillLineConfig config) {
		super(config);
		this.fx      = config.getMathNumber(aliases("x"), 0);
		this.fy      = config.getMathNumber(aliases("y"), 0);
		this.fz      = config.getMathNumber(aliases("z"), 0);
		this.points  = config.getMathNumber(aliases("points"), 32);
		this.v_points = config.getMathNumber(aliases("vPoints", "vp"), 0);
		this.closed  = config.getMathBoolean(aliases("closed", "loop"), false);
		this.bake    = config.getMathBoolean(aliases("bake"), false);
		this.zoom    = config.getMathNumber(aliases("zoom"), 1);
		this.rot_x   = config.getMathNumber(aliases("rotX"), 0);
		this.rot_y   = config.getMathNumber(aliases("rotY"), 0);
		this.rot_z   = config.getMathNumber(aliases("rotZ"), 0);
		this.orient  = config.getMathBoolean(aliases("orient"), false);
	}

	@Override
	protected void emit(Cast cast) {
		EvalContext eval = cast.eval();

		List<Vec3> base = baseShape(eval);
		if (base.isEmpty()) return;

		Vec3 o = cast.origin();
		Matrix4d m = new Matrix4d().translation(o.x, o.y, o.z);

		Entity caster = cast.caster();
		if (caster != null && orient.evalAsBoolean(eval)) {
			Vec3 look = caster.getLookAngle();
			Vector3d dir = new Vector3d(look.x, look.y, look.z);
			if (dir.lengthSquared() > 1.0E-8) m.rotate(new Quaterniond().rotationTo(new Vector3d(0, 1, 0), dir.normalize()));
		}
		m.rotateXYZ(Math.toRadians(rot_x.evalAsDouble(eval)), Math.toRadians(rot_y.evalAsDouble(eval)), Math.toRadians(rot_z.evalAsDouble(eval)));
		m.scale(zoom.evalAsDouble(eval));

		cast.spawn(ShapeMath.transform(base, m));
	}

	private List<Vec3> baseShape(EvalContext eval) {
		boolean reuse = bake.evalAsBoolean(eval);
		if (reuse) {
			List<Vec3> cached = this.baked;
			if (cached != null) return cached;
		}
		List<Vec3> result = compute(eval);
		if (reuse) this.baked = result;
		return result;
	}

	private List<Vec3> compute(EvalContext base) {
		EvalContext local = base.copy(); // keeps t, i, u, v out of the skill's own variables
		local.put("pi", num(Math.PI));
		local.put("tau", num(Math.PI * 2));

		int n = Math.clamp(points.evalAsInt(base), 1, MAX_POINTS);
		int vn = v_points.evalAsInt(base);
		local.put("n", num(n));

		if (vn > 1) {
			vn = Math.clamp(MAX_POINTS / n, 1, vn);
			return ShapeMath.surface(n, vn, (u, v) -> {
				local.put("u", num(u));
				local.put("v", num(v));
				return point(local);
			});
		}

		int[] index = {0};
		ShapeMath.ShapeFunction fn = t -> {
			local.put("t", num(t));
			local.put("i", num(index[0]++));
			return point(local);
		};
		return closed.evalAsBoolean(base) ? ShapeMath.loop(n, fn) : ShapeMath.shape(n, fn);
	}

	private Vec3 point(EvalContext eval) {
		return new Vec3(fx.evalAsDouble(eval), fy.evalAsDouble(eval), fz.evalAsDouble(eval));
	}

	private static NumberValue num(double value) {
		return new NumberValue(value);
	}
}