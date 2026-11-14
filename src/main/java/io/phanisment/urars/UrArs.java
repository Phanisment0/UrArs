package io.phanisment.urars;

import io.phanisment.urars.compatibility.bettermodel.BetterModelCompatibility;
import io.phanisment.urars.config.ConfigManager;
import io.phanisment.urars.config.UrArsConfig;
import io.phanisment.urars.item.ItemManager;
import io.phanisment.urars.mob.MobManager;
import io.phanisment.urars.reload.ReloadResult;
import io.phanisment.urars.reload.UrArsReload;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.util.UrArsScheduler;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.resources.Identifier;

import java.util.List;

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
		new UrArsConfig().register();
		new ConfigManager().register();
		new SkillManager().register();
		new ItemManager().register();
		new MobManager().register();

		long start = System.nanoTime();
		List<ReloadResult> results = UrArsReload.reloadAll();
		for (ReloadResult result : results) {
			if (result.result()) LOGGER.info("[UrArs] {}", result.message());
			else LOGGER.error("[UrArs/ERROR] {}", result.message());
		}
		LOGGER.info("[UrArs] Reload finished in {} ms", (System.nanoTime() - start) / 1_000_000 );

		if (Constant.BETTER_MODEL_LOADED) BetterModelCompatibility.init();
	}

	public static void start(MinecraftServer server) {
	}

	public static void stop(MinecraftServer server) {

	}

	public static void tick(MinecraftServer server) {
		UrArsScheduler.init();
	}
}