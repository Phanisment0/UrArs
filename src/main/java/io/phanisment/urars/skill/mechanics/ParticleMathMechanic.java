package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.parser.math.eval.EvalContext;
import io.phanisment.urars.parser.math.value.NumberValue;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.template.particle.AbstractShapeMechanic;
import io.phanisment.urars.util.ShapeMath;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static io.phanisment.urars.util.ActionUtils.*;

@RegistryInfo(
		author = "Phanisment",
		key = "particleMath",
		namespace = "fx"
)
public class ParticleMathMechanic extends AbstractShapeMechanic {
	protected final Node fx;
	protected final Node fy;
	protected final Node fz;
	protected final Node points;
	protected final Node v_points;
	protected final Node closed;
	protected final Node bake;

	private volatile List<Vec3> baked;

	public ParticleMathMechanic(SkillLineConfig config) {
		super(config);
		this.fx       = config.getMathNumber(aliases("x"), 0);
		this.fy       = config.getMathNumber(aliases("y"), 0);
		this.fz       = config.getMathNumber(aliases("z"), 0);
		this.points   = config.getMathNumber(aliases("points"), 32);
		this.v_points = config.getMathNumber(aliases("vPoints", "vp"), 0);
		this.closed   = config.getMathBoolean(aliases("closed", "loop"), false);
		this.bake     = config.getMathBoolean(aliases("bake"), false);
	}

	@Override
	protected List<Vec3> shape(ParticleCastData cast) {
		EvalContext eval = cast.eval();
		boolean reuse = bake.evalAsBoolean(eval);
		if (reuse && baked != null) return baked;

		List<Vec3> result = compute(eval);
		if (reuse) baked = result;
		return result;
	}

	private List<Vec3> compute(EvalContext base) {
		EvalContext local = base.copy();

		int n = limit(points.evalAsInt(base));
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