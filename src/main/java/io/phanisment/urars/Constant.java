package io.phanisment.urars;

import net.fabricmc.loader.api.FabricLoader;

public interface Constant {
	boolean BETTER_MODEL_LOADED = FabricLoader.getInstance().isModLoaded("bettermodel");
}