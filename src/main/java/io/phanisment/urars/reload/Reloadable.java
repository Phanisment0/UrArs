package io.phanisment.urars.reload;

import org.jspecify.annotations.Nullable;

public interface Reloadable {
	@Nullable ReloadResult load();
	default @Nullable ReloadResult unload() {
		return null;
	}
	default void register() {
		UrArsReload.register(this);
	}
}