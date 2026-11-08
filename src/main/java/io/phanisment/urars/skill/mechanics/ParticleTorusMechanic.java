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
		key = "particleTorus"
)
public class ParticleTorusMechanic extends AbstractShapeMechanic {
	protected final Node radius;     // distance from center to the tube's center
	protected final Node tube;       // tube radius
	protected final Node points;     // points around the big ring
	protected final Node tubePoints; // points around the tube

	public ParticleTorusMechanic(SkillLineConfig config) {
		super(config);
		this.radius     = config.getMathNumber(aliases("radius", "rad"), 1);
		this.tube       = config.getMathNumber(aliases("tube"), 0.25);
		this.points     = config.getMathNumber(aliases("points"), 48);
		this.tubePoints = config.getMathNumber(aliases("tubePoints"), 12);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double r = radius.evalAsDouble(eval), minor = tube.evalAsDouble(eval);
		int n = limit(points.evalAsInt(eval));
		int tn = Math.max(1, Math.min(tubePoints.evalAsInt(eval), MAX_POINTS / n));

		return baked("torus:" + r + ":" + minor + ":" + n + ":" + tn, () -> ShapeMath.surface(n, tn, (u, v) -> {
			double a = u * Math.PI * 2, b = v * Math.PI * 2;
			double ring = r + minor * Math.cos(b);
			return new Vec3(ring * Math.cos(a), minor * Math.sin(b), ring * Math.sin(a));
		}));
	}
}