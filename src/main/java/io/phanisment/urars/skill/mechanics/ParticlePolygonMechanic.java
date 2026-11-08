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
		key = "particlePolygon",
		namespace = "fx"
)
public class ParticlePolygonMechanic extends AbstractShapeMechanic {
	protected final Node radius;
	protected final Node sides;
	protected final Node points; // points per edge

	public ParticlePolygonMechanic(SkillLineConfig config) {
		super(config);
		this.radius = config.getMathNumber(aliases("radius", "rad"), 1);
		this.sides  = config.getMathNumber(aliases("sides"), 3);
		this.points = config.getMathNumber(aliases("points"), 8);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double r = radius.evalAsDouble(eval);
		int s = Math.max(3, Math.min(sides.evalAsInt(eval), 64));
		int n = Math.max(2, Math.min(points.evalAsInt(eval), MAX_POINTS / s));

		return baked("polygon:" + r + ":" + s + ":" + n, () -> {
			Vec3[] corners = new Vec3[s];
			for (int i = 0; i < s; i++) {
				double a = Math.PI * 2 * i / s;
				corners[i] = new Vec3(Math.cos(a) * r, 0, Math.sin(a) * r);
			}
			List<Vec3> out = new ArrayList<>();
			for (int i = 0; i < s; i++) out.addAll(edge(corners[i], corners[(i + 1) % s], n));
			return ShapeMath.combine(out);
		});
	}
}