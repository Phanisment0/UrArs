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
		key = "particleBox"
)
public class ParticleBoxMechanic extends AbstractShapeMechanic {
	private static final int[][] EDGES = {
			{0, 1}, {1, 2}, {2, 3}, {3, 0}, // bottom
			{4, 5}, {5, 6}, {6, 7}, {7, 4}, // top
			{0, 4}, {1, 5}, {2, 6}, {3, 7}  // pillars
	};

	protected final Node width;   // X
	protected final Node height;  // Y
	protected final Node length;  // Z
	protected final Node points;  // points per edge

	public ParticleBoxMechanic(SkillLineConfig config) {
		super(config);
		this.width  = config.getMathNumber(aliases("width", "w"), 1);
		this.height = config.getMathNumber(aliases("height", "h"), 1);
		this.length = config.getMathNumber(aliases("length", "l"), 1);
		this.points = config.getMathNumber(aliases("points"), 8);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double hx = width.evalAsDouble(eval) / 2;
		double hy = height.evalAsDouble(eval) / 2;
		double hz = length.evalAsDouble(eval) / 2;
		int n = Math.clamp(points.evalAsInt(eval), 2, MAX_POINTS / 12);

		return baked("box:" + hx + ":" + hy + ":" + hz + ":" + n, () -> {
			Vec3[] v = {
					new Vec3(-hx, -hy, -hz), new Vec3(hx, -hy, -hz),
					new Vec3(hx, -hy, hz),   new Vec3(-hx, -hy, hz),
					new Vec3(-hx, hy, -hz),  new Vec3(hx, hy, -hz),
					new Vec3(hx, hy, hz),    new Vec3(-hx, hy, hz)
			};
			List<Vec3> out = new ArrayList<>();
			for (int[] e : EDGES) out.addAll(edge(v[e[0]], v[e[1]], n));
			return ShapeMath.combine(out);
		});
	}
}