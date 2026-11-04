package io.phanisment.urars.skill.config;

import io.phanisment.urars.exception.SkillParseException;
import io.phanisment.urars.exception.Unchecked;
import io.phanisment.urars.parser.file.config.LineConfig;
import io.phanisment.urars.parser.math.ast.BooleanNode;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.parser.math.ast.NumberNode;
import io.phanisment.urars.parser.math.ast.StringNode;
import io.phanisment.urars.parser.math.parser.MathParser;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillCondition;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.util.ActionUtils;
import io.phanisment.urars.util.BufferedString;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SkillLineConfig extends LineConfig {

	public SkillLineConfig(LineConfig copy) {
		super(copy.key(), copy.map(), copy.context());
	}

	public SkillLineConfig(String key, Map<String, BufferedString> arguments, BufferedString context) {
		super(key, arguments, context);
	}

	// Math //
	public Node getMath(@Nullable String... keys) {
		BufferedString value = findValue(keys);
		if (value == null) throw new SkillParseException("Missing required argument: " + String.join("/", keys), null);
		return parseMath(value);
	}

	public Node getMathNumber(@Nullable String... keys) {
		return getMathNumber(keys, 0);
	}

	public Node getMathNumber(@Nullable String[] keys, double def) {
		return parseOrDefault(keys, () -> new NumberNode(def));
	}

	public Node getMathString(@Nullable String... keys) {
		return getMathString(keys, "");
	}

	public Node getMathString(@Nullable String[] keys, String def) {
		return parseOrDefault(keys, () -> new StringNode(new BufferedString(def)));
	}

	public Node getMathBoolean(@Nullable String... keys) {
		return getMathBoolean(keys, false);
	}

	public Node getMathBoolean(@Nullable String[] keys, boolean def) {
		return parseOrDefault(keys, () -> new BooleanNode(def));
	}

	private Node parseOrDefault(@Nullable String[] keys, Supplier<Node> def) {
		BufferedString value = findValue(keys);
		return value == null ? def.get() : parseMath(value);
	}

	private Node parseMath(BufferedString value) {
		return Unchecked.parse(() -> new MathParser(value).parse());
	}

	// Mechanics & conditions //

	public MechanicsExecutor getMechanics(@Nullable String... keys) {
		BufferedString value = findValue(keys);
		if (value == null || value.isBlank()) return new MechanicsExecutor();

		if (!isLineList(value)) return new MechanicsExecutor(parseMath(value));

		MechanicsExecutor executor = new MechanicsExecutor();
		executor.addAll(Unchecked.parse(() -> ActionUtils.createAll(getLine(keys), "mechanic", config -> {
			var factory = SkillManager.getMechanic(config.key());
			return factory == null ? null : factory.create(config);
		})));
		return executor;
	}

	public List<SkillCondition> getConditions(@Nullable String... keys) {
		return Unchecked.parse(() -> ActionUtils.createAll(getLine(keys), "condition", config -> {
			var factory = SkillManager.getCondition(config.key());
			return factory == null ? null : factory.create(config);
		}));
	}

	// Helpers //
	public boolean singleArgument() {
		return map().containsKey(null);
	}

	public boolean isLineList(BufferedString value) {
		BufferedString trimmed = value.trimStart();
		return !trimmed.isEmpty() && trimmed.byteAt(0) == '[';
	}
}