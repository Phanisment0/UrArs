package io.phanisment.urars.resource;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.Resource;

import io.phanisment.urars.config.YamlConfig;
import io.phanisment.urars.skill.Skill;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.skill.config.SkillConfigSection;

import static io.phanisment.urars.UrArs.ID;

import java.util.Map;
import java.io.InputStream;

/**
 * Get folder skills in datapack and then register the skill to SkillManager.
 */
public final class SkillResource implements SimpleSynchronousResourceReloadListener {
	@Override
	public Identifier getFabricId() {
		return Identifier.of(ID, "skill_data");
	}
	
	@Override
	public void reload(ResourceManager manager) {
		SkillManager.unload();
		
		Map<Identifier, Resource> resources = manager.findResources("skills", path -> path.getPath().endsWith(".yml"));
		for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
			Identifier id = entry.getKey();
			Resource resource = entry.getValue();
			
			try (InputStream stream = resource.getInputStream()) {
				var config = new YamlConfig(stream);
				for (String name : config.getKeys()) {
					SkillConfigSection section = config.getSection(name).getAsSkillSection();
					var skill = new Skill(id, resource, section);
					SkillManager.registerSkill(Identifier.of(id.getNamespace(), name), skill);
				}
			} catch (Exception e) {
			}
		}
	}
}