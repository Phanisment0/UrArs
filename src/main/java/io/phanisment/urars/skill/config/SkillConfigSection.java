package io.phanisment.urars.skill.config;

import io.phanisment.urars.UrArs;
import io.phanisment.urars.parser.file.LineParser;
import io.phanisment.urars.parser.file.config.ConfigSection;
import io.phanisment.urars.parser.file.config.LineConfig;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillCondition;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.util.ActionUtils;
import io.phanisment.urars.parser.BufferedString;
import org.jspecify.annotations.Nullable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkillConfigSection extends ConfigSection {

	public SkillConfigSection(Map<BufferedString, Object> map) {
		super(map);
	}

	public @Nullable List<LineConfig> getLine(String key) {
		try {
			return toLineParsers(getListBufferedString(key));
		} catch (ParseException e) {
			UrArs.LOGGER.error("Failed to parse line", e);
			return null;
		}
	}

	public MechanicsExecutor getMechanics(String key) {
		return getMechanics(key, new MechanicsExecutor());
	}

	public MechanicsExecutor getMechanics(String key, MechanicsExecutor def) {
		List<LineConfig> lines = getLine(key);
		if (lines == null) return def;

		MechanicsExecutor executor = new MechanicsExecutor();
		executor.addAll(ActionUtils.createAll(lines, "mechanic", config -> {
			var factory = SkillManager.getMechanic(config.key());
			return factory == null ? null : factory.create(config);
		}));
		return executor;
	}

	public List<SkillCondition> getConditions(String key) {
		return getConditions(key, new ArrayList<>());
	}

	public List<SkillCondition> getConditions(String key, List<SkillCondition> def) {
		List<LineConfig> lines = getLine(key);
		if (lines == null) return def;

		return ActionUtils.createAll(lines, "condition", config -> {
			var factory = SkillManager.getCondition(config.key());
			return factory == null ? null : factory.create(config);
		});
	}

	protected List<LineConfig> toLineParsers(List<BufferedString> items) throws ParseException {
		List<LineConfig> result = new ArrayList<>(items.size());
		for (BufferedString item : items) {
			var parser = LineParser.parse(item);
			result.add(parser);
		}
		return result;
	}
}