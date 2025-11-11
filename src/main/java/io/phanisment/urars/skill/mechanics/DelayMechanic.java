package io.phanisment.urars.skill.mechanics;

import net.minecraft.text.Text;
import net.minecraft.entity.player.PlayerEntity;

import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;

@RegistryInfo(author="Phanisment", key="delay")
public class DelayMechanic extends SkillMechanic {
	public DelayMechanic(SkillLineConfig config) {
		super(config);
		try {
			this.delay += Long.parseLong(config.getContext());
		} catch (NumberFormatException e) {
		}
	}
}