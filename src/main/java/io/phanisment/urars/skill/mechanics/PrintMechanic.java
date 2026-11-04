package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.INoTarget;

import static io.phanisment.urars.util.ActionUtils.aliasNull;

@RegistryInfo(
		author = "Phanisment",
		key = "print"
)
public class PrintMechanic extends SkillMechanic implements INoTarget {
	protected final Node message;
	
	public PrintMechanic(SkillLineConfig config) {
		super(config);
		if (config.singleArgument()) message = config.getMathString(aliasNull());
		else message = config.getMathString("message", "msg", "m");
	}
	
	@Override
	public void cast(SkillContext ctx) {
		System.out.println(message.evalAsString(ctx.evalContext()));
	}
}
