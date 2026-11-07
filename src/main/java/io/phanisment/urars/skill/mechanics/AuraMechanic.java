package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.aura.Aura;
import io.phanisment.urars.skill.config.SkillLineConfig;

@RegistryInfo(
		author = "Phanisment",
		key = "aura"
)
public class AuraMechanic extends Aura {
	public AuraMechanic(SkillLineConfig config) {
		super(config);
	}
}