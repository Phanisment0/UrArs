package io.phanisment.urars;

import io.phanisment.urars.config.ConfigManager;
import io.phanisment.urars.mob.MobManager;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.util.TickScheduler;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.resources.Identifier;

import java.io.File;

public class UrArs implements DedicatedServerModInitializer {
	public static final String MOD_ID = "urars";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier of(String input) {
		String name = input.strip().toLowerCase();
		if (name.isEmpty()) return null;
		return name.indexOf(':') >= 0 ? Identifier.tryParse(name) : Identifier.fromNamespaceAndPath(MOD_ID, name);
	}

	@Override
	public void onInitializeServer() {
		reload();
	}

	public static void reload() {
		SkillManager.unload();
		MobManager.unload();
		ConfigManager.load();
	}

	public static void tick(MinecraftServer server) {
		TickScheduler.init();
	}
}