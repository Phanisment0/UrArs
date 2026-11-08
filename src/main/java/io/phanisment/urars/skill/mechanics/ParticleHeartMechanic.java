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
		key = "particleHeart",
		namespace = "fx"
)
public class ParticleHeartMechanic extends AbstractShapeMechanic {
	protected final Node radius;
	protected final Node points;

	public ParticleHeartMechanic(SkillLineConfig config) {
		super(config);
		this.radius = config.getMathNumber(aliases("radius", "rad"), 1);
		this.points = config.getMathNumber(aliases("points"), 64);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		int n = limit(points.evalAsInt(eval));
		double r = radius.evalAsDouble(eval);

		return baked("heart:" + n + ":" + r, () -> ShapeMath.loop(n, t -> {
			double a = t * Math.PI * 2;
			double x = 16 * Math.pow(Math.sin(a), 3);
			double y = 13 * Math.cos(a) - 5 * Math.cos(2 * a) - 2 * Math.cos(3 * a) - Math.cos(4 * a);
			return new Vec3(x / 16 * r, y / 16 * r, 0);
		}));
	}
}