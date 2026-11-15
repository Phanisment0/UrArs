
package io.phanisment.urars.util;

import io.phanisment.urars.item.ItemManager;
import io.phanisment.urars.item.ItemMeta;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.TriggerType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.ItemStack;

import java.lang.foreign.PaddingLayout;
import java.util.Optional;

public final class ItemUtils {

	private ItemUtils() {}

	/**
	 * Casts an item's skill associated with the specified hand.
	 *
	 * @param hand the hand holding the item
	 * @param entity the entity using the item
	 * @param event the skill trigger type
	 */
	public static void castItem(InteractionHand hand, Entity entity, TriggerType event) {
		if (!(entity instanceof Player player)) return;

		ItemStack item = switch (hand) {
			case MAIN_HAND -> player.getMainHandItem();
			case OFF_HAND -> player.getOffhandItem();
		};

		castItem(player, item, event);
	}

	public static void castItem(Entity entity, TriggerType event) {
		if (!(entity instanceof Player player)) return;
		castItem(player, player.getMainHandItem(), event.alias());
	}

	public static void castItem(Player player, TriggerType event) {
		castItem(player, player.getMainHandItem(), event.alias());
	}

	/**
	 * Casts an item's skill using the specified item stack.
	 *
	 * @param player the entity using the item
	 * @param item the item stack
	 * @param event the skill trigger type
	 */
	public static void castItem(Player player, ItemStack item, TriggerType event) {
		castItem(player, item, event.alias());
	}

	/**
	 * Casts an item's skill using the specified item stack and event alias.
	 *
	 * @param player the entity using the item
	 * @param item the item stack
	 * @param event the skill event alias
	 */
	public static void castItem(Player player, ItemStack item, String event) {
		if (item.isEmpty() || item.getItem() instanceof AirItem) return;

		var tag = item.get(DataComponents.CUSTOM_DATA);
		if (tag == null) return;

		Optional<String> id_opt = tag.copyTag().getString(ItemMeta.ITEM_KEY);
		if (id_opt.isEmpty()) return;

		Identifier id = Identifier.tryParse(id_opt.get());
		if (id == null) return;

		ItemMeta item_meta = ItemManager.getItem(id);
		if (item_meta == null) return;

		item_meta.skills().execute(event, new SkillContext(player));
	}
}
