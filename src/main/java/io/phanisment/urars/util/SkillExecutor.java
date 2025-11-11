package io.phanisment.urars.util;

import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.TriggerType;
import io.phanisment.urars.skill.mechanics.DelayMechanic;

import java.util.List;

public final class SkillExecutor {
	private final List<SkillMechanic> mechanics;
	private final SkillContext context;
	
	public SkillExecutor(List<SkillMechanic> mechanics, SkillContext context) {
		this.mechanics = mechanics;
		this.context = context;
	}
	
	public void execute() {
		long delay = 0l;
		for (SkillMechanic mechanic : mechanics) {
			if (mechanic instanceof DelayMechanic delay_mechanic) {
				delay += delay_mechanic.getDelay();
				continue;
			}
			if (delay > 0l) mechanic.addDelay(delay);
			mechanic.execute(context);
		}
	}
	
	public void execute(TriggerType trigger) {
		this.execute(trigger.alias());
	}
	
	public void execute(String trigger) {
		long delay = 0l;
		for (SkillMechanic mechanic : mechanics) {
			if (!trigger.equalsIgnoreCase(mechanic.getTrigger())) continue;
			if (mechanic instanceof DelayMechanic delay_mechanic) {
				delay += delay_mechanic.getDelay();
				continue;
			}
			if (delay > 0l) mechanic.addDelay(delay);
			mechanic.execute(context);
		}
	}
}