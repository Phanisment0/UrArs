package io.phanisment.urars.skill.targeters;

import java.util.Collection;

import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillTargeter;
import io.phanisment.urars.skill.config.SkillLineConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

public class EntityInNearTargeter extends SkillTargeter {
	private final Double radius;

	public EntityInNearTargeter(SkillLineConfig config) {
		super(config);
		this.radius = config.getDouble(new String[]{"radius", "r"}, 5d);
	}

	@Override
	public Collection<Entity> getTarget(SkillContext ctx) {
		Entity caster = ctx.getCaster();
		if (caster == null) return null;
		AABB area = caster.getBoundingBox().inflate(radius);
		return caster.level().getEntities(caster, area, entity -> entity.isAlive());
	}
}
