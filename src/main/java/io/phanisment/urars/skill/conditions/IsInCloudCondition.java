package io.phanisment.urars.skill.conditions;

import io.phanisment.urars.skill.SkillCondition;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;

@RegistryInfo(
		author = "Phanisment",
		key = "isInClouds",
		aliases = { "inClouds" }
)
public class IsInCloudCondition extends SkillCondition {
	public IsInCloudCondition(SkillLineConfig config) {
		super(config);
	}
	
	@Override
	public boolean check(SkillContext ctx) {
		return ctx.entity().isInClouds();
	}
}