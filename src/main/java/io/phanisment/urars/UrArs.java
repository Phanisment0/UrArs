package io.phanisment.urars;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.phanisment.urars.resource.SkillResource;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.util.TickScheduler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;

public class UrArs implements ModInitializer {
	public static final String MOD_ID = "urars";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		SkillManager.load();
		ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(Identifier.fromNamespaceAndPath(MOD_ID, "skill"), new SkillResource());
		ServerLifecycleEvents.SERVER_STARTING.register(this::serverStart);
		ServerLifecycleEvents.SERVER_STOPPED.register(this::serverStop);
	}

	private void serverStart(MinecraftServer server) {
		ServerTickEvents.END_SERVER_TICK.register(this::tick);
	}

	private void serverStop(MinecraftServer server) {
	}

	private void tick(MinecraftServer server) {
		TickScheduler.init();
	}
}