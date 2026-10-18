package io.phanisment.urars.mob;

import io.phanisment.urars.mob.ai.AiGoalFactory;
import io.phanisment.urars.skill.SkillActionFactory;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.util.UrArsDebugger;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public record GoalDataRegistry<T extends Goal>(Map<Identifier, AiGoalFactory<T>> registry, Map<String, Identifier> aliases) {
	public AiGoalFactory<T> get(Identifier id) {
		return registry.get(id);
	}

	@Nullable
	public AiGoalFactory<T> get(String alias) {
		Identifier id = aliases.get(alias.toLowerCase());
		return id == null ? null : registry.get(id);
	}

	public void register(Class<T> clazz, AiGoalFactory<T> factory) {
		RegistryInfo info = clazz.getAnnotation(RegistryInfo.class);

		String author = info.author().toLowerCase();
		String raw_id = info.key().toLowerCase();
		String namespace = info.namespace().toLowerCase();

		Identifier id = Identifier.fromNamespaceAndPath(author, raw_id);
		if (!namespace.isBlank()) id = Identifier.fromNamespaceAndPath(namespace, raw_id);

		if (registry.containsKey(id)) {
			UrArsDebugger.warn("Duplicate registry id '{}' on class '{}', skipping", id, clazz.getSimpleName());
			return;
		}

		registry.put(id, factory);
		aliases.put(raw_id, id);
		aliases.put(id.toString(), id);
		for (String alias : info.aliases()) {
			UrArsDebugger.trace("Alias '{}' -> '{}'", alias.toLowerCase(), id);
			aliases.put(alias.toLowerCase(), id);
		}
	}
}