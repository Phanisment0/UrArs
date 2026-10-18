package io.phanisment.urars.skill;

import io.phanisment.urars.skill.config.SkillLineConfig;

@FunctionalInterface
public interface SkillActionFactory<T extends ISkillAction> {
	T create(SkillLineConfig config);
}