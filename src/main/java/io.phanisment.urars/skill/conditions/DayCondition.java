package io.phanisment.urars.skill.conditions;

import io.phanisment.urars.skill.SkillCondition;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.ConditionInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;

@ConditionInfo(author="Phanisment", key="day")
public class DayCondition extends SkillCondition {
	public DayCondition(SkillLineConfig config) {
		super(config);
	}
	
	@Override
	public boolean check(SkillContext ctx) {
		return ctx.getCaster().getWorld().isDay();
	}
}