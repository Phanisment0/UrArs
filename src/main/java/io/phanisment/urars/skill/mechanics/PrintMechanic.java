package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.UrArs;
import io.phanisment.urars.exception.MathParserException;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.INoTarget;

@RegistryInfo(
		author = "Phanisment",
		key = "print"
)
public class PrintMechanic extends SkillMechanic implements INoTarget {
	protected Node message;
	
	public PrintMechanic(SkillLineConfig config) {
		super(config);
		try {
			this.message = config.getMathString("message", "msg", "m");
		} catch (MathParserException e) {
			UrArs.LOGGER.error("Failed to parse arguments", e);
		}
	}
	
	@Override
	public void cast(SkillContext ctx) {
		System.out.println(message.evalAsString(ctx.evalContext()));
	}
}
