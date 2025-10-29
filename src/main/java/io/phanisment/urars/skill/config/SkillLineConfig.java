package io.phanisment.urars.skill.config;

import io.phanisment.urars.config.LineConfig;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.SkillTargeter;
import io.phanisment.urars.skill.SkillCondition;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Map;

public class SkillLineConfig extends LineConfig {
	
	public SkillLineConfig(String line_raw, String key, String context, Map<String, String> args) {
		super(line_raw, key, context, args);
	}
	
	public SkillLineConfig(String line) {
		super(line);
	}
	
	public List<SkillMechanic> getMechanics(String key) {
		List<SkillMechanic> list = new ArrayList<>();
		for (LineConfig lc : this.getLineList(key)) {
			try {
				Optional<SkillMechanic> mechanic = SkillManager.getMechanic(lc.getAsSkillLine());
				if (mechanic.isPresent()) list.add(mechanic.get());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public List<SkillCondition> getConditions(String key) {
		List<SkillCondition> list = new ArrayList<>();
		for (LineConfig lc : this.getLineList(key)) {
			try {
				Optional<SkillCondition> condition = SkillManager.getCondition(lc.getAsSkillLine());
				if (condition.isPresent()) list.add(condition.get());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
}