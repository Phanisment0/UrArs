package io.phanisment.urars.skill;

import net.minecraft.server.packs.resources.Resource;
import net.minecraft.resources.Identifier;
import io.phanisment.urars.skill.config.SkillConfigSection;

import java.text.ParseException;
import java.util.List;

public class Skill {
	public final Identifier id;
	public final Resource resource;
	public final SkillConfigSection config;
	
	private List<SkillCondition> conditions;
	private MechanicsExecutor mechanics;
	
	public Skill(Identifier id, Resource resource, SkillConfigSection config) {
		this.id = id;
		this.resource = resource;
		this.config = config;
		
		try {
			this.conditions = config.getConditions("conditions");
			this.mechanics = config.getMechanics("mechanics");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void cast(SkillContext ctx) {
		if (!conditions.stream().allMatch(c -> c.execute(ctx))) return;
		mechanics.execute(ctx);
	}
}