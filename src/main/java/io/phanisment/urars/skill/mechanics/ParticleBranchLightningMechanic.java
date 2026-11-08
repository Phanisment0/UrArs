package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.template.particle.AbstractShapeMechanic;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.phanisment.urars.util.ActionUtils.*;

@RegistryInfo(
		author = "Phanisment",
		key = "particleBranchLightning",
		namespace = "fx"
)
public class ParticleBranchLightningMechanic extends AbstractShapeMechanic {
	protected final Node length;
	protected final Node segments;
	protected final Node jitter;
	protected final Node points;
	protected final Node chance;
	protected final Node depth;
	protected final Node fork;
	protected final Node angle;
	protected final Node min_angle;

	public ParticleBranchLightningMechanic(SkillLineConfig config) {
		super(config);
		this.length    = config.getMathNumber(aliases("length", "l"), 6);
		this.segments  = config.getMathNumber(aliases("segments"), 8);
		this.jitter    = config.getMathNumber(aliases("jitter"), 0.5);
		this.points    = config.getMathNumber(aliases("points"), 5);
		this.chance    = config.getMathNumber(aliases("chance"), 0.4);
		this.depth     = config.getMathNumber(aliases("depth"), 2);
		this.fork      = config.getMathNumber(aliases("fork"), 0.5);
		this.angle     = config.getMathNumber(aliases("angle"), 55);
		this.min_angle = config.getMathNumber(aliases("minAngle"), 15);
	}

	@Override
	protected List<Vec3> shape(Cast cast) {
		var eval = cast.eval();
		Builder b = new Builder(
				Math.abs(jitter.evalAsDouble(eval)), Math.clamp(chance.evalAsDouble(eval), 0, 1), Math.clamp(depth.evalAsInt(eval), 0, 6), Math.clamp(fork.evalAsDouble(eval), 0.05, 1),
				Math.toRadians(Math.max(0, angle.evalAsDouble(eval))),
				Math.toRadians(Math.max(0, min_angle.evalAsDouble(eval))));

		int seg = Math.clamp(segments.evalAsInt(eval), 1, 64);
		int pts = Math.clamp(points.evalAsInt(eval), 2, 32);

		b.bolt(new Vec3(0, 0, 0), new Vec3(0, 1, 0), length.evalAsDouble(eval), seg, pts, 0);
		return b.out;
	}

	/** Builds one bolt recursively. A new instance is used per cast, so it is thread-safe. */
	private static final class Builder {
		final List<Vec3> out = new ArrayList<>();
		final ThreadLocalRandom rnd = ThreadLocalRandom.current();
		final double jitter, chance, fork, max_angle, min_angle;
		final int max_depth;

		Builder(double jitter, double chance, int max_depth, double fork, double max_angle, double min_angle) {
			this.jitter = jitter;
			this.chance = chance;
			this.max_depth = max_depth;
			this.fork = fork;
			this.max_angle = Math.max(max_angle, min_angle);
			this.min_angle = Math.min(max_angle, min_angle);
		}

		void bolt(Vec3 start, Vec3 dir, double len, int segs, int pts, int level) {
			// two unit vectors perpendicular to dir, used for sideways jitter and fork direction
			Vec3 helper = Math.abs(dir.y) < 0.99 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
			Vec3 u = dir.cross(helper).normalize();
			Vec3 w = dir.cross(u).normalize();
			double j = jitter * Math.pow(0.75, level);

			Vec3 end = start.add(dir.scale(len));
			Vec3 prev = start;
			for (int i = 1; i <= segs; i++) {
				if (out.size() >= MAX_POINTS) return;

				Vec3 next;
				if (i == segs) {
					next = end;
				} else {
					Vec3 along = start.add(dir.scale(len * i / segs));
					double a = j == 0 ? 0 : rnd.nextDouble(-j, j);
					double b = j == 0 ? 0 : rnd.nextDouble(-j, j);
					next = along.add(u.scale(a)).add(w.scale(b));
				}
				addEdge(prev, next, pts);

				if (i < segs && level < max_depth && rnd.nextDouble() < chance) {
					double theta = min_angle + rnd.nextDouble() * (max_angle - min_angle);
					double phi = rnd.nextDouble() * Math.PI * 2;
					Vec3 side = u.scale(Math.cos(phi)).add(w.scale(Math.sin(phi)));
					Vec3 forkDir = dir.scale(Math.cos(theta)).add(side.scale(Math.sin(theta))).normalize();

					double forkLen = len * fork * (0.6 + rnd.nextDouble() * 0.4);
					int forkSegs = Math.max(2, segs / 2);
					int forkPts = Math.max(2, pts * 2 / 3);
					bolt(next, forkDir, forkLen, forkSegs, forkPts, level + 1);
				}
				prev = next;
			}
		}

		void addEdge(Vec3 a, Vec3 b, int pts) {
			for (int i = 0; i < pts && out.size() < MAX_POINTS; i++) {
				double t = (double) i / (pts - 1);
				out.add(new Vec3(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t, a.z + (b.z - a.z) * t));
			}
		}
	}
}