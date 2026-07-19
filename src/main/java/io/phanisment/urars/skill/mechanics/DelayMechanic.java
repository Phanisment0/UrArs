package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;

@RegistryInfo(
	author = "Phanisment",
	key = "delay"
)
public class DelayMechanic extends SkillMechanic {
	private Long execute_offset = 0l;

	public DelayMechanic(SkillLineConfig config) {
		super(config);
		try {
			this.execute_offset = Long.parseLong(config.getContext());
		} catch (NumberFormatException e) {
			// Ignored
		}
	}

	@Override
	public void execute(SkillContext ctx) {
		ctx.global_delay += execute_offset;
	}
}