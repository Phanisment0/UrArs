package io.phanisment.urars.util;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class ItemBuilder {
	private final ItemStack stack;

	private ItemBuilder(ItemStack stack) {
		this.stack = stack;
	}

	public static ItemBuilder of(Identifier id) {
		return of(BuiltInRegistries.ITEM.getValue(id));
	}

	public static ItemBuilder of(ItemLike item) {
		return new ItemBuilder(new ItemStack(item));
	}

	public static ItemBuilder of(ItemStack item) {
		return new ItemBuilder(item.copy());
	}



	public ItemBuilder count(int amount) {
		stack.setCount(amount);
		return this;
	}

	public ItemBuilder damage(int amount) {
		stack.setDamageValue(amount);
		return this;
	}

	public ItemBuilder maxDamage(int amount) {
		stack.set(DataComponents.MAX_DAMAGE, amount);
		return this;
	}

	public ItemBuilder maxStackSize(int amount) {
		stack.set(DataComponents.MAX_STACK_SIZE, amount);
		return this;
	}

	public ItemBuilder repairCost(int cost) {
		stack.set(DataComponents.REPAIR_COST, cost);
		return this;
	}

	public ItemBuilder rarity(String rarity) {
		return rarity(Rarity.valueOf(rarity));
	}

	public ItemBuilder rarity(Rarity rarity) {
		stack.set(DataComponents.RARITY, rarity);
		return this;
	}

	public ItemBuilder glint(boolean glint) {
		stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, glint);
		return this;
	}

	public ItemBuilder itemModel(Identifier model) {
		stack.set(DataComponents.ITEM_MODEL, model);
		return this;
	}



	public ItemBuilder unbreakable() {
		stack.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
		return this;
	}

	public ItemBuilder unbreakable(boolean value) {
		if (value) stack.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
		else stack.remove(DataComponents.UNBREAKABLE);
		return this;
	}



	public ItemBuilder name(String name) {
		return name(Component.literal(name));
	}

	public ItemBuilder name(Component name) {
		stack.set(DataComponents.CUSTOM_NAME, name);
		return this;
	}

	public ItemBuilder nameNoItalic(String name) {
		return nameNoItalic(Component.literal(name));
	}

	public ItemBuilder nameNoItalic(Component name) {
		return name(name.copy().withStyle(style -> style.withItalic(false)));
	}

	public ItemBuilder itemName(String name) {
		return itemName(Component.literal(name));
	}

	public ItemBuilder itemName(Component name) {
		stack.set(DataComponents.ITEM_NAME, name);
		return this;
	}



	public ItemBuilder loreStr(String... lines) {
		return loreStr(Arrays.asList(lines));
	}

	public ItemBuilder loreStr(List<String> lines) {
		List<Component> result = new ArrayList<>();
		for (var line : lines) result.add(Component.literal(line));
		return lore(result);
	}

	public ItemBuilder lore(Component... lines) {
		return lore(Arrays.asList(lines));
	}

	public ItemBuilder lore(List<Component> lines) {
		stack.set(DataComponents.LORE, new ItemLore(new ArrayList<>(lines)));
		return this;
	}

	public ItemBuilder addLore(Component... lines) {
		ItemLore current = stack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
		List<Component> merged = new ArrayList<>(current.lines());
		merged.addAll(Arrays.asList(lines));
		stack.set(DataComponents.LORE, new ItemLore(merged));
		return this;
	}



	public ItemBuilder customModelData(float value) {
		return customModelData(new CustomModelData(List.of(value), List.of(), List.of(), List.of()));
	}

	public ItemBuilder customModelData(CustomModelData data) {
		stack.set(DataComponents.CUSTOM_MODEL_DATA, data);
		return this;
	}

	public ItemBuilder customModelData(List<Float> floats, List<Boolean> flags, List<String> strings, List<Integer> colors) {
		return customModelData(new CustomModelData(floats, flags, strings, colors));
	}



	public ItemBuilder hideTooltip(boolean hide) {
		TooltipDisplay current = stack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
		stack.set(DataComponents.TOOLTIP_DISPLAY, new TooltipDisplay(hide, current.hiddenComponents()));
		return this;
	}

	public ItemBuilder hideComponents(DataComponentType<?>... types) {
		TooltipDisplay display = stack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
		for (DataComponentType<?> type : types) display = display.withHidden(type, true);
		stack.set(DataComponents.TOOLTIP_DISPLAY, display);
		return this;
	}



	public ItemBuilder enchant(Holder<Enchantment> enchantment, int level) {
		ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY));
		mutable.set(enchantment, level);
		stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
		return this;
	}

	public ItemBuilder enchant(HolderLookup.Provider registries, ResourceKey<Enchantment> key, int level) {
		Holder<Enchantment> holder = registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
		return enchant(holder, level);
	}

	public ItemBuilder storedEnchant(Holder<Enchantment> enchantment, int level) {
		ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY));
		mutable.set(enchantment, level);
		stack.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
		return this;
	}

	public ItemBuilder storedEnchant(HolderLookup.Provider registries, ResourceKey<Enchantment> key, int level) {
		Holder<Enchantment> holder = registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
		return storedEnchant(holder, level);
	}

	public ItemBuilder enchantable(int value) {
		stack.set(DataComponents.ENCHANTABLE, new Enchantable(value));
		return this;
	}



	public ItemBuilder attribute(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slot) {
		ItemAttributeModifiers current = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
		stack.set(DataComponents.ATTRIBUTE_MODIFIERS, current.withModifierAdded(attribute, modifier, slot));
		return this;
	}



	public ItemBuilder dyedColor(int rgb) {
		stack.set(DataComponents.DYED_COLOR, new DyedItemColor(rgb));
		return this;
	}

	public ItemBuilder food(int nutrition, float saturation, boolean canAlwaysEat) {
		return food(nutrition, saturation, canAlwaysEat);
	}

	public ItemBuilder food(FoodProperties properties) {
		stack.set(DataComponents.FOOD, properties);
		return this;
	}

	public ItemBuilder potion(Holder<Potion> potion) {
		stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
		return this;
	}



	public ItemBuilder customData(Consumer<CompoundTag> editor) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, editor);
		return this;
	}

	public <T> ItemBuilder component(DataComponentType<T> type, T value) {
		stack.set(type, value);
		return this;
	}

	public ItemBuilder remove(DataComponentType<?> type) {
		stack.remove(type);
		return this;
	}



	public ItemStack get() {
		return stack;
	}
}