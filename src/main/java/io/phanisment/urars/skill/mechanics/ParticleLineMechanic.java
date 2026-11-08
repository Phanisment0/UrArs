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
		key = "particleLine"
)
public class ParticleLineMechanic extends AbstractShapeMechanic {
	protected final Node length;
	protected final Node points;

	public ParticleLineMechanic(SkillLineConfig config) {
		super(config);
		this.length = config.getMathNumber(aliases("length", "l"), 5);
		this.points = config.getMathNumber(aliases("points"), 16);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		int n = limit(points.evalAsInt(eval));
		double len = length.evalAsDouble(eval);

		return baked("line:" + n + ":" + len, () -> ShapeMath.shape(n, t -> new Vec3(0, t * len, 0)));
	}
}