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
		key = "particleRect",
		namespace = "fx"
)
public class ParticleRectMechanic extends AbstractShapeMechanic {
	protected final Node width;  // X
	protected final Node length; // Z
	protected final Node points; // points per edge

	public ParticleRectMechanic(SkillLineConfig config) {
		super(config);
		this.width  = config.getMathNumber(aliases("width", "w"), 2);
		this.length = config.getMathNumber(aliases("length", "l"), 2);
		this.points = config.getMathNumber(aliases("points"), 10);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double hx = width.evalAsDouble(eval) / 2, hz = length.evalAsDouble(eval) / 2;
		int n = Math.clamp(points.evalAsInt(eval), 2, MAX_POINTS / 4);

		return baked("rect:" + hx + ":" + hz + ":" + n, () -> {
			Vec3[] c = {
					new Vec3(-hx, 0, -hz), new Vec3(hx, 0, -hz),
					new Vec3(hx, 0, hz),   new Vec3(-hx, 0, hz)
			};
			List<Vec3> out = new ArrayList<>();
			for (int i = 0; i < 4; i++) out.addAll(edge(c[i], c[(i + 1) % 4], n));
			return ShapeMath.combine(out);
		});
	}
}