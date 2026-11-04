package io.phanisment.urars.skill;

import io.phanisment.urars.UrArs;
import io.phanisment.urars.parser.file.config.LineConfig;
import io.phanisment.urars.parser.file.parser.FlagParser;
import io.phanisment.urars.parser.file.parser.LineParser;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.util.BufferedString;
import org.jspecify.annotations.Nullable;

import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import io.phanisment.urars.skill.target.ILocationTarget;
import io.phanisment.urars.skill.target.INoTarget;
import io.phanisment.urars.util.Location;
import io.phanisment.urars.util.UrArsScheduler;
import net.minecraft.world.entity.Entity;

import java.text.ParseException;

public class SkillMechanic implements ISkillAction {
	protected final SkillLineConfig config;
	protected Node delay;
	protected @Nullable SkillTargeter skill_targeter;
	protected @Nullable String trigger;

	public SkillMechanic(SkillLineConfig config) {
		this.config = config;

		this.delay = config.getMathNumber(new String[]{"delay"}, 0);

		try {
			parseContext(config.context());
		} catch (ParseException e) {
			UrArs.LOGGER.error("Failed to parse mechanic flags", e);
		}
	}

	private void parseContext(@Nullable BufferedString context) throws ParseException {
		if (context == null || context.isBlank()) return;
		FlagParser parser = new FlagParser(context);
		for (FlagParser.Flag flag : parser.parse()) {
			BufferedString value = flag.text();
			if (value.isBlank()) continue;
			switch (flag.c()) {
				case '@' -> initializeTargeter(value);
				case '~' -> this.trigger = value.trimEnd().toString();
			}
		}
	}

	private void initializeTargeter(BufferedString context) throws ParseException {
		var targeter_parser = new LineParser(context);
		targeter_parser.parse();
		LineConfig targeter_config = targeter_parser.config();
		var factory = SkillManager.getTargeter(targeter_config.key());
		this.skill_targeter = factory == null ? null : factory.create(new SkillLineConfig(targeter_config));
	}

	public long getDelay(SkillContext context) {
		return delay.evalAsLong(context.evalContext());
	}

	public @Nullable String getTrigger() {
		return this.trigger;
	}

	public void execute(SkillContext ctx) {
		long total_delay = getDelay(ctx) + ctx.global_delay;
		if (total_delay > 0L) UrArsScheduler.wait(total_delay, () -> this.executeAtTarget(ctx));
		else this.executeAtTarget(ctx);
	}

	private void executeAtTarget(SkillContext ctx) {
		if (this.skill_targeter == null) {
			if (this instanceof INoTarget no_target) no_target.cast(ctx);
			return;
		}

		if (skill_targeter.isLocation() && this instanceof ILocationTarget location_target) {
			for (Location loc : skill_targeter.getLocations(ctx)) {
				if (!skill_targeter.conditionIsPass(ctx)) continue;
				location_target.castAtLocation(ctx, loc);
			}
			return;
		}

		switch (this) {
			case IEntityTarget entity_target -> {
				for (Entity target : skill_targeter.getEntities(ctx)) {
					if (!skill_targeter.conditionIsPass(ctx)) continue;
					entity_target.castAtEntity(ctx, target);
				}
			}
			case ILocationTarget location_target -> {
				for (Location loc : skill_targeter.getLocations(ctx)) {
					if (!skill_targeter.conditionIsPass(ctx)) continue;
					location_target.castAtLocation(ctx, loc);
				}
			}
			case INoTarget no_target when skill_targeter.conditionIsPass(ctx) -> no_target.cast(ctx);
			default -> {
			}
		}
	}
}