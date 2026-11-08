package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.template.particle.AbstractShapeMechanic;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static io.phanisment.urars.util.ActionUtils.*;

import java.util.concurrent.ThreadLocalRandom;

@RegistryInfo(
		author = "Phanisment",
		key = "particleCloud",
		namespace = "fx"
)
public class ParticleCloudMechanic extends AbstractShapeMechanic {
	protected final Node radius;
	protected final Node points;

	public ParticleCloudMechanic(SkillLineConfig config) {
		super(config);
		this.radius = config.getMathNumber(aliases("radius", "rad"), 1);
		this.points = config.getMathNumber(aliases("points"), 30);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double r = radius.evalAsDouble(eval);
		int n = limit(points.evalAsInt(eval));

		ThreadLocalRandom rnd = ThreadLocalRandom.current();
		List<Vec3> out = new ArrayList<>(n);
		for (int i = 0; i < n; i++) {
			double x = rnd.nextGaussian(), y = rnd.nextGaussian(), z = rnd.nextGaussian();
			double len = Math.sqrt(x * x + y * y + z * z);
			if (len < 1.0E-9) continue;
			double d = Math.cbrt(rnd.nextDouble()) * r / len;
			out.add(new Vec3(x * d, y * d, z * d));
		}
		return out;
	}
}