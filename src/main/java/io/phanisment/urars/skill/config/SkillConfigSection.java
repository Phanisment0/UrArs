package io.phanisment.urars.skill.config;

import io.phanisment.urars.config.LineConfig;
import io.phanisment.urars.config.ConfigSection;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.SkillCondition;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class SkillConfigSection extends ConfigSection {
	public List<SkillMechanic> getMechanics(String key) {
		return this.getMechanics(key, new ArrayList<>());
	}
	
	public List<SkillMechanic> getMechanics(String key, List<SkillMechanic> def) {
		if (this.get(key) instanceof List<?> list) {
			List<SkillMechanic> result = new ArrayList<>();
			for (Object value : list) {
				if (!(value instanceof String s)) continue;
				var line = new SkillLineConfig(s);
				try {
					SkillManager.getMechanic(line).ifPresent(mechanic -> result.add(mechanic));
				} catch (Exception e) {
					e.printStackTrace();
				}
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
					SkillManager.getCondition(line).ifPresent(condition -> result.add(condition));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return result;
		}
		return def;
	}
}