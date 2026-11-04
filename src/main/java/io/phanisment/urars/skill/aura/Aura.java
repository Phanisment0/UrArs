package io.phanisment.urars.skill.aura;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.phanisment.urars.util.ActionUtils.aliases;

/**
 * Base class for all aura mechanics. Handles config and hooks only;
 * running auras are tracked by {@link AuraManager}.
 */
public abstract class Aura extends SkillMechanic implements IEntityTarget {
	protected final Node name;
	protected final Node duration;
	protected final Node interval;

	protected final MechanicsExecutor on_start;
	protected final MechanicsExecutor on_tick;
	protected final MechanicsExecutor on_end;

	private final Map<String, MechanicsExecutor> events = new HashMap<>();

	protected Aura(SkillLineConfig config) {
		this(config, UUID.randomUUID().toString());
	}

	protected Aura(SkillLineConfig config, String default_name) {
		super(config);
		name = config.getMathString(aliases("name", "n", "aura", "a"), default_name);
		duration = config.getMathNumber(aliases("duration", "d"), 400); // < 0 = infinite
		interval = config.getMathNumber(aliases("interval", "i"), 1);

		on_start = config.getMechanics("onStart", "os");
		on_tick = config.getMechanics("onTick", "ot");
		on_end = config.getMechanics("onEnd", "oe");
	}

	protected void onEvent(String event, String... keys) {
		MechanicsExecutor executor = config.getMechanics(keys);
		if (executor != null) events.put(event, executor);
	}

	public MechanicsExecutor getEvent(String event) {
		return events.get(event);
	}

	protected void start(ActiveAura a) {
		if (on_start != null) on_start.execute(a.context());
	}

	protected void tick(ActiveAura a) {
		if (on_tick != null) on_tick.execute(a.context());
	}

	protected void end(ActiveAura a) {
		if (on_end != null) on_end.execute(a.context());
	}

	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		var eval = ctx.evalContext();
		AuraManager.start(this, ctx, target, name.evalAsString(eval), duration.evalAsLong(eval), interval.evalAsLong(eval));
	}
}