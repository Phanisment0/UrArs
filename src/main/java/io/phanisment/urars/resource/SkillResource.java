package io.phanisment.urars.resource;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import io.phanisment.urars.config.YamlConfig;
import io.phanisment.urars.skill.Skill;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.skill.config.SkillConfigSection;

import static io.phanisment.urars.UrArs.LOGGER;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public final class SkillResource implements PreparableReloadListener {

	@Override
	public CompletableFuture<Void> reload(SharedState state, Executor prepare_exc, PreparationBarrier prep, Executor reload_exc) {
		ResourceManager manager = state.resourceManager();
		return CompletableFuture.runAsync(() -> load(manager), prepare_exc)
		 .thenCompose(prep::wait)
		 .thenRunAsync(this::apply, reload_exc);
	}

	// Load all file with extension `.yml` in file skills
	private void load(ResourceManager manager) {
		Map<Identifier, Resource> list_resource =	manager.listResources("skills", id -> id.getPath().endsWith(".yml"));
		for (var entry : list_resource.entrySet()) {
			Identifier id = entry.getKey();
			Resource resource = entry.getValue();
			try (var stream = resource.open()) {
				var config = new YamlConfig(stream);
				for (String name : config.getKeys()) {
					SkillConfigSection section = config.getSection(name).getAsSkillSection();
					var skill = new Skill(id, resource, section);
					SkillManager.registerSkill(Identifier.fromNamespaceAndPath(id.getNamespace(), name), skill);
				}
			} catch (IOException err) {
				LOGGER.warn("File is not loaded", err);
			}
		}
	}

	// If done parsing the file, will be applied
	private void apply() {
		int count = 0; // Just a pesudo data length
		LOGGER.info("Loaded " + count + " skills");
	}
}