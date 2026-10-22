package io.phanisment.urars.skill;

import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.BufferedString;
import io.phanisment.urars.util.UrArsDebugger;
import net.minecraft.resources.Identifier;

import static io.phanisment.urars.UrArs.LOGGER;

public abstract class SkillCondition implements ISkillAction {
	private final SkillLineConfig config;

	public SkillCondition(SkillLineConfig config) {
		this.config = config;
	}

	public abstract boolean check(SkillContext ctx);

	public boolean execute(SkillContext ctx) {
		boolean result = check(ctx);
		BufferedString context = config.context();
		if (context == null || context.isEmpty()) return result;

		UrArsDebugger.trace("Condition context raw: '{}'", context);

		BufferedString[] part = context.split(' ', 2);
		BufferedString name = part[0].trim();
		Action action = parseAction(name);

		switch (action) {
			case TRUE -> {
				return result;
			}
			case FALSE -> {
				return !result;
			}
			case UNKNOWN -> {
				LOGGER.warn("Unknown condition action of {}", name);
				return result;
			}
			default -> {
				// Only cast / castinstead / orelse need a skill id, so only parse it here
				Skill skill = resolveSkill(part, name);
				if (skill == null) return action == Action.CAST && result;

				switch (action) {
					case CAST -> {
						skill.cast(ctx);
						return result;
					}
					case CAST_INSTEAD -> {
						if (result) skill.cast(ctx);
						return false;
					}
					default -> { // OR_ELSE
						if (!result) skill.cast(ctx);
						return false;
					}
				}
			}
		}
	}

	private static Action parseAction(BufferedString s) {
		return switch (s.length()) {
			case 4:
				if (s.isTrue()) yield Action.TRUE;
				if (s.equalsIgnoreCase("cast")) yield Action.CAST;
			case 5:  if (s.isFalse()) yield  Action.FALSE;
			case 6:  if (s.equalsIgnoreCase("orelse")) yield Action.OR_ELSE;
			case 11: if (s.equalsIgnoreCase("castinstead")) yield Action.CAST_INSTEAD;
			default: yield Action.UNKNOWN;
		};
	}

	private static Skill resolveSkill(BufferedString[] part, BufferedString type) {
		if (part.length < 2) {
			UrArsDebugger.warn("Condition action '{}' needs a skill id, but none was given", type);
			return null;
		}
		String raw_id = part[1].trim().toString().toLowerCase();
		Identifier id = Identifier.tryParse(raw_id);
		if (id == null) {
			UrArsDebugger.warn("Invalid skill id '{}' in condition action '{}'", raw_id, type);
			return null;
		}
		Skill skill = SkillManager.getSkill(id);
		if (skill == null) UrArsDebugger.warn("Skill '{}' referenced by condition action '{}' was not found", id, type);
		return skill;
	}

	private enum Action {
		TRUE, FALSE, CAST, CAST_INSTEAD, OR_ELSE, UNKNOWN
	}
}