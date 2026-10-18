package io.phanisment.urars.reload;

import io.phanisment.urars.util.UrArsDebugger;

import java.util.ArrayList;
import java.util.List;

public final class UrArsReload {
	private static final List<Reloadable> list = new ArrayList<>();
	private static boolean first_load = true;

	public static void register(Reloadable reloadable) {
		if (!list.contains(reloadable)) list.add(reloadable);
	}

	public static List<ReloadResult> reloadAll() {
		List<ReloadResult> results = new ArrayList<>();
		if (!first_load) {
			for (int i = list.size() - 1; i >= 0; i--) {
				Reloadable reloadable = list.get(i);
				try {
					ReloadResult msg = reloadable.unload();
					if (msg != null) results.add(msg);
				} catch (Exception e) {
					UrArsDebugger.warn("Failed to unload {}", reloadable.getClass().getSimpleName(), e);
				}
			}
		}
		first_load = false;

		for (Reloadable reloadable : list) try {
			ReloadResult msg = reloadable.load();
			if (msg != null) results.add(msg);
		} catch (Exception e) {
			results.add(ReloadResult.failed(reloadable.getClass().getSimpleName() + ": " + e.getMessage()));
		}
		return results;
	}
}