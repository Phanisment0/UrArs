package io.phanisment.urars.reload;

import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public final class UrArsReload {
	private static final List<Reloadable> list = new ArrayList<>();

	public static void register(Reloadable reloadable) {
		list.add(reloadable);
	}

	public static List<ReloadResult> reloadAll(ServerPlayer player) {
		List<ReloadResult> results = new ArrayList<>();
		for (var reloadable : list) try {
			results.add(reloadable.reload(player));
		} catch (Exception e) {
			results.add(ReloadResult.failed(e.getMessage()));
		}
		return results;
	}
}