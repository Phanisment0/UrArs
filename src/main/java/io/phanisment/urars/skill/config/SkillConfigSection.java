package io.phanisment.urars.skill.config;

import io.phanisment.urars.parser.file.config.ConfigSection;
import io.phanisment.urars.parser.file.parser.LineParser;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillCondition;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.util.ActionUtils;
import io.phanisment.urars.util.BufferedString;
import io.phanisment.urars.util.UrArsDebugger;
import org.jspecify.annotations.Nullable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkillConfigSection extends ConfigSection {

	public SkillConfigSection(Map<String, BufferedString> map) {
		super(map);
	}

	public MechanicsExecutor getMechanics(String key) throws ParseException {
		return getMechanics(key, new MechanicsExecutor());
	}

	public MechanicsExecutor getMechanics(String key, MechanicsExecutor def) throws ParseException {
		List<LineParser> lines = getLine(key);
		if (lines == null || lines.isEmpty()) return def;

		MechanicsExecutor executor = new MechanicsExecutor();
		executor.addAll(ActionUtils.createAll(lines, "mechanic", config -> {
			var factory = SkillManager.getMechanic(config.key());
			return factory == null ? null : factory.create(config);
		}));
		return executor;
	}

	public List<SkillCondition> getConditions(String key) throws ParseException {
		return getConditions(key, new ArrayList<>());
	}

	public List<SkillCondition> getConditions(String key, List<SkillCondition> def) throws ParseException {
		List<LineParser> lines = getLine(key);
		if (lines == null || lines.isEmpty()) return def;

		return ActionUtils.createAll(lines, "condition", config -> {
			var factory = SkillManager.getCondition(config.key());
			return factory == null ? null : factory.create(config);
		});
	}
}