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
		key = "particleLissajous",
		namespace = "fx"
)
public class ParticleLissajousMechanic extends AbstractShapeMechanic {
	protected final Node radius;
	protected final Node freq_x;
	protected final Node freq_y;
	protected final Node freq_z;
	protected final Node phase;  // degrees, applied to X
	protected final Node points;

	public ParticleLissajousMechanic(SkillLineConfig config) {
		super(config);
		this.radius = config.getMathNumber(aliases("radius", "rad"), 1);
		this.freq_x = config.getMathNumber(aliases("freqX"), 3);
		this.freq_y = config.getMathNumber(aliases("freqY"), 2);
		this.freq_z = config.getMathNumber(aliases("freqZ"), 5);
		this.phase  = config.getMathNumber(aliases("phase"), 90);
		this.points = config.getMathNumber(aliases("points"), 200);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double r = radius.evalAsDouble(eval);
		double fx = freq_x.evalAsDouble(eval), fy = freq_y.evalAsDouble(eval), fz = freq_z.evalAsDouble(eval);
		double ph = Math.toRadians(phase.evalAsDouble(eval));
		int n = limit(points.evalAsInt(eval));

		return baked("liss:" + r + ":" + fx + ":" + fy + ":" + fz + ":" + ph + ":" + n, () -> ShapeMath.loop(n, t -> {
			double a = t * Math.PI * 2;
			return new Vec3(Math.sin(fx * a + ph) * r, Math.sin(fy * a) * r, Math.sin(fz * a) * r);
		}));
	}
}