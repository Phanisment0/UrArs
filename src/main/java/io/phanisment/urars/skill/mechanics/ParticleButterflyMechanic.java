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
		key = "particleButterfly",
		namespace = "fx"
)
public class ParticleButterflyMechanic extends AbstractShapeMechanic {
	protected final Node radius; // approximate half-height
	protected final Node points;

	public ParticleButterflyMechanic(SkillLineConfig config) {
		super(config);
		this.radius = config.getMathNumber(aliases("radius", "rad"), 1);
		this.points = config.getMathNumber(aliases("points"), 240);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		int n = limit(points.evalAsInt(eval));
		double scale = radius.evalAsDouble(eval) / 4.0;

		return baked("butterfly:" + n + ":" + scale, () -> ShapeMath.loop(n, t -> {
			double a = t * Math.PI * 12;
			double r = Math.exp(Math.sin(a)) - 2 * Math.cos(4 * a) + Math.pow(Math.sin((2 * a - Math.PI) / 24), 5);
			return new Vec3(Math.sin(a) * r * scale, Math.cos(a) * r * scale, 0);
		}));
	}
}