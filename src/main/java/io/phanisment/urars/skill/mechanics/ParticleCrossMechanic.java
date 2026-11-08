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
		key = "particleCross",
		namespace = "fx"
)
public class ParticleCrossMechanic extends AbstractShapeMechanic {
	protected final Node length; // full length of each arm
	protected final Node points; // points per arm
	protected final Node vertical;

	public ParticleCrossMechanic(SkillLineConfig config) {
		super(config);
		this.length   = config.getMathNumber(aliases("length", "l"), 2);
		this.points   = config.getMathNumber(aliases("points"), 12);
		this.vertical = config.getMathBoolean(aliases("vertical"), false);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double h = length.evalAsDouble(eval) / 2;
		int n = Math.clamp(points.evalAsInt(eval), 2, MAX_POINTS / 3);
		boolean y = vertical.evalAsBoolean(eval);

		return baked("cross:" + h + ":" + n + ":" + y, () -> {
			List<Vec3> out = new ArrayList<>();
			out.addAll(edge(new Vec3(-h, 0, 0), new Vec3(h, 0, 0), n));
			out.addAll(edge(new Vec3(0, 0, -h), new Vec3(0, 0, h), n));
			if (y) out.addAll(edge(new Vec3(0, -h, 0), new Vec3(0, h, 0), n));
			return ShapeMath.combine(out);
		});
	}
}