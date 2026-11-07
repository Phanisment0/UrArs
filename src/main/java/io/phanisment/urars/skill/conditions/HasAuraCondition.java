package io.phanisment.urars.skill.conditions;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillCondition;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.aura.AuraManager;
import io.phanisment.urars.skill.config.SkillLineConfig;

@RegistryInfo(
		author = "Phanisment",
		key = "hasAura"
)
public class HasAuraCondition extends SkillCondition {
	protected final Node name;

	public HasAuraCondition(SkillLineConfig config) {
		super(config);
		name = config.getMathString(new String[]{"name","n", "aura", "a"}, "");
	}

	@Override
	public boolean check(SkillContext ctx) {
		return AuraManager.has(ctx.entity(), name.evalAsString(ctx.evalContext()));
	}
}
