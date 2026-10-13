package io.phanisment.urars.skill.config;

import io.phanisment.urars.parser.file.config.ConfigSection;
import io.phanisment.urars.parser.file.parser.LineParser;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillCondition;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.util.BufferedString;

import static io.phanisment.urars.UrArs.LOGGER;

import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class SkillConfigSection extends ConfigSection {
	public SkillConfigSection(Map<String, BufferedString> map) {
		super(map);
	}

	public MechanicsExecutor getMechanics(String key) throws ParseException {
		return this.getMechanics(key, new MechanicsExecutor());
	}

	public MechanicsExecutor getMechanics(String key, MechanicsExecutor def) throws ParseException {
		List<LineParser> list = this.getLine(key);
		if (list == null || list.isEmpty()) return def;

		var mechanics = new MechanicsExecutor();

		for (LineParser line : list) try {
			var config = new SkillLineConfig(line.config());
			SkillMechanic action = SkillManager.getMechanic(config.key()).create(config);
			mechanics.add(action);
		} catch (Exception e) {
			LOGGER.warn("Failed to create mechanic", e);
		}
		return mechanics;
	}

	public List<SkillCondition> getConditions(String key) throws ParseException {
		return this.getConditions(key, new ArrayList<>());
	}

	public List<SkillCondition> getConditions(String key, List<SkillCondition> def) throws ParseException {
		List<LineParser> list = this.getLine(key);
		if (list == null || list.isEmpty()) return def;

		List<SkillCondition> conditions = new ArrayList<>();

		for (LineParser line : list) try {
			var config = new SkillLineConfig(line.config());
			SkillCondition action = SkillManager.getCondition(config.key()).create(config);
			conditions.add(action);
		} catch (Exception e) {
			LOGGER.warn("Failed to create condition", e);
		}
		return conditions;
	}
}