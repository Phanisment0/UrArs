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
		key = "particleDome",
		namespace = "fx"
)
public class ParticleDomeMechanic extends AbstractShapeMechanic {
	protected final Node radius;
	protected final Node points;

	public ParticleDomeMechanic(SkillLineConfig config) {
		super(config);
		this.radius = config.getMathNumber(aliases("radius", "rad"), 1);
		this.points = config.getMathNumber(aliases("points"), 64);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		int n = limit(points.evalAsInt(eval));
		double r = radius.evalAsDouble(eval);

		return baked("dome:" + n + ":" + r, () -> ShapeMath.shape(n, t -> {
			double y = 1 - t; // 1 (top) .. 0 (rim)
			double ring = Math.sqrt(Math.max(0, 1 - y * y));
			double a = GOLDEN_ANGLE * t * (n - 1);
			return new Vec3(Math.cos(a) * ring * r, y * r, Math.sin(a) * ring * r);
		}));
	}
}