package io.phanisment.urars.config;

import io.phanisment.urars.config.pack.Pack;
import io.phanisment.urars.config.pack.PackFolderRegistry;
import io.phanisment.urars.config.pack.PackLoader;
import io.phanisment.urars.item.ItemManager;
import io.phanisment.urars.item.ItemMeta;
import io.phanisment.urars.mob.MobMeta;
import io.phanisment.urars.mob.MobManager;
import io.phanisment.urars.parser.file.UrArsYaml;
import io.phanisment.urars.parser.file.config.ConfigSection;
import io.phanisment.urars.reload.ReloadResult;
import io.phanisment.urars.reload.Reloadable;
import io.phanisment.urars.skill.Skill;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.skill.config.SkillConfigSection;
import io.phanisment.urars.util.BufferedString;
import io.phanisment.urars.util.UrArsDebugger;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.BiConsumer;

public final class ConfigManager implements Reloadable {
	public static final PackFolderRegistry PACK_FOLDER_REGISTRY = new PackFolderRegistry();
	private static final String FILE_SUFFIX = ".yml";

	private static int failed_files;

	static {
		PACK_FOLDER_REGISTRY.register("items", folderLoader("items", ItemMeta::new, ItemManager::register));
		PACK_FOLDER_REGISTRY.register("skills", folderLoader("skills", Skill::new, SkillManager::registerSkill));
		PACK_FOLDER_REGISTRY.register("mobs", folderLoader("mobs", MobMeta::new, MobManager::registerMob));
	}

	@Override
	public @NonNull ReloadResult load() {
		failed_files = 0;
		new UrArsPackConfig(new File("config/urars"), PACK_FOLDER_REGISTRY).load();
		return failed_files == 0 ? ReloadResult.success("Loaded packs") : ReloadResult.failed("Failed to read " + failed_files + " file(s), check the log");
	}



	@FunctionalInterface
	private interface EntryFactory<T> {
		T create(Identifier id, File file, SkillConfigSection config);
	}

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
			ConfigSection raw = new ConfigSection(UrArsYaml.parse(file));
			for (BufferedString key : raw.map().keySet()) {
				var key_str = key.toString();
				var config = new SkillConfigSection(raw.getMap(key_str));
				Identifier id = Identifier.fromNamespaceAndPath(pack.name(), key_str);
				register.accept(id, factory.create(id, file, config));
			}
		} catch (IOException e) {
			failed_files++;
			UrArsDebugger.warn("Failed to read file {}", file.getPath(), e);
		}
	}
}