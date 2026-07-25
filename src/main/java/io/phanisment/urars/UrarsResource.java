package io.phanisment.urars;

import static io.phanisment.urars.UrArs.LOGGER;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.jspecify.annotations.NonNull;

import io.phanisment.urars.lib.file_parser.UrArsFileParser;
import io.phanisment.urars.lib.file_parser.config.ConfigSection;
import io.phanisment.urars.lib.file_parser.util.BufferedString;
import io.phanisment.urars.mobs.Mob;
import io.phanisment.urars.mobs.MobManager;
import io.phanisment.urars.skill.Skill;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.skill.config.SkillConfigSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public final class UrArsResource implements PreparableReloadListener {

	@NonNull
	@Override
	public CompletableFuture<Void> reload(@NonNull SharedState state, @NonNull Executor prepare_exc, @NonNull PreparationBarrier prep, @NonNull Executor reload_exc) {
		ResourceManager manager = state.resourceManager();
		return CompletableFuture.runAsync(() -> {
			try {
				loadSkills(manager);
				loadMobs(manager);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}, prepare_exc).thenCompose(prep::wait).thenRunAsync(() -> {
			appliedSkills();
			appliedMobs();
		}, reload_exc);
	}

	private void loadSkills(ResourceManager manager) throws ParseException {
		SkillManager.unload();
		Map<Identifier, Resource> list_resource =	manager.listResources("skills", id -> id.getPath().endsWith(".yml"));
		for (var entry : list_resource.entrySet()) {
			Identifier path = entry.getKey();
			Resource resource = entry.getValue();
			try (var stream = resource.open()) {
				ConfigSection config = UrArsFileParser.load(stream.readAllBytes());
				for (BufferedString name : config.map().keySet()) {
					SkillConfigSection section = new SkillConfigSection(config.getSection(name));
					var id = Identifier.fromNamespaceAndPath(path.getNamespace(), name.toString());
					var skill = new Skill(id, resource, section);
					SkillManager.registerSkill(id, skill);
				}
			} catch (IOException err) {
				LOGGER.warn("File is not loaded", err);
			}
		}
	}

	private void loadMobs(ResourceManager manager) throws ParseException {
		MobManager.unload();
		Map<Identifier, Resource> list_resource =	manager.listResources("mobs", id -> id.getPath().endsWith(".yml"));
		for (var entry : list_resource.entrySet()) {
			Identifier path = entry.getKey();
			Resource resource = entry.getValue();
			try (var stream = resource.open()) {
				ConfigSection config = UrArsFileParser.load(stream.readAllBytes());
				for (BufferedString name : config.map().keySet()) {
					SkillConfigSection section = new SkillConfigSection(config.getSection(name));
					var id = Identifier.fromNamespaceAndPath(path.getNamespace(), name.toString());
					var mob = new Mob(id, resource, section);
					MobManager.registerMob(id, mob);
				}
			} catch (IOException err) {
				LOGGER.warn("File is not loaded", err);
			}
		}
	}

	// If done parsing the file, will be applied
	private void appliedSkills() {
		LOGGER.info("Loaded " + SkillManager.getSkills().size() + " skills");
	}

	private void appliedMobs() {
		LOGGER.info("Loaded " + MobManager.getMobs().size() + " mobs");
	}
}