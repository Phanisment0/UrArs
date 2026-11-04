package io.phanisment.urars.skill.conditions;

import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.variable.VariableCondition;

@RegistryInfo(
		author = "Phanisment",
		key = "variableIsSet",
		aliases = { "varIsSet" }
)
public class VariableIsSetCondition extends VariableCondition {
	public VariableIsSetCondition(SkillLineConfig config) {
		super(config);
	}

	@Override
	public boolean check(SkillContext ctx) {
		if (global.evalAsBoolean(ctx.evalContext())) return ctx.global().containsKey(name);
		return ctx.containsVar(name);
	}
}