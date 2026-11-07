package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.template.DamagingMechanic;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import static io.phanisment.urars.util.ActionUtils.aliases;
import static io.phanisment.urars.util.ActionUtils.aliasNull;

@RegistryInfo(
		author = "Phanisment",
		key = "percentDamage"
)
public class PercentDamageMechanic extends DamagingMechanic {
	private final Node percent;
	private final Node base;

	public PercentDamageMechanic(SkillLineConfig config) {
		super(config);
		if (config.singleArgument()) this.percent = config.getMathNumber(aliasNull(), 10);
		else this.percent = config.getMathNumber(aliases("percent", "p", "amount", "a"), 10);
		this.base = config.getMathString(aliases("base", "b"), "max");
	}

	@Override
	protected float calculateDamage(SkillContext ctx, Entity target) {
		if (!(target instanceof LivingEntity living)) return 0f;

		float pct = this.percent.evalAsFloat(ctx.evalContext()) / 100f;
		float max = living.getMaxHealth();
		float current = living.getHealth();

		float baseValue = switch (this.base.evalAsString(ctx.evalContext()).toLowerCase()) {
			case "current" -> current;
			case "missing" -> max - current;
			default -> max;
		};

		return baseValue * pct;
	}
}