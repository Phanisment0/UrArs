package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import io.phanisment.urars.skill.variable.VariableMechanic;
import net.minecraft.world.entity.Entity;


@RegistryInfo(
		author = "Phanisment",
		key = "unsetVariable",
		aliases = {
				"unsetVar",
				"removeVariable",
				"removeVar"
		}
)
public class VariableUnsetMechanic extends VariableMechanic implements IEntityTarget {
	public VariableUnsetMechanic(SkillLineConfig config) {
		super(config);
	}

	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		if (global.evalAsBoolean(ctx.evalContext())) ctx.global().remove(name);
		else ctx.removeVar(name);
	}
}