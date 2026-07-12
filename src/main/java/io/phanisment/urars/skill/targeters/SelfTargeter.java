package io.phanisment.urars.skill.targeters;

import net.minecraft.world.entity.Entity;

import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillTargeter;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import java.util.Collection;
import java.util.HashSet;

@RegistryInfo(author="Phanisment", key="self")
public class SelfTargeter extends SkillTargeter {
	public SelfTargeter(SkillLineConfig config) {
		super(config);
	}
	
	@Override
	public Collection<Entity> getTarget(SkillContext ctx) {
		Collection<Entity> target = new HashSet<>();
		Entity caster = ctx.entity();
		if (caster != null) target.add(caster);
		return target;
	}
}