package io.phanisment.urars.item;

import io.phanisment.urars.UrArs;
import io.phanisment.urars.parser.file.config.IConfig;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.config.SkillConfigSection;
import io.phanisment.urars.util.ItemBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ItemMeta {
	public static final String ITEM_KEY = "urars:item";
	private final Identifier id;

	private final Identifier type;
	private final String name;
	private final List<String> lore;
	private final Integer custom_model_data;
	private final Integer max_stack;
	private final String item_name;
	private final Integer damaged;
	private final Integer max_damaged;
	private final Integer repair_cost;
	private final Boolean glint;
	private final String rarity;
	private final Boolean unbreakable;
	//private final Identifier item_model;
	private final Boolean hide_tooltips;
	//private final FoodProperties food;

	private final MechanicsExecutor skills;

	public ItemMeta(final Identifier id, final File file, final SkillConfigSection config) {
		this.id = id;

		type = parseType(config);
		name = config.getString("name");
		lore = parseLore(config);

		custom_model_data = config.getInteger("custom-model-data");
		max_stack = config.getInteger("max-stack");
		item_name = config.getString("item-name");
		damaged = config.getInteger("damaged");
		max_damaged = config.getInteger("max-damaged");
		repair_cost = config.getInteger("repair-cost");
		glint = config.getBoolean("glint");
		rarity = config.getString("rarity");
		unbreakable = config.getBoolean("unbreakable");
		//item_model = Identifier.tryParse(config.getString("item-model")); // FIXME: im lazy to check if item exist
		hide_tooltips = config.getBoolean("hide-tooltips");
		//food = parseFood(config);
		skills = config.getMechanics("skills");
	}

	public MechanicsExecutor skills() {
		return skills;
	}

	private Identifier parseType(IConfig config) {
		String type_str = config.getString("type", "stone");
		Identifier type_id = Identifier.tryParse(type_str);
		if (type_id == null || !BuiltInRegistries.ITEM.containsKey(type_id)) {
			UrArs.LOGGER.error("Unknown item type `{}` in {} use stone instead as default", type_str, id);
			return Identifier.fromNamespaceAndPath("minecraft", "stone");
		}
		return type_id;
	}

	private List<String> parseLore(IConfig config) {
		List<String> list = new ArrayList<>();
		for (var line : config.getList("lore")) list.add(line.toString());
		return list;
	}

	/*private FoodProperties parseFood(IConfig config) {
		IConfig food = config.getSection("food");
		return new FoodProperties(
				food.getInteger("nutrition"),
				food.getFloat("saturation"),
				food.getBoolean("can-always-eat")
		);
	}*/

	public ItemStack build() {
		var builder = ItemBuilder.of(type);
		builder.customData(c -> c.putString(ITEM_KEY, id.toString()));

		if (name != null) builder.nameNoItalic(name);
		if (!lore.isEmpty()) builder.loreStr(lore);

		if (custom_model_data != null) builder.customModelData(custom_model_data);
		if (max_stack != null) builder.maxStackSize(max_stack);
		if (item_name != null) builder.itemName(item_name);
		if (damaged != null) builder.damage(damaged);
		if (max_damaged != null) builder.maxDamage(max_damaged);
		if (repair_cost != null) builder.repairCost(repair_cost);
		if (rarity != null) builder.rarity(rarity);
		if (glint != null) builder.glint(glint);
		if (unbreakable != null) builder.unbreakable(unbreakable);
		//if (item_model != null) builder.itemModel(item_model);
		if (hide_tooltips != null) builder.hideTooltip(hide_tooltips);
		//if (food != null) builder.food(food);

		return builder.get();
	}
}