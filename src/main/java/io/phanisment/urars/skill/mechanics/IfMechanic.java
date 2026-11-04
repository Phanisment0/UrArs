package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.INoTarget;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		key = "if"
)
public class IfMechanic extends SkillMechanic implements INoTarget {
	protected final Node condition;
	protected final MechanicsExecutor then;
	protected final MechanicsExecutor not;

	public IfMechanic(SkillLineConfig config) {
		super(config);
		condition = config.getMathBoolean(aliases("condition", "c"), false);
		then = config.getMechanics("then", "t");
		not = config.getMechanics("else", "e");
	}

	@Override
	public void cast(SkillContext ctx) {
		if (condition.evalAsBoolean(ctx.evalContext())) then.execute(ctx);
		else not.execute(ctx);
	}
}
