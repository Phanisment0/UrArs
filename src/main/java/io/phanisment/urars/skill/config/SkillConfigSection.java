package io.phanisment.urars.skill.config;

import io.phanisment.urars.config.ConfigSection;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillCondition;
import java.util.List;
import java.util.ArrayList;

public class SkillConfigSection extends ConfigSection {
	public MechanicsExecutor getMechanics(String key) {
		return this.getMechanics(key, new MechanicsExecutor());
	}
	
	public MechanicsExecutor getMechanics(String key, MechanicsExecutor def) {
		if (this.get(key) instanceof List<?> list) {
			var result = new MechanicsExecutor();
			for (Object value : list) {
				if (!(value instanceof String s)) continue;
				var line = new SkillLineConfig(s);
				try {
					var action = SkillManager.getMechanic(line);
					if (action != null) result.add(action);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return result;
		} else if (this.get(key) instanceof String value) {
			var result = new MechanicsExecutor();
			var line = new SkillLineConfig(value);
			try {
				var action = SkillManager.getMechanic(line);
				if (action != null) result.add(action);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
		return def;
	}
	
	public List<SkillCondition> getConditions(String key) {
		return this.getConditions(key, new ArrayList<>());
	}
	
	public List<SkillCondition> getConditions(String key, List<SkillCondition> def) {
		if (this.get(key) instanceof List<?> list) {
			List<SkillCondition> result = new ArrayList<>();
			for (Object value : list) {
				if (!(value instanceof String s)) continue;
				var line = new SkillLineConfig(s);
				try {
					var action = SkillManager.getCondition(line);
					if (action != null) result.add(action);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return result;
		} else if (this.get(key) instanceof String value) {
			var line = new SkillLineConfig(value);
			List<SkillCondition> result = new ArrayList<>();
			try {
				var action = SkillManager.getCondition(line);
				if (action != null) result.add(action);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
		return def;
	}
}