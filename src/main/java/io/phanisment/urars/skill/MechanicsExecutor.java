package io.phanisment.urars.skill;

import io.phanisment.urars.UrArs;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.util.UrArsDebugger;
import org.jspecify.annotations.Nullable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class MechanicsExecutor extends AbstractList<SkillMechanic> {
	private final List<SkillMechanic> mechanics = new ArrayList<>();
	private final @Nullable Node skill;

	public MechanicsExecutor() {
		this(null);
	}

	public MechanicsExecutor(@Nullable Node skill) {
		this.skill = skill;
	}

	public void execute(TriggerType event, SkillContext context) {
		execute(event.alias(), context);
	}

	public void execute(String event, SkillContext context) {
		UrArsDebugger.trace("Executing trigger '{}'", event);
		run(context, event);
	}

	public void execute(SkillContext context) {
		run(context, null);
	}

	private void run(SkillContext context, @Nullable String event) {
		if (skill != null) {
			castSkill(context);
			return;
		}

		long delay = 0L;
		try {
			for (SkillMechanic mechanic : mechanics) {
				if (event != null && (mechanic.getTrigger() == null || !event.equalsIgnoreCase(mechanic.getTrigger()))) {
					UrArsDebugger.trace("Skipped '{}': trigger '{}' on event '{}'", mechanic.getClass().getSimpleName(), mechanic.getTrigger(), event);
					continue;
				}

				context.global_delay = delay;
				mechanic.execute(context);
				delay += mechanic.getDelay(context);
			}
		} finally {
			context.global_delay = 0L;
		}
	}

	private void castSkill(SkillContext context) {
		assert skill != null;
		String parsed = skill.evalAsString(context.evalContext());
		Skill skill = SkillManager.getSkill(UrArs.of(parsed));
		if (skill == null) {
			UrArsDebugger.warn("Unknown skill of '{}'", parsed);
			return;
		}
		skill.cast(context);
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
	public void add(int index, SkillMechanic mechanic) {
		mechanics.add(index, mechanic);
		modCount++;
	}

	@Override
	public SkillMechanic remove(int index) {
		SkillMechanic removed = mechanics.remove(index);
		modCount++;
		return removed;
	}
}