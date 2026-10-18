package io.phanisment.urars.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

public final class EntityTypeUtils {
	private static final Map<String, Predicate<Entity>> ALIASES = Map.of(
			"entity", e -> true,
			"living", e -> e instanceof LivingEntity,
			"mob", e -> e instanceof Mob,
			"player", e -> e instanceof Player,
			"monster", e -> e instanceof Enemy,
			"animal", e -> e instanceof Animal
	);

	private EntityTypeUtils() {}

	/**
	 * Accepts: an alias (player, monster, living...), an entity id (minecraft:villager, or just villager),
	 * or a tag (#minecraft:skeletons). Combine several with '|': player|villager
	 */
	public static Predicate<Entity> matcher(String raw) {
		List<Predicate<Entity>> parts = new ArrayList<>();
		for (String part : raw.split("\\|")) {
			String s = part.strip().toLowerCase();
			if (!s.isEmpty()) parts.add(single(s));
		}
		if (parts.isEmpty()) throw new IllegalArgumentException("Empty entity matcher");
		return e -> {
			for (Predicate<Entity> p : parts) if (p.test(e)) return true;
			return false;
		};
	}

	private static Predicate<Entity> single(String s) {
		Predicate<Entity> alias = ALIASES.get(s);
		if (alias != null) return alias;

		if (s.startsWith("#")) {
			Identifier tag_id = Identifier.tryParse(s.substring(1));
			if (tag_id == null) throw new IllegalArgumentException("Invalid entity tag '" + s + "'");
			TagKey<EntityType<?>> tag = TagKey.create(Registries.ENTITY_TYPE, tag_id);
			return e -> e.getType().builtInRegistryHolder().is(tag);
		}

		Identifier id = Identifier.tryParse(s.contains(":") ? s : "minecraft:" + s);
		EntityType<?> type = id == null ? null : BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
		if (type == null) throw new IllegalArgumentException("Unknown entity type '" + s + "'");
		return e -> e.getType() == type;
	}
}