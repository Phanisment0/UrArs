package io.phanisment.urars.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

public final class EntityTypeUtils {
	private static final Map<String, Predicate<Entity>> ALIASES = Map.of(
			"entity", e -> true,
			"pathfinder", e -> e instanceof PathfinderMob,
			"living", e -> e instanceof LivingEntity,
			"mob", e -> e instanceof Mob,
			"player", e -> e instanceof Player,
			"monster", e -> e instanceof Enemy,
			"animal", e -> e instanceof Animal
	);

	private EntityTypeUtils() {
	}

	public static Predicate<Entity> matcher(String raw) {
		return matcher(new BufferedString(raw));
	}

	public static Predicate<Entity> matcher(BufferedString raw) {
		List<Predicate<Entity>> parts = new ArrayList<>();
		for (BufferedString part : raw.split('|')) {
			BufferedString s = part.trim();
			if (!s.isEmpty()) parts.add(single(s));
		}
		if (parts.isEmpty()) throw new IllegalArgumentException("Empty entity matcher");
		return e -> {
			for (Predicate<Entity> p : parts) if (p.test(e)) return true;
			return false;
		};
	}

	public static Predicate<Entity> single(BufferedString s) {
		for (Map.Entry<String, Predicate<Entity>> entry : ALIASES.entrySet()) if (s.equalsIgnoreCase(entry.getKey())) return entry.getValue();

		// Tag: "#namespace:tag"
		if (s.byteAt(0) == '#') {
			String raw_tag = s.slice(1).toString().toLowerCase(Locale.ROOT);
			Identifier tag_id = Identifier.tryParse(raw_tag);
			if (tag_id == null) throw new IllegalArgumentException("Invalid entity tag '" + s + "'");
			TagKey<EntityType<?>> tag = TagKey.create(Registries.ENTITY_TYPE, tag_id);
			return e -> e.getType().builtInRegistryHolder().is(tag);
		}

		// Entity type id: "zombie" or "namespace:zombie"
		String name = s.toString().toLowerCase(Locale.ROOT);
		Identifier id = Identifier.tryParse(s.contains((byte) ':') ? name : "minecraft:" + name);
		EntityType<?> type = id == null ? null : BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
		if (type == null) throw new IllegalArgumentException("Unknown entity type '" + s + "'");
		return e -> e.getType() == type;
	}
}