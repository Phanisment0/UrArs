package io.phanisment.urars.skill;

import io.phanisment.urars.util.UrArsDebugger;
import net.minecraft.resources.Identifier;
import io.phanisment.urars.skill.config.SkillConfigSection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Skill {
	public final Identifier id;
	public final File file;
	public final SkillConfigSection config;

	private final List<SkillCondition> conditions;
	private final MechanicsExecutor mechanics;

	public Skill(Identifier id, File file, SkillConfigSection config) {
		this.id = id;
		this.file = file;
		this.config = config;

		this.conditions = load(id, "conditions", () -> config.getConditions("conditions"), new ArrayList<>());
		this.mechanics = load(id, "mechanics", () -> config.getMechanics("mechanics"), new MechanicsExecutor());
		UrArsDebugger.debug("Skill '{}': {} condition(s), {} mechanic(s)", id, conditions.size(), mechanics.size());
	}

	private static <V> V load(Identifier id, String what, Callable<V> loader, V fallback) {
		try {
			return loader.call();
		} catch (Exception e) {
			UrArsDebugger.error("Failed to load " + what + " for skill '" + id + "'", e);
			return fallback;
		}
	}
	
	public void cast(SkillContext ctx) {
		if (!conditions.stream().allMatch(c -> c.execute(ctx))) return;
		mechanics.execute(ctx);
	}
}