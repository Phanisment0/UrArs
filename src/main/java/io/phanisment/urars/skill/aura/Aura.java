package io.phanisment.urars.skill.aura;

import io.phanisment.urars.skill.SkillMechanic;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class Aura {
	private long duration = 0l;
	private int interval = 0;
	private final List<SkillMechanic> on_tick;
	private final List<SkillMechanic> on_end;
	public int stack = 1;
	public int max_stack = 1;
	public boolean remove_on_death = true;
	public boolean remove_on_leave = true;
	public boolean run_when_remove = true; // execute on_end when removed
	
	public Aura(long duration, int interval, List<SkillMechanic> on_tick, List<SkillMechanic> on_end) {
		this.duration = duration;
		this.interval = interval;
		this.on_tick = on_tick;
		this.on_end = on_end;
	}
}