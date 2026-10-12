package io.phanisment.urars.skill;

import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.BufferedString;
import io.phanisment.urars.util.UrArsDebugger;
import net.minecraft.resources.Identifier;

import java.util.Locale;

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

		// strip() removes leading/trailing whitespace including \r \n \t
		BufferedString raw = context.trim();
		UrArsDebugger.trace("Condition context raw: '{}'", escape(context.toString()));
		if (raw.isEmpty()) return result;

		String[] part = raw.split("\\s+", 2);
		String type = part[0].toLowerCase(Locale.ROOT);

		switch (type) {
			case "true" -> {
				return result;
			}
			case "false" -> {
				return !result;
			}
			case "cast", "castinstead", "orelse" -> {
				// Only these actions need a skill id, so only parse it here
				Skill skill = resolveSkill(part, type);
				if (skill == null) return type.equals("cast") && result;

				switch (type) {
					case "cast" -> {
						skill.cast(ctx);
						return result;
					}
					case "castinstead" -> {
						if (result) skill.cast(ctx);
						return false;
					}
					default -> { // orelse
						if (!result) skill.cast(ctx);
						return false;
					}
				}
			}
			default -> LOGGER.warn("Unknown condition action of {}", type);
		}

		return result;
	}

	private static Skill resolveSkill(String[] part, String type) {
		if (part.length < 2) {
			UrArsDebugger.warn("Condition action '{}' needs a skill id, but none was given", type);
			return null;
		}
		String raw_id = part[1].strip().toLowerCase(Locale.ROOT);
		Identifier id = Identifier.tryParse(raw_id); // null instead of throwing
		if (id == null) {
			UrArsDebugger.warn("Invalid skill id '{}' in condition action '{}'", escape(raw_id), type);
			return null;
		}
		Skill skill = SkillManager.getSkill(id);
		if (skill == null) UrArsDebugger.warn("Skill '{}' referenced by condition action '{}' was not found", id, type);
		return skill;
	}

	/** Makes invisible characters visible in logs. */
	private static String escape(String s) {
		return s.replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t");
	}
}