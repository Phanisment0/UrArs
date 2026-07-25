package io.phanisment.urars.skill.config;

import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.lib.file_parser.config.ConfigSection;
import io.phanisment.urars.lib.file_parser.parser.LineParser;
import io.phanisment.urars.lib.file_parser.util.BufferedString;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillCondition;
import java.util.List;
import java.util.Map;
import java.text.ParseException;
import java.util.ArrayList;

public class SkillConfigSection extends ConfigSection {
	public SkillConfigSection(ConfigSection section) {
		super(section.map());
	}

	public SkillConfigSection(Map<BufferedString, BufferedString> map) {
		super(map);
	}

	public MechanicsExecutor getMechanics(String key) throws ParseException {
		return this.getMechanics(key, new MechanicsExecutor());
	}
	
	public MechanicsExecutor getMechanics(String key, MechanicsExecutor def) throws ParseException {
		List<LineParser> list = this.getLine(key);
		var result = new MechanicsExecutor();
		for (LineParser line : list) {
			var config = new SkillLineConfig(line.config());
			try {
				var action = SkillManager.getMechanic(config);
				if (action != null) result.add(action);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public List<SkillCondition> getConditions(String key) throws ParseException {
		return this.getConditions(key, new ArrayList<>());
	}
	
	public List<SkillCondition> getConditions(String key, List<SkillCondition> def) throws ParseException {
		List<LineParser> list = this.getLine(key);
		List<SkillCondition> result = new ArrayList<>();
		for (LineParser line : list) {
			var config = new SkillLineConfig(line.config());
			try {
				var action = SkillManager.getCondition(config);
				if (action != null) result.add(action);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}