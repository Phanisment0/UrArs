package io.phanisment.urars.skill;

import net.minecraft.entity.Entity;

import java.util.Map;
import java.util.HashMap;

public class SkillContext {
	private final Entity entity;
	private final Map<String, Object> variables;
	
	public SkillContext(Entity entity) {
		this(entity, new HashMap<>());
	}
	
	public SkillContext(Entity entity, Map<String, Object> variables) {
		this.entity = entity;
		this.variables = variables;
	}
	
	public Entity getCaster() {
		return this.entity;
	}
}