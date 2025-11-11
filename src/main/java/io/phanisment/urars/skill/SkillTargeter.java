package io.phanisment.urars.skill;

import net.minecraft.entity.Entity;

import io.phanisment.urars.skill.config.SkillLineConfig;
import java.util.Collection;

public abstract class SkillTargeter implements ISkillAction {
	protected final SkillLineConfig config;
	protected boolean isLocation = false;
	
	public SkillTargeter(SkillLineConfig config) {
		this.config = config;
	}
	
	public abstract Collection<Entity> getTarget(SkillContext ctx);
}