package io.phanisment.urars.config.pack;

import io.phanisment.urars.exception.UrArsParseException;
import io.phanisment.urars.parser.file.config.ConfigSection;
import io.phanisment.urars.parser.file.parser.Parser;
import io.phanisment.urars.util.UrArsDebugger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public record PackMeta(String name, String version, String author, String description) {
	public static final String FILE_NAME = "pack.urars";

	public static PackMeta defaults(String folder_name) {
		return new PackMeta(folder_name, "0.1", "unknown", "");
	}

	public static PackMeta load(String folder_name, File dir) {
		PackMeta def = defaults(folder_name);
		File file = new File(dir, FILE_NAME);

		if (!file.exists()) {
			try {
				Files.writeString(file.toPath(), template(def), StandardCharsets.UTF_8);
				UrArsDebugger.info("Generated {} for pack '{}'", FILE_NAME, folder_name);
			} catch (IOException e) {
				UrArsDebugger.warn("Could not create {} for pack '{}'", FILE_NAME, folder_name, e);
			}
			return def;
		}

		try {
			var section = new ConfigSection(new Parser(file).parse());
			return new PackMeta(
					read(section, "name", def.name),
					read(section, "version", def.version),
					read(section, "author", def.author),
					read(section, "description", def.description)
			);
		} catch (IOException | UrArsParseException e) {
			UrArsDebugger.warn("Failed to read {} for pack '{}', using defaults", FILE_NAME, folder_name, e);
			return def;
		}
	}

	private static String read(ConfigSection section, String key, String def) {
		String value = section.getString(key, def);
		if (value == null) return def;
		value = value.trim();
		return value.isEmpty() ? def : value;
	}

	private static String template(PackMeta meta) {
		return """
				# urars generated pack metadata
				
				# This parser is really simple and (hopefully) fast.
				# The structure is: key ( `=` | `{` ) value [ `}` ]
				# When you use `=` only a single-line value is allowed.
				# When you use `{}` the value can span multiple lines.

				name = %s
				version = %s
				author = %s
				description {
					%s
				}
				""".formatted(meta.name, meta.version, meta.author, meta.description);
	}
}