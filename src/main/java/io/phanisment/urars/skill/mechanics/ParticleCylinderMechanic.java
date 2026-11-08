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
		key = "particleCylinder",
		namespace = "fx"
)
public class ParticleCylinderMechanic extends AbstractShapeMechanic {
	protected final Node radius;
	protected final Node height;
	protected final Node points; // points around each layer
	protected final Node layers; // rings stacked along the height

	public ParticleCylinderMechanic(SkillLineConfig config) {
		super(config);
		this.radius = config.getMathNumber(aliases("radius", "rad"), 1);
		this.height = config.getMathNumber(aliases("height", "h"), 2);
		this.points = config.getMathNumber(aliases("points"), 32);
		this.layers = config.getMathNumber(aliases("layers"), 5);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double r = radius.evalAsDouble(eval), h = height.evalAsDouble(eval);
		int n = limit(points.evalAsInt(eval));
		int l = Math.max(1, Math.min(layers.evalAsInt(eval), MAX_POINTS / n));

		return baked("cylinder:" + r + ":" + h + ":" + n + ":" + l, () -> ShapeMath.surface(n, l, (u, v) -> {
			double a = u * Math.PI * 2;
			double y = l == 1 ? 0 : v * l / (l - 1) * h;
			return new Vec3(Math.cos(a) * r, y, Math.sin(a) * r);
		}));
	}
}