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
		key = "particleFibonacci",
		namespace = "fx"
)
public class ParticleFibonacciMechanic extends AbstractShapeMechanic {
	private static final double GROWTH = Math.log((1 + Math.sqrt(5)) / 2) / (Math.PI / 2);

	protected final Node radius; // radius at the outer end
	protected final Node turns;
	protected final Node points;

	public ParticleFibonacciMechanic(SkillLineConfig config) {
		super(config);
		this.radius = config.getMathNumber(aliases("radius", "rad"), 2);
		this.turns  = config.getMathNumber(aliases("turns"), 3);
		this.points = config.getMathNumber(aliases("points"), 120);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double r = radius.evalAsDouble(eval), tn = turns.evalAsDouble(eval);
		int n = limit(points.evalAsInt(eval));
		double total = tn * Math.PI * 2;

		return baked("fib:" + r + ":" + tn + ":" + n, () -> ShapeMath.shape(n, t -> {
			double a = t * total;
			double d = r * Math.exp(GROWTH * (a - total));
			return new Vec3(Math.cos(a) * d, 0, Math.sin(a) * d);
		}));
	}
}