package io.phanisment.urars.skill;

import java.util.Map;

import net.minecraft.world.entity.Entity;

import java.util.HashMap;

public class SkillContext {
	private final Entity entity;
	private final Map<String, Object> variables;

	public long global_delay = 0;
	
	public SkillContext(Entity entity) {
		this(entity, new HashMap<>());
	}
	
	public SkillContext(Entity entity, Map<String, Object> variables) {
		this.entity = entity;
		this.variables = variables;
	}
	
	public Entity entity() {
		return entity;
	}

	public Map<String, Object> variables() {
		return variables;
	}
}