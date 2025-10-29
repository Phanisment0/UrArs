package io.phanisment.urars.skill;

import net.minecraft.util.Identifier;
import net.minecraft.resource.Resource;

import io.phanisment.urars.skill.config.SkillConfigSection;

import java.util.List;

public class Skill {
	private final Identifier id;
	private final Resource resource;
	private final SkillConfigSection config;
	private final List<SkillCondition> conditions;
	private final List<SkillMechanic> mechanics;
	
	public Skill(Identifier id, Resource resource, SkillConfigSection config) {
		this.id = id;
		this.resource = resource;
		this.config = config;
		
		this.conditions = config.getConditions("conditions");
		this.mechanics = config.getMechanics("skills");
	}
	
	public void cast(SkillContext ctx) {
		for (SkillCondition condition : conditions) {
			boolean result = condition.execute(ctx);
			if (!result) return;
		}
		
		for (SkillMechanic mechanic : mechanics) {
			mechanic.execute(ctx);
		}
	}
}