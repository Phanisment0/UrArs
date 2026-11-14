package io.phanisment.urars.util;

import io.phanisment.urars.item.ItemMeta;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Optional;

public final class ItemUtils {
	public static void castItem(Entity entity, ItemStack item, String event) {
		if (!(entity instanceof Player player)) return;

		ItemStack current_item = player.getInventory().getSelectedItem();
		if (current_item.getItem() instanceof AirItem) return;

		var tag = current_item.get(DataComponents.CUSTOM_DATA);
		if (tag == null) return;
		Optional<String> id = tag.copyTag().getString(ItemMeta.ITEM_KEY);
	}
}
