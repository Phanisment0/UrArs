package io.phanisment.urars.skill.config;

import io.phanisment.urars.exception.MathParserException;
import io.phanisment.urars.parser.file.config.LineConfig;
import io.phanisment.urars.parser.file.parser.LineParser;
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

import java.text.ParseException;
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
	public Node getMath(@Nullable String... keys) throws MathParserException {
		BufferedString value = findValue(keys);
		if (value == null) throw new MathParserException("Missing required argument: " + String.join("/", keys));
		return new MathParser(value).parse();
	}

	public Node getMathNumber(@Nullable String... keys) throws MathParserException {
		return getMathNumber(keys, 0);
	}

	public Node getMathNumber(@Nullable String[] keys, double def) throws MathParserException {
		return parseOrDefault(keys, () -> new NumberNode(def));
	}

	public Node getMathString(@Nullable String... keys) throws MathParserException {
		return getMathString(keys, "");
	}

	public Node getMathString(@Nullable String[] keys, String def) throws MathParserException {
		return parseOrDefault(keys, () -> new StringNode(new BufferedString(def)));
	}

	public Node getMathBoolean(@Nullable String... keys) throws MathParserException {
		return getMathBoolean(keys, false);
	}

	public Node getMathBoolean(@Nullable String[] keys, boolean def) throws MathParserException {
		return parseOrDefault(keys, () -> new BooleanNode(def));
	}

	private Node parseOrDefault(@Nullable String[] keys, Supplier<Node> def) throws MathParserException {
		BufferedString value = findValue(keys);
		return value == null ? def.get() : new MathParser(value).parse();
	}

	// Mechanics & conditions //

	public MechanicsExecutor getMechanics(@Nullable String... keys) throws ParseException {
		BufferedString value = findValue(keys);
		if (value == null || value.isBlank()) return new MechanicsExecutor();

		if (!isLineList(value)) {
			return new MechanicsExecutor(new MathParser(value).parse());
		}

		MechanicsExecutor executor = new MechanicsExecutor();
		executor.addAll(ActionUtils.createAll(getLine(keys), "mechanic", config -> {
			var factory = SkillManager.getMechanic(config.key());
			return factory == null ? null : factory.create(config);
		}));
		return executor;
	}

	public List<SkillCondition> getConditions(@Nullable String... keys) throws ParseException {
		return ActionUtils.createAll(getLine(keys), "condition", config -> {
			var factory = SkillManager.getCondition(config.key());
			return factory == null ? null : factory.create(config);
		});
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