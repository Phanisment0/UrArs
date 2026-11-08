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
		key = "particleRing"
)
public class ParticleRingMechanic extends AbstractShapeMechanic {
	protected final Node radius;
	protected final Node points;

	public ParticleRingMechanic(SkillLineConfig config) {
		super(config);
		this.radius = config.getMathNumber(aliases("radius", "rad"), 1);
		this.points = config.getMathNumber(aliases("points"), 32);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		int n = limit(points.evalAsInt(eval));
		double r = radius.evalAsDouble(eval);

		return baked("ring:" + n + ":" + r, () -> ShapeMath.loop(n, t -> {
			double a = t * Math.PI * 2;
			return new Vec3(Math.cos(a) * r, 0, Math.sin(a) * r);
		}));
	}
}