package io.phanisment.urars.skill;

import net.minecraft.entity.Entity;

import io.phanisment.urars.skill.config.SkillLineConfig;
import java.util.Collection;

public abstract class SkillTargeter implements ISkillAction {
	protected final SkillLineConfig config;
	protected boolean is_location = false;
	
	public SkillTargeter(SkillLineConfig config) {
		this.config = config;
	}
	
	public Collection<Location> getLocation(SkillContext ctx) {
		
	}
	
	public Collection<Entity> getTarget(SkillContext ctx) {
	}
}