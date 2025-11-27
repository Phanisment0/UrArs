package io.phanisment.urars.resource;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.Resource;

import io.phanisment.urars.config.YamlConfig;
import io.phanisment.urars.skill.config.SkillConfigSection;
import io.phanisment.urars.mob.MobManager;
import io.phanisment.urars.mob.Mob;

import static io.phanisment.urars.UrArs.ID;
import static io.phanisment.urars.UrArs.LOGGER;

import java.util.Map;
import java.io.InputStream;

public final class MobResource implements SimpleSynchronousResourceReloadListener {
	@Override
	public Identifier getFabricId() {
		return Identifier.of(ID, "mob_data");
	}
	
	@Override
	public void reload(ResourceManager manager) {
		Map<Identifier, Resource> resources = manager.findResources("mobs", path -> path.getPath().endsWith(".yml"));
		for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
			Identifier id = entry.getKey();
			Resource resource = entry.getValue();
			
			try (InputStream stream = resource.getInputStream()) {
				var config = new YamlConfig(stream);
				for (String key : config.getKeys()) {
					SkillConfigSection section = config.getSection(key).getAsSkillSection();
					var name = Identifier.of(id.getNamespace(), key);
					var mob = new Mob(name, resource, section);
					MobManager.registerMob(name, mob);
				}
			} catch (Exception e) {
				LOGGER.error("Unexpected error", e);
			}
		}
		LOGGER.info("Loaded " + MobManager.getMobs().size() + " mobs");
	}
}