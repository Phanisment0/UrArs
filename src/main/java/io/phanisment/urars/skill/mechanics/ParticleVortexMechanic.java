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
		key = "particleVortex",
		namespace = "fx"
)
public class ParticleVortexMechanic extends AbstractShapeMechanic {
	protected final Node radius;    // radius at the top
	protected final Node minRadius; // radius at the bottom
	protected final Node height;
	protected final Node turns;
	protected final Node points;

	public ParticleVortexMechanic(SkillLineConfig config) {
		super(config);
		this.radius    = config.getMathNumber(aliases("radius", "rad"), 2);
		this.minRadius = config.getMathNumber(aliases("minRadius"), 0.2);
		this.height    = config.getMathNumber(aliases("height", "h"), 4);
		this.turns     = config.getMathNumber(aliases("turns"), 5);
		this.points    = config.getMathNumber(aliases("points"), 100);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double top = radius.evalAsDouble(eval), bottom = minRadius.evalAsDouble(eval);
		double h = height.evalAsDouble(eval), tn = turns.evalAsDouble(eval);
		int n = limit(points.evalAsInt(eval));

		return baked("vortex:" + top + ":" + bottom + ":" + h + ":" + tn + ":" + n, () -> ShapeMath.shape(n, t -> {
			double a = t * tn * Math.PI * 2;
			double rr = bottom + (top - bottom) * t;
			return new Vec3(Math.cos(a) * rr, t * h, Math.sin(a) * rr);
		}));
	}
}