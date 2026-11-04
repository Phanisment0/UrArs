package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.INoTarget;
import io.phanisment.urars.util.UrArsScheduler;
import net.minecraft.world.entity.Entity;
import io.phanisment.urars.skill.target.IEntityTarget;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		key = "loop"
)
public class LoopMechanic extends SkillMechanic implements INoTarget, IEntityTarget {
	protected final Node repeat;
	protected final Node interval;
	protected final Node sync_tick;
	protected final MechanicsExecutor mechanics;
	protected final MechanicsExecutor finally_mechanics;

	public LoopMechanic(SkillLineConfig config) {
		super(config);
		this.repeat = config.getMathNumber(aliases("repeat", "r"), 2);
		this.interval = config.getMathNumber(aliases("interval", "i"), 1);
		this.sync_tick = config.getMathBoolean(aliases("syncTick", "st"), true);
		this.mechanics = config.getMechanics("do");
		this.finally_mechanics = config.getMechanics("finally", "fin");
	}

	@Override
	public void cast(SkillContext ctx) {
		this.castAtEntity(ctx, ctx.entity());
	}

	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		int times = repeat.evalAsInt(ctx.evalContext());
		long gap = interval.evalAsLong(ctx.evalContext());

		if (times <= 0) {
			finally_mechanics.execute(ctx);
			return;
		}

		if (gap <= 0) {
			for (int i = 0; i < times; i++) mechanics.execute(ctx);
			finally_mechanics.execute(ctx);
			return;
		}

		if (sync_tick.evalAsBoolean(ctx.evalContext())) stepTick(ctx, times, gap);
		else stepTime(ctx, times, gap, System.nanoTime());
	}

	private void stepTick(SkillContext ctx, int remaining, long gap) {
		if (isCasterGone(ctx)) {
			finally_mechanics.execute(ctx);
			return;
		}

		mechanics.execute(ctx);

		if (remaining > 1) UrArsScheduler.wait(gap, () -> stepTick(ctx, remaining - 1, gap));
		else finally_mechanics.execute(ctx);;
	}

	private void stepTime(SkillContext ctx, int remaining, long gap_ms, long deadline) {
		if (isCasterGone(ctx)) {
			finally_mechanics.execute(ctx);
			return;
		}

		mechanics.execute(ctx);

		if (remaining > 1) {
			long next = deadline + gap_ms * 1_000_000L;
			UrArsScheduler.waitUntilNanos(next, () -> stepTime(ctx, remaining - 1, gap_ms, next));
		} else finally_mechanics.execute(ctx);
	}

	private boolean isCasterGone(SkillContext ctx) {
		Entity caster = ctx.entity();
		return caster != null && caster.isRemoved();
	}
}