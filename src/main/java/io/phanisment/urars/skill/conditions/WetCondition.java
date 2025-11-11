package io.phanisment.urars.skill.conditions;

import io.phanisment.urars.skill.SkillCondition;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;

@RegistryInfo(author="Phanisment", key="wet", aliases={"iswet"})
public class WetCondition extends SkillCondition {
	public WetCondition(SkillLineConfig config) {
		super(config);
	}
	
	@Override
	public boolean check(SkillContext ctx) {
		return ctx.getCaster().isWet();
	}
}