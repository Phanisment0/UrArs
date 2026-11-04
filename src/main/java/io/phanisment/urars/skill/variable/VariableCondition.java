package io.phanisment.urars.skill.variable;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillCondition;
import io.phanisment.urars.skill.config.SkillLineConfig;
import org.jspecify.annotations.Nullable;

import static io.phanisment.urars.util.ActionUtils.aliases;

public abstract class VariableCondition extends SkillCondition {
	protected final @Nullable String name;
	protected final Node global;

	public VariableCondition(SkillLineConfig config) {
		super(config);
		name = config.getString(aliases("name", "n"), null);
		global = config.getMathBoolean(aliases("global", "g"), false);
	}
}
