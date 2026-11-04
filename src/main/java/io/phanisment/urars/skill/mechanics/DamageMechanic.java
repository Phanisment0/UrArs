package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.damage.DamagingMechanic;
import net.minecraft.world.entity.Entity;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		key = "damage"
)
public class DamageMechanic extends DamagingMechanic {
	private final Node amount;

	public DamageMechanic(SkillLineConfig config) {
		super(config);
		if (config.singleArgument()) this.amount = config.getMathNumber(new String[]{null}, 1);
		else this.amount = config.getMathNumber(aliases("amount", "a"), 1);
	}

	@Override
	protected float calculateDamage(SkillContext ctx, Entity target) {
		return this.amount.evalAsFloat(ctx.evalContext());
	}
}