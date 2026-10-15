package io.phanisment.urars.skill;

import java.util.Map;

import io.phanisment.urars.parser.math.eval.EvalContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;

public class SkillContext {
	private final Entity entity;
	private final EvalContext eval_context;

	public long global_delay = 0;
	
	public SkillContext(Entity entity) {
		this(entity, EvalContext.withStandardLibrary());
	}
	
	public SkillContext(Entity entity, EvalContext eval_context) {
		this.entity = entity;
		this.eval_context = eval_context;
	}

	public boolean isPlayer() {
		return entity instanceof Player;
	}
	
	public Entity entity() {
		return entity;
	}

	public LivingEntity livingEntity() {
		if (entity instanceof LivingEntity e) return entity.asLivingEntity();
		throw new IllegalArgumentException("Unknown Entity");
	}

	public EvalContext evalContext() {
		return eval_context;
	}
}