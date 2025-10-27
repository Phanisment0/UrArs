package io.phanisment.urars.skill;

import io.phanisment.urars.skill.config.SkillConfigSection;
import io.phanisment.urars.pack.Pack;

import java.util.List;

public class Skill {
	private final String name;
	private final Pack pack;
	private final SkillConfigSection config;
	private final List<SkillCondition> conditions;
	private final List<SkillMechanic> mechanics;
	
	public Skill(String name, Pack pack, SkillConfigSection config) {
		this.name = name;
		this.pack = pack;
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