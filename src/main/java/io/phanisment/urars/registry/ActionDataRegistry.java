package io.phanisment.urars.registry;

import io.phanisment.urars.skill.ISkillAction;
import io.phanisment.urars.skill.SkillActionFactory;

public class ActionDataRegistry<T extends ISkillAction> extends AnnotatedRegistry<T, SkillActionFactory<T>> {
}