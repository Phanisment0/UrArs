package io.phanisment.urars.skill;

import io.phanisment.urars.lib.file_parser.config.LineConfig;
import io.phanisment.urars.lib.file_parser.parser.FlagParser;
import io.phanisment.urars.lib.file_parser.parser.LineParser;
import io.phanisment.urars.util.BufferedString;
import org.jspecify.annotations.Nullable;

import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import io.phanisment.urars.skill.target.ILocationTarget;
import io.phanisment.urars.skill.target.INoTarget;
import io.phanisment.urars.util.Location;
import io.phanisment.urars.util.TickScheduler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.text.ParseException;

public class SkillMechanic implements ISkillAction {
	protected final SkillLineConfig config;
	protected long delay;
	@Nullable
	protected SkillTargeter skill_targeter;
	protected String trigger = "";

	public SkillMechanic(SkillLineConfig config) {
		this.config = config;

		this.delay = config.getLong("delay", 0L);

		try {
			parseContext(config.context());
		} catch (ParseException e) {
			throw new RuntimeException(e);
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
				case '~' -> this.trigger = value.toString();
			}
		}
	}

	private void initializeTargeter(BufferedString context) throws ParseException {
		var targeter_parser = new LineParser(context);
		targeter_parser.parse();
		LineConfig targeter_config = targeter_parser.config();
		this.skill_targeter = SkillManager.getTargeter(targeter_config.key()).create(new SkillLineConfig(targeter_config));
	}

	public long getDelay() {
		return this.delay;
	}

	public String getTrigger() {
		return this.trigger;
	}

	public void execute(SkillContext ctx) {
		long total_delay = this.delay + ctx.global_delay;
		if (total_delay > 0L) TickScheduler.wait(total_delay, () -> this.executeAtTarget(ctx));
		else this.executeAtTarget(ctx);
	}

	private void executeAtTarget(SkillContext ctx) {
		if (this.skill_targeter != null) {
			for (Entity target : skill_targeter.getTarget(ctx)) {
				if (this instanceof INoTarget no_target && target == null) no_target.cast(ctx);
				if (this instanceof IEntityTarget entity_target && target != null) entity_target.castAtEntity(ctx, target);
				if (this instanceof ILocationTarget location_target && target != null && skill_targeter.is_location) location_target.castAtLocation(ctx, new Location((ServerLevel)target.level(), target.position()));
			}
		} else if (this instanceof INoTarget no_target) no_target.cast(ctx);
	}
}