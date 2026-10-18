package io.phanisment.urars.reload;

import org.jspecify.annotations.Nullable;

public interface Reloadable {
	default void register() { UrArsReload.register(this); }
	@Nullable ReloadResult load();
	default @Nullable ReloadResult unload() {
		return null;
	};
}