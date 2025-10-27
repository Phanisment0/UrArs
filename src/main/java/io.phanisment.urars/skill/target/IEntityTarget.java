package io.phanisment.urars.skill.target;

import net.minecraft.entity.Entity;
import io.phanisment.urars.skill.SkillContext;

public interface IEntityTarget {
	public void castAtEntity(SkillContext ctx, Entity target);
}