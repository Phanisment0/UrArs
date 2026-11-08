package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.template.particle.AbstractParticleMechanic;

@RegistryInfo(
		author = "Phanisment",
		key = "particle",
		namespace = "fx"
)
public class ParticleMechanic extends AbstractParticleMechanic {

	public ParticleMechanic(SkillLineConfig config) {
		super(config);
	}

	@Override
	protected void emit(Cast cast) {
		cast.spawn(cast.origin());
	}
}