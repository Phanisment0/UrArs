package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.UrArs;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;

@RegistryInfo(
	author = "Phanisment",
	key = "delay"
)
public class DelayMechanic extends SkillMechanic {
	protected long execute_offset = 0L;

	public DelayMechanic(SkillLineConfig config) {
		super(config);
		try {
			this.execute_offset = config.context().toLong();
		} catch (NumberFormatException e) {
			UrArs.LOGGER.error("Failed parse delay argument to long", e);
		}
	}

	@Override
	public void execute(SkillContext ctx) {
		ctx.global_delay += execute_offset;
	}
}