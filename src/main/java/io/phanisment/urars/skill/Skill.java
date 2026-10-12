package io.phanisment.urars.skill;

import net.minecraft.resources.Identifier;
import io.phanisment.urars.skill.config.SkillConfigSection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Skill {
	public final Identifier id;
	public final File file;
	public final SkillConfigSection config;

	private List<SkillCondition> conditions;
	private MechanicsExecutor mechanics;
	
	public Skill(Identifier id, File file, SkillConfigSection config) {
		this.id = id;
		this.file = file;
		this.config = config;

		try {
			this.conditions = config.getConditions("conditions");
			this.mechanics = config.getMechanics("mechanics");
		} catch (Exception e) {
			this.mechanics = new MechanicsExecutor();
			this.conditions = new ArrayList<>();
		}
	}
	
	public void cast(SkillContext ctx) {
		if (!conditions.stream().allMatch(c -> c.execute(ctx))) return;
		mechanics.execute(ctx);
	}
}