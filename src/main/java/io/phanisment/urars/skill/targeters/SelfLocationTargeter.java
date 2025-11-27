package io.phanisment.urars.skill.targeters;

import net.minecraft.entity.Entity;

import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillTargeter;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import java.util.Collection;
import java.util.HashSet;

@RegistryInfo(author="Phanisment", key="selflocation")
public class SelfLocationTargeter extends SkillTargeter {
	public SelfLocationTargeter(SkillLineConfig config) {
		super(config);
		this.is_location = true;
	}
	
	@Override
	public Collection<Entity> getTarget(SkillContext ctx) {
		Collection<Entity> target = new HashSet<>();
		Entity caster = ctx.getCaster();
		if (caster != null) target.add(caster);
		return target;
	}
}