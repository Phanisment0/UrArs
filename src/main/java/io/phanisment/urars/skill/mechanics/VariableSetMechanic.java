package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import io.phanisment.urars.skill.variable.VariableMechanic;

import static io.phanisment.urars.util.ActionUtils.aliases;

import net.minecraft.world.entity.Entity;

@RegistryInfo(
		author = "Phanisment",
		key = "setVariable",
		aliases = { "setVar" }
)
public class VariableSetMechanic extends VariableMechanic implements IEntityTarget {
	protected final Node value;

	public VariableSetMechanic(SkillLineConfig config) {
		super(config);
		value = config.getMathBoolean(aliases("value", "v"));
	}

	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		if (global.evalAsBoolean(ctx.evalContext())) ctx.global().put(name, value.eval(ctx.evalContext()));
		else ctx.setVar(name, value.eval(ctx.evalContext()));
	}
}