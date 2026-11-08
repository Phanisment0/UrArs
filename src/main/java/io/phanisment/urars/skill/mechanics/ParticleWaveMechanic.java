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
		key = "particleWave",
		namespace = "fx"
)
public class ParticleWaveMechanic extends AbstractShapeMechanic {
	protected final Node length;
	protected final Node amplitude;
	protected final Node waves;
	protected final Node points;

	public ParticleWaveMechanic(SkillLineConfig config) {
		super(config);
		this.length    = config.getMathNumber(aliases("length", "l"), 5);
		this.amplitude = config.getMathNumber(aliases("amplitude", "amp"), 0.5);
		this.waves     = config.getMathNumber(aliases("waves"), 3);
		this.points    = config.getMathNumber(aliases("points"), 48);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double len = length.evalAsDouble(eval), amp = amplitude.evalAsDouble(eval), w = waves.evalAsDouble(eval);
		int n = limit(points.evalAsInt(eval));

		return baked("wave:" + len + ":" + amp + ":" + w + ":" + n, () -> ShapeMath.shape(n, t ->
				new Vec3(Math.sin(t * w * Math.PI * 2) * amp, t * len, 0)));
	}
}