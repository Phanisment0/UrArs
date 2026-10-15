package io.phanisment.urars.skill;

import io.phanisment.urars.UrArs;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.util.UrArsDebugger;
import org.jspecify.annotations.Nullable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class MechanicsExecutor extends AbstractList<SkillMechanic> {
	private final List<SkillMechanic> mechanics;
	private final Node skill_id;

	private long global_delay = 0L;

	public MechanicsExecutor() {
		this(null);
	}

	public MechanicsExecutor(Node skill_id) {
		this.skill_id = skill_id;
		mechanics = new ArrayList<>();
	}

	public void execute(TriggerType event, SkillContext context) {
		execute(event.alias(), context);
	}

	public void execute(String event, SkillContext context) {
		if (skill_id != null) {
			String parsed = skill_id.evalAsString(context.evalContext());
			Skill skill_meta = SkillManager.getSkill(UrArs.of(parsed));
			if (skill_meta == null) {
				UrArsDebugger.warn("Unknown skill of '{}'", parsed);
				return;
			}
			skill_meta.cast(context);
			return;
		}

		for (var mechanic : mechanics) {
			context.global_delay = global_delay;
			if (event.equalsIgnoreCase(mechanic.getTrigger())) {
				mechanic.execute(context);
				global_delay += mechanic.getDelay();
			}
		}
		context.global_delay = 0L;
	}

	public void execute(SkillContext context) {
		if (skill_id != null) {
			String parsed = skill_id.evalAsString(context.evalContext());
			Skill skill_meta = SkillManager.getSkill(UrArs.of(parsed));
			if (skill_meta == null) {
				UrArsDebugger.warn("Unknown skill of '{}'", parsed);
				return;
			}
			skill_meta.cast(context);
			return;
		}

		for (var mechanic : mechanics) {
			context.global_delay = global_delay;
			mechanic.execute(context);
			global_delay += mechanic.getDelay();
		}
		context.global_delay = 0L;
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