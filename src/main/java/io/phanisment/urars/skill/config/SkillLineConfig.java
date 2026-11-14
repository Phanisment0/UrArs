package io.phanisment.urars.skill.config;

import io.phanisment.urars.UrArs;
import io.phanisment.urars.exception.MathParserException;
import io.phanisment.urars.exception.SkillParseException;
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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SkillLineConfig extends LineConfig {
	public SkillLineConfig(LineConfig copy) {
		super(copy.key(), copy.map(), copy.context());
	}

	public SkillLineConfig(String key, Map<BufferedString, Object> arguments, BufferedString context) {
		super(key, arguments, context);
	}

	// Math
	public Node getMath(@Nullable String... keys) {
		BufferedString value = find(keys);
		if (value == null) throw new SkillParseException("Missing required argument: " + String.join("/", keys), null);
		try {
			return new MathParser(value).parse();
		} catch (MathParserException e) {
			throw new SkillParseException("Failed to parse math in keys " + String.join("/", keys), e);
		}
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
		BufferedString value = find(keys);
		if (value == null) return def.get();
		try {
			return new MathParser(value).parse();
		} catch (MathParserException e) {
			throw new SkillParseException("Failed to parse math in keys " + String.join("/", keys), e);
		}
	}

	// Mechanics & conditions

	public @NonNull MechanicsExecutor getMechanics(@Nullable String... keys) {
		BufferedString value = find(keys);
		if (value == null || value.isBlank()) return new MechanicsExecutor();

		if (!isLineList(value)) try {
			return new MechanicsExecutor(new MathParser(value).parse());
		} catch (MathParserException e) {
			throw new SkillParseException("Failed to parse math in keys " + String.join("/", keys), e);
		}

		MechanicsExecutor executor = new MechanicsExecutor();
		try {
			var list = getLine(keys);
			if (list != null) executor.addAll(ActionUtils.createAll(list, "mechanic", config -> {
				var factory = SkillManager.getMechanic(config.key());
				return factory == null ? null : factory.create(config);
			}));
		} catch (ParseException e) {
			UrArs.LOGGER.error("Failed to parsing mechanics", e);
		}
		return executor;
	}

	public @NonNull List<SkillCondition> getConditions(@Nullable String... keys) {
		try {
			var list = getLine(keys);
			if (list != null) return ActionUtils.createAll(list, "condition", config -> {
				var factory = SkillManager.getCondition(config.key());
				return factory == null ? null : factory.create(config);
			});
		} catch (ParseException e) {
			UrArs.LOGGER.error("Failed to parsing conditions", e);
		}
		return new ArrayList<>();
	}

	// Helpers
	public boolean singleArgument() {
		return map().containsKey(null);
	}

	public boolean isLineList(BufferedString value) {
		BufferedString trimmed = value.trimStart();
		return !trimmed.isEmpty() && trimmed.byteAt(0) == '[';
	}
}