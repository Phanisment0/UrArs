package io.phanisment.urars;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resource.ResourceType;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import io.phanisment.urars.resource.MobResource;
import io.phanisment.urars.resource.SkillResource;
import io.phanisment.urars.mob.MobManager; 
import io.phanisment.urars.mob.render.ConfigurableEntityRender;
import io.phanisment.urars.skill.SkillManager; 
import io.phanisment.urars.util.TickScheduler;

/**
 * Main instance of this mod.
 */
public final class UrArs implements ModInitializer, ClientModInitializer {
	public static final String ID = "urars";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);
	
	/** {@inheritDoc} */
	@Override
	public void onInitialize() {
		SkillManager.load();
		MobManager.load();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SkillResource());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new MobResource());
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			TickScheduler.init();
		});
	}
	
	/** {@inheritDoc} */
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(MobManager.CONFIGURABLE_ENTITY, ConfigurableEntityRender::new);
	}
}