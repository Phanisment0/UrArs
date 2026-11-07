
package io.phanisment.urars.compatibility.bettermodel;

import io.phanisment.urars.UrArs;
import io.phanisment.urars.exception.MathParserException;
import io.phanisment.urars.parser.math.UrArsMath;
import io.phanisment.urars.parser.math.value.IValue;
import io.phanisment.urars.parser.math.value.NumberValue;
import io.phanisment.urars.parser.math.value.StringValue;
import io.phanisment.urars.skill.Skill;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.util.UrArsDebugger;

import kr.toxicity.model.api.script.AnimationScript;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.manager.ScriptManagerImpl;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public final class BetterModelCompatibility {
	private BetterModelCompatibility() {}

	public static void init() {
		ScriptManagerImpl.INSTANCE.addBuilder("ua", data -> {
			String args = data.args();

			if (args == null || args.isBlank()) return AnimationScript.EMPTY;

			return AnimationScript.of(true, tracker -> {
				if (!(tracker instanceof EntityTracker entityTracker)) return;

				Object handle = entityTracker.registry().entity().handle();
				if (!(handle instanceof Entity entity)) return;

				Identifier skill_id = Identifier.tryParse(args);
				if (skill_id == null) {
					UrArsDebugger.warn("Invalid UrArs skill identifier '{}'", args);
					return;
				}

				Skill skill = SkillManager.getSkill(skill_id);
				if (skill == null) {
					UrArsDebugger.warn("Unknown UrArs skill '{}'", skill_id);
					return;
				}

				try {
					SkillContext ctx = new SkillContext(entity);
					data.metadata().toMap().forEach((key, value) -> {
						try {
							IValue<?> result = UrArsMath.withStandardLibrary().parse(value).eval();
							ctx.setVar(key, result);
						} catch (MathParserException e) {
							ctx.setVar(key, new StringValue(value)); // Save catch, because this run on animation
						}
					});

					skill.cast(ctx);

					UrArsDebugger.trace("BetterModel script executed: '{}' on '{}'", skill_id, entity.getName().getString());
				} catch (Exception e) {
					UrArs.LOGGER.error("Failed to execute BetterModel script '{}'. Entity: {}", skill_id, entity.getUUID(), e);
				}
			});
		});
		UrArsDebugger.info("BetterModel compatibility initialized");
	}
}
