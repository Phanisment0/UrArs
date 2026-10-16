package io.phanisment.urars.skill;

import io.phanisment.urars.parser.math.eval.EvalContext;
import io.phanisment.urars.parser.math.eval.EvalContextImpl;
import io.phanisment.urars.skill.value.EntityValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

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

		((EvalContextImpl)this.eval_context).put("caster", new EntityValue(entity));
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