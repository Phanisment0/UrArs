package io.phanisment.urars.util;

import io.phanisment.urars.parser.file.parser.LineParser;
import io.phanisment.urars.skill.config.SkillLineConfig;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class ActionUtils {
	public static String[] aliases(String... alias) {
		return alias;
	}

	public static String[] aliasNull() {
		return new String[]{null};
	}

	public static <T> List<T> createAll(List<LineParser> lines, String kind, LineFactory<T> factory) {
		List<T> result = new ArrayList<>(lines.size());
		for (LineParser line : lines) {
			try {
				var config = new SkillLineConfig(line.config());
				T created = factory.create(config);
				if (created == null) {
					UrArsDebugger.warn("Unknown {} '{}' in line: {}", kind, config.key(), line);
					continue;
				}
				result.add(created);
				UrArsDebugger.trace("Created {} '{}'", kind, config.key());
			} catch (Exception e) {
				UrArsDebugger.error("Failed to create " + kind + " from line: " + line, e);
			}
		}
		return result;
	}

	@FunctionalInterface
	public interface LineFactory<T> {
		@Nullable T create(SkillLineConfig config) throws Exception;
	}
}
