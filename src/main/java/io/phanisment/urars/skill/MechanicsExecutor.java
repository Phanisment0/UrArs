package io.phanisment.urars.skill;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class MechanicsExecutor extends AbstractList<SkillMechanic> {
	private List<SkillMechanic> mechanics;

	private long global_delay = 0l;

	public MechanicsExecutor() {
		this.mechanics = new ArrayList<>();
	}

	public void execute(TriggerType event, SkillContext context) {
		execute(event.alias(), context);
	}

	public void execute(String event, SkillContext context) {
		for (var mechanic : mechanics) {
			context.global_delay = global_delay;
			if (event.equalsIgnoreCase(mechanic.getTrigger())) {
				mechanic.execute(context);
				global_delay += mechanic.getDelay();
			}
		}
		context.global_delay = 0l;
	}

	public void execute(SkillContext context) {
		for (var mechanic : mechanics) {
			context.global_delay = global_delay;
			mechanic.execute(context);
			global_delay += mechanic.getDelay();
		}
		context.global_delay = 0l;
	}

	@Override
	public int size() {
		return mechanics.size();
	}

	@Override
	public SkillMechanic get(int index) {
		return mechanics.get(index);
	}

	@Override
	public SkillMechanic set(int index, SkillMechanic mechanic) {
		return mechanics.set(index, mechanic);
	}

	@Override
	public boolean add(SkillMechanic mechanic) {
		return mechanics.add(mechanic);
	}

	@Override
	public SkillMechanic remove(int index) {
		return mechanics.remove(index);
	}
}