package io.phanisment.urars.skill.config;

import io.phanisment.urars.config.LineConfig;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkillLineConfig extends LineConfig {
	
	public SkillLineConfig(String line_raw, String key, String context, Map<String, String> args) {
		super(line_raw, key, context, args);
	}
	
	public SkillLineConfig(String line) {
		super(line);
	}
	
	public MechanicsExecutor getMechanics(String key) {
		MechanicsExecutor list = new MechanicsExecutor();
		for (LineConfig lc : this.getLineList(key)) {
			try {
				var action = SkillManager.getMechanic(lc.getAsSkillLine());
				if (action != null) list.add(action);
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
				var action = SkillManager.getCondition(lc.getAsSkillLine());
				if (action != null) list.add(action);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
}