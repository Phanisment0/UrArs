package io.phanisment.urars.skill.template.particle;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.parser.math.eval.EvalContext;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import io.phanisment.urars.skill.target.ILocationTarget;
import io.phanisment.urars.skill.target.INoTarget;
import io.phanisment.urars.util.Location;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import static io.phanisment.urars.util.ActionUtils.*;

	/**
	 * Base class for every particle mechanic. It owns everything that is the same
	 * for all of them:
	 *   - particle attributes (particle, color, size, block, item, ...) via ParticleSpec
	 *   - count, dx/dy/dz (spread), speed
	 *   - ox/oy/oz offset from the target
	 *   - target handling (self, entity, location) and ServerLevel / options checks
	 *
	 * A subclass only reads its own arguments in the constructor and implements
	 * {@link #emit(Cast)}.
	 */
	public abstract class AbstractParticleMechanic extends SkillMechanic implements IEntityTarget, ILocationTarget, INoTarget {
		protected final ParticleSpec spec;
		protected final Node count;
		protected final Node dx;
		protected final Node dy;
		protected final Node dz;
		protected final Node speed;
		protected final Node off_x;
		protected final Node off_y;
		protected final Node off_z;

		protected AbstractParticleMechanic(SkillLineConfig config) {
			super(config);
			this.spec  = new ParticleSpec(config);
			this.count = config.getMathNumber(aliases("count", "c", "amount"), 1);
			this.dx    = config.getMathNumber(aliases("dx", "spreadX"), 0);
			this.dy    = config.getMathNumber(aliases("dy", "spreadY"), 0);
			this.dz    = config.getMathNumber(aliases("dz", "spreadZ"), 0);
			this.speed = config.getMathNumber(aliases("speed", "s"), 0);
			this.off_x = config.getMathNumber(aliases("offX", "ox"), 0);
			this.off_y = config.getMathNumber(aliases("offY", "oy"), 0);
			this.off_z = config.getMathNumber(aliases("offZ", "oz"), 0);
		}

		/** Implement this: draw using the prepared {@link Cast}. */
		protected abstract void emit(Cast cast);

		// ------------------------------------------------------------------
		// Targets
		// ------------------------------------------------------------------

		@Override
		public void cast(SkillContext ctx) {
			Entity caster = ctx.entity();
			if (caster == null) return;
			run(ctx, caster.level(), caster.position());
		}

		@Override
		public void castAtEntity(SkillContext ctx, Entity target) {
			if (target == null) return;
			run(ctx, target.level(), target.position());
		}

		@Override
		public void castAtLocation(SkillContext ctx, Location location) {
			run(ctx, location.level(), location.pos());
		}

		private void run(SkillContext ctx, Level level, Vec3 position) {
			if (!(level instanceof ServerLevel server)) return;
			EvalContext eval = ctx.evalContext();

			ParticleOptions options = spec.build(eval);
			if (options == null) return;

			Vec3 origin = position.add(
					off_x.evalAsDouble(eval),
					off_y.evalAsDouble(eval),
					off_z.evalAsDouble(eval));

			emit(new Cast(
					ctx, ctx.entity(), server, options, origin,
					Math.max(1, count.evalAsInt(eval)),
					dx.evalAsDouble(eval), dy.evalAsDouble(eval), dz.evalAsDouble(eval),
					speed.evalAsDouble(eval)));
		}

		// ------------------------------------------------------------------
		// Per-cast data handed to subclasses
		// ------------------------------------------------------------------

		/**
		 * Everything resolved for one cast. `origin` already includes ox/oy/oz.
		 * Use spawn(...) to send particles with the user's count/spread/speed.
		 */
		public record Cast(
				SkillContext ctx,
				@Nullable Entity caster,
				ServerLevel level,
				ParticleOptions options,
				Vec3 origin,
				int count,
				double dx, double dy, double dz,
				double speed
		) {
			public EvalContext eval() {
				return ctx.evalContext();
			}

			public void spawn(Vec3 pos) {
				level.sendParticles(options, pos.x, pos.y, pos.z, count, dx, dy, dz, speed);
			}

			public void spawn(Iterable<Vec3> positions) {
				for (Vec3 p : positions) spawn(p);
			}
		}
	}
