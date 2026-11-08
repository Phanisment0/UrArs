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
		key = "particleCrescent",
		namespace = "fx"
)
public class ParticleCrescentMechanic extends AbstractShapeMechanic {
	protected final Node radius;
	protected final Node offset; // inner circle shift, fraction of radius
	protected final Node inner;  // inner circle size, fraction of radius
	protected final Node points; // samples per circle

	public ParticleCrescentMechanic(SkillLineConfig config) {
		super(config);
		this.radius = config.getMathNumber(aliases("radius", "rad"), 1);
		this.offset = config.getMathNumber(aliases("offset"), 0.4);
		this.inner  = config.getMathNumber(aliases("inner"), 0.8);
		this.points = config.getMathNumber(aliases("points"), 48);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double r = radius.evalAsDouble(eval);
		double d = r * offset.evalAsDouble(eval), ri = r * inner.evalAsDouble(eval);
		int n = Math.clamp(points.evalAsInt(eval), 3, MAX_POINTS / 2);

		return baked("crescent:" + r + ":" + d + ":" + ri + ":" + n, () -> {
			List<Vec3> out = new ArrayList<>();
			for (int i = 0; i < n; i++) {
				double a = Math.PI * 2 * i / n;
				double x = Math.cos(a) * r, y = Math.sin(a) * r;
				if (Math.hypot(x - d, y) >= ri) out.add(new Vec3(x, y, 0)); // outer edge not inside the cut
			}
			for (int i = 0; i < n; i++) {
				double a = Math.PI * 2 * i / n;
				double x = d + Math.cos(a) * ri, y = Math.sin(a) * ri;
				if (Math.hypot(x, y) <= r) out.add(new Vec3(x, y, 0)); // cut edge inside the moon
			}
			return List.copyOf(out);
		});
	}
}