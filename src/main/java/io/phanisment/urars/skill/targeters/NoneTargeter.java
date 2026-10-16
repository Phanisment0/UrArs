package io.phanisment.urars.skill.targeters;

import io.phanisment.urars.skill.EntityTargeter;
import net.minecraft.world.entity.Entity;

import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillTargeter;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import java.util.Collection;
import java.util.HashSet;

@RegistryInfo(
	author = "Phanisment",
	key = "none"
)
public class NoneTargeter extends EntityTargeter {
	public NoneTargeter(SkillLineConfig config) {
		super(config);
	}
	
	@Override
	public Collection<Entity> getEntities(SkillContext ctx) {
		return new HashSet<>();
	}
}