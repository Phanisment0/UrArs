package io.phanisment.urars.skill;

import net.minecraft.util.Identifier;

import io.phanisment.urars.skill.config.SkillLineConfig;

import static io.phanisment.urars.UrArs.LOGGER;

public abstract class SkillCondition implements ISkillAction {
	private final SkillLineConfig config;
	
	public SkillCondition(SkillLineConfig config) {
		this.config = config;
	}
	
	public abstract boolean check(SkillContext ctx);
	
	public boolean execute(SkillContext ctx) {
		boolean result = check(ctx);
		String context = config.getContext();
		if (context == null || context.isEmpty()) return result;
		
		String[] part = context.split(" ", 2);
		String type = part[0].toLowerCase();
		var skill_id = Identifier.of(part[1]);
		switch (type) {
			case "true":
				return result;
			case "false":
				return !result;
			case "cast":
				if (part.length > 1) SkillManager.getSkill(skill_id).ifPresent(skill -> skill.cast(ctx));
				return result;
			case "castinstead":
				if (result && part.length > 1) SkillManager.getSkill(skill_id).ifPresent(skill -> skill.cast(ctx));
				return false;
			case "orelse":
				if (!result && part.length > 1) SkillManager.getSkill(skill_id).ifPresent(skill -> skill.cast(ctx));
				return false;
			default:
				LOGGER.warn("Unknown condition action of {}", type);
		}
		
		return result;
	}
}