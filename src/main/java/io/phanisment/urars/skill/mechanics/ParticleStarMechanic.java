package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.template.particle.AbstractShapeMechanic;
import io.phanisment.urars.util.ShapeMath;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static io.phanisment.urars.util.ActionUtils.*;

@RegistryInfo(
		author = "Phanisment",
		key = "particleStar",
		namespace = "fx"
)
public class ParticleStarMechanic extends AbstractShapeMechanic {
	protected final Node radius; // outer radius
	protected final Node inner;  // inner radius
	protected final Node spikes;
	protected final Node points; // points per edge

	public ParticleStarMechanic(SkillLineConfig config) {
		super(config);
		this.radius = config.getMathNumber(aliases("radius", "rad"), 1);
		this.inner  = config.getMathNumber(aliases("inner"), 0.4);
		this.spikes = config.getMathNumber(aliases("spikes"), 5);
		this.points = config.getMathNumber(aliases("points"), 8);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double outer = radius.evalAsDouble(eval), in = inner.evalAsDouble(eval);
		int s = Math.clamp(spikes.evalAsInt(eval), 2, 64);
		int n = Math.max(2, Math.min(points.evalAsInt(eval), MAX_POINTS / (s * 2)));

		return baked("star:" + outer + ":" + in + ":" + s + ":" + n, () -> {
			Vec3[] corners = new Vec3[s * 2];
			for (int i = 0; i < corners.length; i++) {
				double r = (i % 2 == 0) ? outer : in;
				double a = Math.PI * i / s;
				corners[i] = new Vec3(Math.cos(a) * r, 0, Math.sin(a) * r);
			}
			List<Vec3> out = new ArrayList<>();
			for (int i = 0; i < corners.length; i++) {
				out.addAll(edge(corners[i], corners[(i + 1) % corners.length], n));
			}
			return ShapeMath.combine(out);
		});
	}
}