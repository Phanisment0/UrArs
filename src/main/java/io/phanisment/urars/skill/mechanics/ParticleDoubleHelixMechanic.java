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
		key = "particleDoubleHelix",
		namespace = "fx"
)
public class ParticleDoubleHelixMechanic extends AbstractShapeMechanic {
	protected final Node radius;
	protected final Node height;
	protected final Node turns;
	protected final Node points; // points per strand
	protected final Node rungs;  // connecting bars between the strands (0 = none)

	public ParticleDoubleHelixMechanic(SkillLineConfig config) {
		super(config);
		this.radius = config.getMathNumber(aliases("radius", "rad"), 1);
		this.height = config.getMathNumber(aliases("height", "h"), 3);
		this.turns  = config.getMathNumber(aliases("turns"), 3);
		this.points = config.getMathNumber(aliases("points"), 48);
		this.rungs  = config.getMathNumber(aliases("rungs"), 0);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double r = radius.evalAsDouble(eval), h = height.evalAsDouble(eval), tn = turns.evalAsDouble(eval);
		int n = Math.clamp(points.evalAsInt(eval), 1, MAX_POINTS / 3);
		int rg = Math.clamp(rungs.evalAsInt(eval), 0, 64);

		return baked("dhelix:" + r + ":" + h + ":" + tn + ":" + n + ":" + rg, () -> {
			List<Vec3> out = new ArrayList<>();
			out.addAll(ShapeMath.shape(n, t -> strand(t, r, h, tn, 0)));
			out.addAll(ShapeMath.shape(n, t -> strand(t, r, h, tn, Math.PI)));
			for (int i = 0; i < rg; i++) {
				double t = rg == 1 ? 0.5 : (double) i / (rg - 1);
				out.addAll(edge(strand(t, r, h, tn, 0), strand(t, r, h, tn, Math.PI), 5));
			}
			return ShapeMath.combine(out);
		});
	}

	private static Vec3 strand(double t, double r, double h, double turns, double phase) {
		double a = t * turns * Math.PI * 2 + phase;
		return new Vec3(Math.cos(a) * r, t * h, Math.sin(a) * r);
	}
}