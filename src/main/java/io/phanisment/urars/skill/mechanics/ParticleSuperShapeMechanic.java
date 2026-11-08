package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.template.particle.AbstractShapeMechanic;
import io.phanisment.urars.util.ShapeMath;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static io.phanisment.urars.util.ActionUtils.*;

@RegistryInfo(
		author = "Phanisment",
		key = "particleSuperShape",
		namespace = "fx"
)
public class ParticleSuperShapeMechanic extends AbstractShapeMechanic {
	protected final Node radius; // outer radius after normalising
	protected final Node m;
	protected final Node n1;
	protected final Node n2;
	protected final Node n3;
	protected final Node points;

	public ParticleSuperShapeMechanic(SkillLineConfig config) {
		super(config);
		this.radius = config.getMathNumber(aliases("radius", "rad"), 1);
		this.m      = config.getMathNumber(aliases("m"), 6);
		this.n1     = config.getMathNumber(aliases("n1"), 1);
		this.n2     = config.getMathNumber(aliases("n2"), 1);
		this.n3     = config.getMathNumber(aliases("n3"), 1);
		this.points = config.getMathNumber(aliases("points"), 200);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double r = radius.evalAsDouble(eval), mm = m.evalAsDouble(eval);
		double a1 = n1.evalAsDouble(eval) == 0 ? 1 : n1.evalAsDouble(eval);
		double a2 = n2.evalAsDouble(eval), a3 = n3.evalAsDouble(eval);
		int n = limit(points.evalAsInt(eval));

		return baked("super:" + r + ":" + mm + ":" + a1 + ":" + a2 + ":" + a3 + ":" + n, () -> {
			double max = 1.0E-9;
			for (int i = 0; i < n; i++) max = Math.max(max, f(Math.PI * 2 * i / n, mm, a1, a2, a3));
			double norm = r / max;
			return ShapeMath.loop(n, t -> {
				double phi = t * Math.PI * 2;
				double d = f(phi, mm, a1, a2, a3) * norm;
				return new Vec3(Math.cos(phi) * d, 0, Math.sin(phi) * d);
			});
		});
	}

	private static double f(double phi, double m, double n1, double n2, double n3) {
		double t1 = Math.pow(Math.abs(Math.cos(m * phi / 4)), n2);
		double t2 = Math.pow(Math.abs(Math.sin(m * phi / 4)), n3);
		double s = t1 + t2;
		return s == 0 ? 0 : Math.pow(s, -1.0 / n1);
	}
}