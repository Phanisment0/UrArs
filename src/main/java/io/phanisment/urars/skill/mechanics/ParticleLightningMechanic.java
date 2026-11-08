package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.template.particle.AbstractShapeMechanic;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static io.phanisment.urars.util.ActionUtils.*;

import java.util.concurrent.ThreadLocalRandom;

@RegistryInfo(
		author = "Phanisment",
		key = "particleLightning",
		namespace = "fx"
)
public class ParticleLightningMechanic extends AbstractShapeMechanic {
	protected final Node length;
	protected final Node segments;
	protected final Node jitter;  // max sideways offset of each joint
	protected final Node points;  // points per segment

	public ParticleLightningMechanic(SkillLineConfig config) {
		super(config);
		this.length   = config.getMathNumber(aliases("length", "l"), 6);
		this.segments = config.getMathNumber(aliases("segments"), 8);
		this.jitter   = config.getMathNumber(aliases("jitter"), 0.5);
		this.points   = config.getMathNumber(aliases("points"), 6);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		double len = length.evalAsDouble(eval), j = Math.abs(jitter.evalAsDouble(eval));
		int seg = Math.clamp(segments.evalAsInt(eval), 1, 64);
		int n = Math.max(2, Math.min(points.evalAsInt(eval), MAX_POINTS / seg));

		ThreadLocalRandom rnd = ThreadLocalRandom.current();
		List<Vec3> out = new ArrayList<>();
		Vec3 prev = new Vec3(0, 0, 0);
		for (int i = 1; i <= seg; i++) {
			Vec3 next = i == seg
					? new Vec3(0, len, 0)
					: new Vec3(j == 0 ? 0 : rnd.nextDouble(-j, j), len * i / seg, j == 0 ? 0 : rnd.nextDouble(-j, j));
			out.addAll(edge(prev, next, n));
			prev = next;
		}
		return out;
	}
}