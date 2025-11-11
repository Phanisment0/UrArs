package io.phanisment.urars.skill;

import net.minecraft.util.Identifier;
import net.minecraft.resource.Resource;

import io.phanisment.urars.skill.config.SkillConfigSection;
import io.phanisment.urars.skill.mechanics.DelayMechanic;

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
		this.mechanics = config.getMechanics("mechanics");
	}
	
	public void cast(SkillContext ctx) {
		for (SkillCondition condition : conditions) {
			boolean result = condition.execute(ctx);
			if (!result) return;
		}
		
		long delay = 0l;
		for (SkillMechanic mechanic : mechanics) {
			if (mechanic instanceof DelayMechanic delay_mechanic) {
				delay += delay_mechanic.getDelay();
				continue;
			}
			if (delay > 0l) mechanic.addDelay(delay);
			mechanic.execute(ctx);
		}
	}
}