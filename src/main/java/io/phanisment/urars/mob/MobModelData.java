package io.phanisment.urars.mob;

import io.phanisment.urars.parser.file.config.ConfigSection;
import io.phanisment.urars.util.UrArsDebugger;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.mod.platform.ModAdapter;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public record MobModelData(@Nullable String model) {
	public MobModelData(final ConfigSection config) {
		this(config.getString("id"));
	}

	public void apply(final Entity target) {
		if (model == null || model.isBlank()) {
			UrArsDebugger.trace("Skipping empty model for entity '{}'", target.getUUID());
			return;
		}

		UrArsDebugger.debug("Applying model '{}' to entity '{}'", model, target.getUUID());
		BetterModel.model(model).ifPresentOrElse(resource -> resource.getOrCreate(ModAdapter.adapt(target)), () -> UrArsDebugger.warn("Cannot apply model '{}': model was not found", model));
	}
}