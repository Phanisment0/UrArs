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
		key = "particleGrid",
		namespace = "fx"
)
public class ParticleGridMechanic extends AbstractShapeMechanic {
	protected final Node width;
	protected final Node length;
	protected final Node cols; // points along X
	protected final Node rows; // points along Z

	public ParticleGridMechanic(SkillLineConfig config) {
		super(config);
		this.width  = config.getMathNumber(aliases("width", "w"), 4);
		this.length = config.getMathNumber(aliases("length", "l"), 4);
		this.cols   = config.getMathNumber(aliases("cols"), 8);
		this.rows   = config.getMathNumber(aliases("rows"), 8);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double w = width.evalAsDouble(eval), l = length.evalAsDouble(eval);
		int c = limit(cols.evalAsInt(eval));
		int r = Math.max(1, Math.min(rows.evalAsInt(eval), MAX_POINTS / c));

		return baked("grid:" + w + ":" + l + ":" + c + ":" + r, () -> ShapeMath.surface(c, r, (u, v) -> {
			double fx = c == 1 ? 0.5 : u * c / (c - 1);
			double fz = r == 1 ? 0.5 : v * r / (r - 1);
			return new Vec3((fx - 0.5) * w, 0, (fz - 0.5) * l);
		}));
	}
}