package io.phanisment.urars.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import io.phanisment.urars.reload.ReloadResult;
import io.phanisment.urars.reload.Reloadable;
import io.phanisment.urars.util.UrArsDebugger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class UrArsConfig implements Reloadable {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = Path.of("config", "urars", "config.json");

	private static volatile Data data = new Data();

	public static final class Data {
		public String debug = "off";
	}

	public static UrArsDebugger.Level debug() {
		return UrArsDebugger.Level.valueOf(data.debug.toUpperCase());
	}

	@Override
	public ReloadResult load() {
		try {
			Files.createDirectories(FILE.getParent());

			Data loaded = null;
			if (Files.exists(FILE)) {
				try (Reader reader = Files.newBufferedReader(FILE)) {
					loaded = GSON.fromJson(reader, Data.class);
				}
			}

			data = loaded != null ? loaded : new Data();
			save();

			apply();

			return ReloadResult.success("Loaded config");
		} catch (JsonParseException e) {
			data = new Data();
			return ReloadResult.failed("config.json is invalid, using defaults: " + e.getMessage());
		} catch (IOException e) {
			data = new Data();
			return ReloadResult.failed("Could not read config.json: " + e.getMessage());
		}
	}

	private void apply() {
		UrArsDebugger.level(debug());
	}

	private static void save() throws IOException {
		try (Writer writer = Files.newBufferedWriter(FILE)) {
			GSON.toJson(data, writer);
		}
	}
}