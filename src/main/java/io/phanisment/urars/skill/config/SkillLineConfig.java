package io.phanisment.urars.skill.config;

import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.lib.file_parser.config.LineConfig;
import io.phanisment.urars.lib.file_parser.parser.LineParser;
import io.phanisment.urars.lib.file_parser.util.BufferedString;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillCondition;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkillLineConfig extends LineConfig {
	public SkillLineConfig(LineConfig config) {
		super(config.key(), config.map(), config.context());
	}

	public SkillLineConfig(BufferedString key, Map<BufferedString, BufferedString> arguments, BufferedString context) {
		super(key, arguments, context);
	}

	public BufferedString getBufferedString(String[] key, BufferedString def) {
		for (String alias : key) {
			BufferedString value = this.getBufferedString(alias);
			if (value != null) return value;
		}
		return def;
	}

	public String getString(String[] key, String def) {
		for (String alias : key) {
			String value = this.getString(alias);
			if (value != null) return value;
		}
		return def;
	}

	public int getInt(String[] key, int def) {
		for (String alias : key) {
			var value = this.getInt(alias);
			if (value != null) return value;
		}
		return def;
	}

	public MechanicsExecutor getMechanics(BufferedString... key) throws ParseException {
		MechanicsExecutor list = new MechanicsExecutor();
		for (BufferedString alias : key) {
			for (LineParser lp : this.getLine(alias)) {
				var config = new SkillLineConfig(lp.config());
				try {
					var action = SkillManager.getMechanic(config);
					if (action != null) list.add(action);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public List<SkillCondition> getConditions(BufferedString... key) throws ParseException {
		List<SkillCondition> list = new ArrayList<>();
		for (BufferedString alias : key) {
			if (!this.map().containsKey(alias)) continue;
			for (LineParser lp : this.getLine(alias)) {
				var config = new SkillLineConfig(lp.config());
				try {
					var action = SkillManager.getCondition(config);
					if (action != null) list.add(action);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
}