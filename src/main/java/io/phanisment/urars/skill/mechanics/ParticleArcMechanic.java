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
		key = "particleArc",
		namespace = "fx"
)
public class ParticleArcMechanic extends AbstractShapeMechanic {
	protected final Node radius;
	protected final Node angle;
	protected final Node start;
	protected final Node points;

	public ParticleArcMechanic(SkillLineConfig config) {
		super(config);
		this.radius = config.getMathNumber(aliases("radius", "rad"), 1);
		this.angle  = config.getMathNumber(aliases("angle"), 180);
		this.start  = config.getMathNumber(aliases("start"), 0);
		this.points = config.getMathNumber(aliases("points"), 24);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double r = radius.evalAsDouble(eval);
		double span = Math.toRadians(angle.evalAsDouble(eval));
		double from = Math.toRadians(start.evalAsDouble(eval));
		int n = limit(points.evalAsInt(eval));

		return baked("arc:" + r + ":" + span + ":" + from + ":" + n, () -> ShapeMath.shape(n, t -> {
			double a = from + t * span;
			return new Vec3(Math.cos(a) * r, 0, Math.sin(a) * r);
		}));
	}
}