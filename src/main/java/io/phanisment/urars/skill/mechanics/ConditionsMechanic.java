package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillCondition;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.INoTarget;

import java.util.List;

@RegistryInfo(
		author = "Phanisment",
		key = "conditions"
)
public class ConditionsMechanic extends SkillMechanic implements INoTarget {
	protected final List<SkillCondition> conditions;
	protected final MechanicsExecutor then;
	protected final MechanicsExecutor not;

	public ConditionsMechanic(SkillLineConfig config) {
		super(config);
		conditions = config.getConditions("conditions", "c");
		then = config.getMechanics("then", "t");
		not = config.getMechanics("else", "e");
	}

	@Override
	public void cast(SkillContext ctx) {
		if (conditions.stream().allMatch(c -> c.execute(ctx))) then.execute(ctx);
		else not.execute(ctx);
	}
}