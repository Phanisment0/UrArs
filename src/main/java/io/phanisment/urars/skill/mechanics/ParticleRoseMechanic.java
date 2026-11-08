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
		key = "particleRose",
		namespace = "fx"
)
public class ParticleRoseMechanic extends AbstractShapeMechanic {
	protected final Node radius;
	protected final Node petals;
	protected final Node points;

	public ParticleRoseMechanic(SkillLineConfig config) {
		super(config);
		this.radius = config.getMathNumber(aliases("radius", "rad"), 1);
		this.petals = config.getMathNumber(aliases("petals"), 5);
		this.points = config.getMathNumber(aliases("points"), 120);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double r = radius.evalAsDouble(eval), k = petals.evalAsDouble(eval);
		int n = limit(points.evalAsInt(eval));
		double range = (Math.round(k) % 2 != 0 && k == Math.rint(k)) ? Math.PI : Math.PI * 2;

		return baked("rose:" + r + ":" + k + ":" + n, () -> ShapeMath.loop(n, t -> {
			double a = t * range;
			double d = Math.cos(k * a) * r;
			return new Vec3(Math.cos(a) * d, 0, Math.sin(a) * d);
		}));
	}
}