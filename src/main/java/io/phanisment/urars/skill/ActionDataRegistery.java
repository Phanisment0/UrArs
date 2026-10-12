package io.phanisment.urars.skill;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public record ActionDataRegistery<T extends ISkillAction>(Map<Identifier, SkillActionFactory<T>> registry, Map<String, Identifier> aliases) {
	public SkillActionFactory<T> get(Identifier id) {
		return registry.get(id);
	}
	
	@Nullable
	public SkillActionFactory<T> get(String alias) {
		Identifier id = aliases.get(alias.toLowerCase());
		return id == null ? null : registry.get(id);
	}
}
