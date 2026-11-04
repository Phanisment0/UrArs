package io.phanisment.urars.skill.conditions;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillCondition;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import static io.phanisment.urars.util.ActionUtils.aliases;
import static io.phanisment.urars.util.ActionUtils.aliasNull;

@RegistryInfo(
		author = "Phanisment",
		key = "if"
)
public class IfCondition extends SkillCondition {
	protected final Node condition;

	public IfCondition(SkillLineConfig config) {
		super(config);
		if (config.singleArgument()) condition = config.getMathBoolean(aliasNull(), false);
		else condition = config.getMathBoolean(aliases("condition", "c"), false);
	}
	
	@Override
	public boolean check(SkillContext ctx) {
		return condition.evalAsBoolean(ctx.evalContext());
	}
}