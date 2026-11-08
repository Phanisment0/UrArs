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
		key = "particlePyramid",
		namespace = "fx"
)
public class ParticlePyramidMechanic extends AbstractShapeMechanic {
	protected final Node width; // base side length
	protected final Node height;
	protected final Node points; // points per edge

	public ParticlePyramidMechanic(SkillLineConfig config) {
		super(config);
		this.width  = config.getMathNumber(aliases("width", "w"), 1);
		this.height = config.getMathNumber(aliases("height", "h"), 1);
		this.points = config.getMathNumber(aliases("points"), 8);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double half = width.evalAsDouble(eval) / 2, h = height.evalAsDouble(eval);
		int n = Math.max(2, Math.min(points.evalAsInt(eval), MAX_POINTS / 8));

		return baked("pyramid:" + half + ":" + h + ":" + n, () -> {
			Vec3[] base = {
					new Vec3(-half, 0, -half), new Vec3(half, 0, -half),
					new Vec3(half, 0, half),   new Vec3(-half, 0, half)
			};
			Vec3 apex = new Vec3(0, h, 0);
			List<Vec3> out = new ArrayList<>();
			for (int i = 0; i < 4; i++) {
				out.addAll(edge(base[i], base[(i + 1) % 4], n));
				out.addAll(edge(base[i], apex, n));
			}
			return ShapeMath.combine(out);
		});
	}
}