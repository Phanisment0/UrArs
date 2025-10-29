package io.phanisment.urars;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceType;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import io.phanisment.urars.resource.SkillResource;
import io.phanisment.urars.skill.SkillManager; 

/**
 * Main instance of this mod.
 */
public final class UrArs implements ModInitializer {
	public static final String ID = "urars";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);
	
	/**
	 * On initialize this mod jar
	 */
	@Override
	public void onInitialize() {
		SkillManager.loadClasses();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SkillResource());
	}
}