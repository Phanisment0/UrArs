package io.phanisment.urars.skill;

import io.phanisment.urars.skill.config.SkillLineConfig;
import java.util.Collection;
import java.util.Optional;

public abstract class SkillCondition {
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
		switch (type) {
			case "true":
				return result;
			case "false":
				return !result;
			case "cast":
				if (part.length > 1) SkillManager.getSkill(part[1]).ifPresent(skill -> skill.cast(ctx));
				return result;
			case "castinstead":
				if (result && part.length > 1) SkillManager.getSkill(part[1]).ifPresent(skill -> skill.cast(ctx));
				return false;
			case "orelse":
				if (!result && part.length > 1) SkillManager.getSkill(part[1]).ifPresent(skill -> skill.cast(ctx));
				return false;
			default:
				System.out.println("Unknown condition action");
		}
		
		return result;
	}
}