package io.phanisment.urars.skill.conditions;

import io.phanisment.urars.skill.SkillCondition;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;

@RegistryInfo(author="Phanisment", key="raining")
public class RainingCondition extends SkillCondition {
	public RainingCondition(SkillLineConfig config) {
		super(config);
	}
	
	@Override
	public boolean check(SkillContext ctx) {
		return ctx.getCaster().getWorld().isRaining();
	}
}