package io.phanisment.urars.skill.template.variable;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.config.SkillLineConfig;
import org.jspecify.annotations.Nullable;

import static io.phanisment.urars.util.ActionUtils.aliases;

public class VariableMechanic  extends SkillMechanic {
	protected final @Nullable String name;
	protected final Node global;

	public VariableMechanic(SkillLineConfig config) {
		super(config);
		this.name = config.getString(aliases("name", "n"), null);
		this.global = config.getMathBoolean(aliases("global", "g"), false);
	}
}