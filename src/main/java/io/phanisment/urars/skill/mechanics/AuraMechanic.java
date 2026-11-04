package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import net.minecraft.world.entity.Entity;

@RegistryInfo(
		author = "Phanisment",
		key = "aura"
)
public class AuraMechanic extends SkillMechanic implements IEntityTarget {

	public AuraMechanic(SkillLineConfig config) {
		super(config);
	}

	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {

	}
}