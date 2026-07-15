package io.phanisment.urars;

import static io.phanisment.urars.UrArs.LOGGER;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import io.phanisment.urars.config.YamlConfig;
import io.phanisment.urars.mobs.Mob;
import io.phanisment.urars.mobs.MobManager;
import io.phanisment.urars.skill.Skill;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.skill.config.SkillConfigSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public final class UrarsResource implements PreparableReloadListener {

	@Override
	public CompletableFuture<Void> reload(SharedState state, Executor prepare_exc, PreparationBarrier prep, Executor reload_exc) {
		ResourceManager manager = state.resourceManager();
		return CompletableFuture.runAsync(() -> {
			loadSkills(manager);
			loadMobs(manager);
		}, prepare_exc).thenCompose(prep::wait).thenRunAsync(() -> {
			appliedSkills();
			appliedMobs();
		}, reload_exc);
	}

	private void loadSkills(ResourceManager manager) {
		Map<Identifier, Resource> list_resource =	manager.listResources("skills", id -> id.getPath().endsWith(".yml"));
		for (var entry : list_resource.entrySet()) {
			Identifier path = entry.getKey();
			Resource resource = entry.getValue();
			try (var stream = resource.open()) {
				var config = new YamlConfig(stream);
				for (String name : config.getKeys()) {
					SkillConfigSection section = config.getSection(name).getAsSkillSection();
					var id = Identifier.fromNamespaceAndPath(path.getNamespace(), name);
					var skill = new Skill(id, resource, section);
					SkillManager.registerSkill(id, skill);
				}
			} catch (IOException err) {
				LOGGER.warn("File is not loaded", err);
			}
		}
	}

	private void loadMobs(ResourceManager manager) {
		Map<Identifier, Resource> list_resource =	manager.listResources("mobs", id -> id.getPath().endsWith(".yml"));
		for (var entry : list_resource.entrySet()) {
			Identifier path = entry.getKey();
			Resource resource = entry.getValue();
			try (var stream = resource.open()) {
				var config = new YamlConfig(stream);
				for (String name : config.getKeys()) {
					SkillConfigSection section = config.getSection(name).getAsSkillSection();
					var id = Identifier.fromNamespaceAndPath(path.getNamespace(), name);
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
		LOGGER.info("Loaded " + MobManager.getMobs().size() + " skills");
	}
}
