package io.phanisment.urars.mob;

import io.phanisment.urars.parser.file.config.ConfigSection;
import io.phanisment.urars.util.UrArsDebugger;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.mod.platform.ModAdapter;
import kr.toxicity.model.api.tracker.TrackerModifier;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public record MobModelData(@Nullable String model, float scale, boolean sight_trace, boolean damage_animation, boolean damage_tint) {
	public MobModelData(final ConfigSection config) {
		this(
				config.getString("id", null),
				config.getFloat("scale", 1f),
				config.getBoolean("sight-trace", true),
				config.getBoolean("damage-animation", false),
				config.getBoolean("damage-tint", true)
		);
	}

	public void apply(final Entity target) {
		if (model == null || model.isBlank()) {
			UrArsDebugger.trace("Skipping empty model for entity '{}'", target.getUUID());
			return;
		}

		UrArsDebugger.debug("Applying model '{}' to entity '{}'", model, target.getUUID());
		BetterModel.model(model).ifPresentOrElse(resource -> resource.getOrCreate(ModAdapter.adapt(target), new TrackerModifier(sight_trace, damage_animation, damage_tint)), () -> UrArsDebugger.warn("Cannot apply model '{}': model was not found", model));
	}
}