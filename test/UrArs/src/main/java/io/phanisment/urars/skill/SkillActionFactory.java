package io.phanisment.urars.skill;

import io.phanisment.urars.skill.config.SkillLineConfig;

@FunctionalInterface
public interface SkillActionFactory<T extends ISkillAction> {
	default boolean validation() {
		return true;
	}
	T create(SkillLineConfig config);
}