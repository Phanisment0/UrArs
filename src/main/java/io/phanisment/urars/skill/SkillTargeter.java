package io.phanisment.urars.skill;

import net.minecraft.entity.Entity;

import io.phanisment.urars.skill.config.SkillLineConfig;
import java.util.Collection;

public abstract class SkillTargeter {
	protected final SkillLineConfig config;
	
	public SkillTargeter(SkillLineConfig config) {
		this.config = config;
	}
	
	public abstract Collection<Entity> getTarget(SkillContext ctx);
}