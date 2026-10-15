package io.phanisment.urars.config;

import io.phanisment.urars.config.pack.Pack;
import io.phanisment.urars.config.pack.PackFolderRegistry;
import io.phanisment.urars.config.pack.PackLoader;
import io.phanisment.urars.exception.UrArsParseException;
import io.phanisment.urars.mob.Mob;
import io.phanisment.urars.mob.MobManager;
import io.phanisment.urars.parser.file.UrArsFileParser;
import io.phanisment.urars.parser.file.parser.Parser;
import io.phanisment.urars.skill.Skill;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.skill.config.SkillConfigSection;
import io.phanisment.urars.util.BufferedString;
import io.phanisment.urars.util.UrArsDebugger;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.function.BiConsumer;

public final class ConfigManager {
	public static final PackFolderRegistry PACK_FOLDER_REGISTRY = new PackFolderRegistry();
	private static final String FILE_SUFFIX = ".urars";

	private ConfigManager() {
	}

	public static void load() {
		PACK_FOLDER_REGISTRY.register("skills", folderLoader("skills", Skill::new, SkillManager::registerSkill));
		PACK_FOLDER_REGISTRY.register("mobs", folderLoader("mobs", Mob::new, MobManager::registerMob));
		new UrArsPackConfig(new File("config/urars"), PACK_FOLDER_REGISTRY).load();
	}

	@FunctionalInterface
	private interface EntryFactory<T> {
		T create(Identifier id, File file, SkillConfigSection config);
	}

	/**
	 * Creates a loader that reads every .urars file in {@code <pack>/<folder>}, turns each
	 * top-level entry into a T, and registers it under {@code <pack name>:<entry key>}.
	 */
	private static <T> PackLoader folderLoader(String folder, EntryFactory<T> factory, BiConsumer<Identifier, T> register) {
		return pack -> {
			File[] files = pack.folder(folder).listFiles(f -> f.isFile() && f.getName().endsWith(FILE_SUFFIX));
			if (files == null) return;

			Arrays.sort(files, Comparator.comparing(File::getName));
			for (File file : files) loadFile(pack, file, factory, register);
		};
	}

	private static <T> void loadFile(Pack pack, File file, EntryFactory<T> factory, BiConsumer<Identifier, T> register) {
		try {
			Map<String, BufferedString> raw = UrArsFileParser.load(file);
			for (Map.Entry<String, BufferedString> entry : raw.entrySet()) {
				var config = new SkillConfigSection(new Parser(entry.getValue()).parse());
				Identifier id = Identifier.fromNamespaceAndPath(pack.name(), entry.getKey());
				register.accept(id, factory.create(id, file, config));
			}
		} catch (IOException | UrArsParseException e) {
			UrArsDebugger.warn("Failed to read file {}", file.getPath(), e);
		}
	}
}