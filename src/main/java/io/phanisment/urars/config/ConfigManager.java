package io.phanisment.urars.config;

import io.phanisment.urars.UrArs;
import io.phanisment.urars.config.pack.PackFolderRegistry;
import io.phanisment.urars.exception.UrArsParseException;
import io.phanisment.urars.parser.file.UrArsFileParser;
import io.phanisment.urars.parser.file.parser.Parser;
import io.phanisment.urars.mob.Mob;
import io.phanisment.urars.mob.MobManager;
import io.phanisment.urars.skill.Skill;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.skill.config.SkillConfigSection;
import io.phanisment.urars.util.BufferedString;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class ConfigManager {
	public static final PackFolderRegistry PACK_FOLDER_REGISTRY = new PackFolderRegistry();

	public static void load() {
		PACK_FOLDER_REGISTRY.register("skills", ConfigManager::folderSkills);
		PACK_FOLDER_REGISTRY.register("mobs", ConfigManager::folderMobs);
		new UrArsConfig(new File("config/urars"), PACK_FOLDER_REGISTRY).load();
	}

	private static void folderMobs(File folder) {
		File[] files = folder.listFiles(f -> f.getName().endsWith(".urars"));
		if (files != null) for (File file : files) try {
			Map<String, BufferedString> raw_config = UrArsFileParser.load(file);
			for (Map.Entry<String, BufferedString> entry : raw_config.entrySet()) {
				var config = new SkillConfigSection(new Parser(entry.getValue()).parse());
				String namespace = file.getName().replaceAll(".urars", "");
				Identifier id = Identifier.fromNamespaceAndPath(namespace, entry.getKey());
				var mob_meta = new Mob(id, file, config);
				MobManager.registerMob(id, mob_meta);
			}
		} catch (IOException | UrArsParseException e) {
			UrArs.LOGGER.info("Filed to read a file in {}", file.getPath());
		}
	}

	private static void folderSkills(File folder) {
		File[] files = folder.listFiles(f -> f.getName().endsWith(".urars"));
		if (files != null) for (File file : files) try {
			Map<String, BufferedString> raw_config = UrArsFileParser.load(file);
			for (Map.Entry<String, BufferedString> entry : raw_config.entrySet()) {
				var config = new SkillConfigSection(new Parser(entry.getValue()).parse());
				String namespace = folder.getName().replaceAll(".urars", "");
				Identifier id = Identifier.fromNamespaceAndPath(namespace, entry.getKey());
				var skill_meta = new Skill(id, file, config);
				SkillManager.registerSkill(id, skill_meta);
			}
		} catch (IOException | UrArsParseException e) {
			UrArs.LOGGER.info("Filed to read a file in {}", file.getPath());
		}
	}
}
