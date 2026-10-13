package io.phanisment.urars.skill.config;

import io.phanisment.urars.parser.file.config.LineConfig;
import io.phanisment.urars.parser.file.parser.LineParser;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillCondition;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.util.BufferedString;
import io.phanisment.urars.util.UrArsDebugger;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkillLineConfig extends LineConfig {

	public SkillLineConfig(LineConfig copy) {
		super(copy.key(), copy.map(), copy.context());
	}

	public SkillLineConfig(String key, Map<String, BufferedString> arguments, BufferedString context) {
		super(key, arguments, context);
	}

	public MechanicsExecutor getMechanics(String key) throws ParseException {
		MechanicsExecutor list = new MechanicsExecutor();
		for (LineParser line : this.getLine(key)) try {
			var config = new SkillLineConfig(line.config());
			var factory = SkillManager.getMechanic(config.key());
			if (factory == null) {
				UrArsDebugger.warn("Unknown mechanic '{}' in line: {}", config.key(), line);
				continue;
			}
			SkillMechanic action = factory.create(config);
			if (action != null) {
				list.add(action);
				UrArsDebugger.trace("Created mechanic '{}'", config.key());
			}
		} catch (Exception e) {
			UrArsDebugger.error("Failed to create mechanic from line: " + line, e);
		}
		return list;
	}

	public List<SkillCondition> getConditions(String key) throws ParseException {
		List<SkillCondition> list = new ArrayList<>();
		for (LineParser line : this.getLine(key)) try {
			var config = new SkillLineConfig(line.config());
			var factory = SkillManager.getCondition(config.key());
			if (factory == null) {
				UrArsDebugger.warn("Unknown condition '{}' in line: {}", config.key(), line);
				continue;
			}
			SkillCondition action = factory.create(config);
			if (action != null) {
				list.add(action);
				UrArsDebugger.trace("Created condition '{}'", config.key());
			}
		} catch (Exception e) {
			UrArsDebugger.error("Failed to create condition from line: " + line, e);
		}
		return list;
	}
}