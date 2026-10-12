package io.phanisment.urars.reload;

import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

public interface Reloadable {
	default void register() { UrArsReload.register(this); }
	default ReloadResult reload() { return reload(null); }
	ReloadResult reload(@Nullable ServerPlayer player);
}