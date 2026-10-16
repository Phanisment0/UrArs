package io.phanisment.urars.skill.config;

import io.phanisment.urars.UrArs;
import io.phanisment.urars.exception.MathParserException;
import io.phanisment.urars.parser.file.config.LineConfig;
import io.phanisment.urars.parser.file.parser.LineParser;
import io.phanisment.urars.parser.math.ast.BooleanNode;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.parser.math.ast.NumberNode;
import io.phanisment.urars.parser.math.ast.StringNode;
import io.phanisment.urars.parser.math.eval.EvalContext;
import io.phanisment.urars.parser.math.parser.MathParser;
import io.phanisment.urars.skill.*;
import io.phanisment.urars.util.BufferedString;
import io.phanisment.urars.util.UrArsDebugger;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

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

	public Node getMath(@Nullable String... keys) throws MathParserException {
		BufferedString value = findValue(keys);
		if (value == null) throw new MathParserException("Missing required argument: " + String.join("/", keys));
		return new MathParser(value).parse();
	}

	public Node getMathNumber(@Nullable String... keys) throws MathParserException {
		return getMathNumber(keys, 0);
	}

	public Node getMathNumber(@Nullable String[] keys, double def) throws MathParserException {
		BufferedString value = findValue(keys);
		return value == null ? new NumberNode(def) : new MathParser(value).parse();
	}

	public Node getMathString(@Nullable String... keys) throws MathParserException {
		return getMathString(keys, "");
	}

	public Node getMathString(@Nullable String[] keys, String def) throws MathParserException {
		BufferedString value = findValue(keys);
		return value == null ? new StringNode(new BufferedString(def)) : new MathParser(value).parse();
	}

	public Node getMathBoolean(@Nullable String... keys) throws MathParserException {
		return getMathBoolean(keys, false);
	}

	public Node getMathBoolean(@Nullable String[] keys, boolean def) throws MathParserException {
		BufferedString value = findValue(keys);
		return value == null ? new BooleanNode(def) : new MathParser(value).parse();
	}

	public MechanicsExecutor getMechanics(@Nullable String... keys) throws ParseException {
		BufferedString value = findValue(keys);
		if (value == null || value.isBlank()) return new MechanicsExecutor();

		if (!isLineList(value)) {
			Node id = new MathParser(value).parse();
			return new MechanicsExecutor(id);
		}

		MechanicsExecutor list = new MechanicsExecutor();
		for (LineParser line : this.getLine(keys)) try {
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

	public List<SkillCondition> getConditions(@Nullable String... keys) throws ParseException {
		List<SkillCondition> list = new ArrayList<>();
		for (LineParser line : this.getLine(keys)) try {
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

	public boolean singleArgument() {
		return map().containsKey(null);
	}

	public boolean isLineList(BufferedString value) {
		BufferedString trimmed = value.trim();
		return !trimmed.isEmpty() && trimmed.byteAt(0) == '[';
	}
}