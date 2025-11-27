package io.phanisment.urars.skill.targeters;

import net.minecraft.entity.Entity;

import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillTargeter;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import java.util.Collection;
import java.util.HashSet;

@RegistryInfo(author="Phanisment", key="none")
public class NoneTargeter extends SkillTargeter {
	public NoneTargeter(SkillLineConfig config) {
		super(config);
	}
	
	@Override
	public Collection<Entity> getTarget(SkillContext ctx) {
		return new HashSet<>();
	}
}