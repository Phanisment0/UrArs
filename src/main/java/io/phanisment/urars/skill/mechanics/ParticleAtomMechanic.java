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
		key = "particleAtom",
		namespace = "fx"
)
public class ParticleAtomMechanic extends AbstractShapeMechanic {
	protected final Node radius;
	protected final Node rings;
	protected final Node points; // points per ring
	protected final Node tilt;   // degrees

	public ParticleAtomMechanic(SkillLineConfig config) {
		super(config);
		this.radius = config.getMathNumber(aliases("radius", "rad"), 1);
		this.rings  = config.getMathNumber(aliases("rings"), 3);
		this.points = config.getMathNumber(aliases("points"), 32);
		this.tilt   = config.getMathNumber(aliases("tilt"), 60);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double r = radius.evalAsDouble(eval), tl = Math.toRadians(tilt.evalAsDouble(eval));
		int k = Math.clamp(rings.evalAsInt(eval), 1, 16);
		int n = Math.max(1, Math.min(points.evalAsInt(eval), MAX_POINTS / k));

		return baked("atom:" + r + ":" + tl + ":" + k + ":" + n, () -> {
			List<Vec3> out = new ArrayList<>();
			double ct = Math.cos(tl), st = Math.sin(tl);
			for (int ring = 0; ring < k; ring++) {
				double phi = Math.PI * ring / k;
				double cp = Math.cos(phi), sp = Math.sin(phi);
				out.addAll(ShapeMath.loop(n, t -> {
					double a = t * Math.PI * 2;
					double x = Math.cos(a) * r, z = Math.sin(a) * r;
					// tilt around X, then spin around Y
					double y1 = -z * st, z1 = z * ct;
					return new Vec3(x * cp + z1 * sp, y1, -x * sp + z1 * cp);
				}));
			}
			return ShapeMath.combine(out);
		});
	}
}