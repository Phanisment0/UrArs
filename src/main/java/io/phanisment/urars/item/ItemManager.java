package io.phanisment.urars.item;

import io.phanisment.urars.reload.ReloadResult;
import io.phanisment.urars.reload.Reloadable;
import io.phanisment.urars.util.UrArsDebugger;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ItemManager implements Reloadable {
	private static final Map<Identifier, ItemMeta> ITEMS = new HashMap<>();

	@Override
	public @NonNull ReloadResult load() {
		return ReloadResult.success("Loaded " + ITEMS.size() + " item(s)");
	}

	@Override
	public @NonNull ReloadResult unload() {
		var msg = ReloadResult.success("Unloading " + ITEMS.size() + " item(s)");
		ITEMS.clear();
		return msg;
	}

	public static ItemMeta getItem(Identifier id) {
		return ITEMS.get(id);
	}

	public static Set<Identifier> getItems() {
		return ITEMS.keySet();
	}

	public static void register(Identifier id, ItemMeta item) {
		ItemMeta previous = ITEMS.putIfAbsent(id, item);
		if (previous != null) {
			UrArsDebugger.warn("Item '{}' is already registered, ignoring duplicate", id);
			return;
		}
		UrArsDebugger.debug("Registered item '{}' (total: {})", id, ITEMS.size());
	}
}