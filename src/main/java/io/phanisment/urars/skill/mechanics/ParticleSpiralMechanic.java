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
		key = "particleSpiral",
		namespace = "fx"
)
public class ParticleSpiralMechanic extends AbstractShapeMechanic {
	protected final Node radius; // final radius
	protected final Node turns;
	protected final Node points;

	public ParticleSpiralMechanic(SkillLineConfig config) {
		super(config);
		this.radius = config.getMathNumber(aliases("radius", "rad"), 2);
		this.turns  = config.getMathNumber(aliases("turns"), 3);
		this.points = config.getMathNumber(aliases("points"), 64);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		int n = limit(points.evalAsInt(eval));
		double r = radius.evalAsDouble(eval), tn = turns.evalAsDouble(eval);

		return baked("spiral:" + n + ":" + r + ":" + tn, () -> ShapeMath.shape(n, t -> {
			double a = t * tn * Math.PI * 2;
			return new Vec3(Math.cos(a) * r * t, 0, Math.sin(a) * r * t);
		}));
	}
}