package io.phanisment.urars.skill.targeters;

import net.minecraft.entity.Entity;

import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillTargeter;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import java.util.Collection;
import java.util.HashSet;

@RegistryInfo(author="Phanisment", key="self", aliases={"caster", "s"})
public class SelfTargeter extends SkillTargeter {
	public SelfTargeter(SkillLineConfig config) {
		super(config);
	}
	
	@Override
	public Collection<Entity> getTarget(SkillContext ctx) {
		Collection<Entity> e = new HashSet<>();
		e.add(ctx.getCaster());
		return e;
	}
}