package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.exception.MathParserException;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import io.phanisment.urars.skill.target.INoTarget;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
	author = "Phanisment",
	key = "damage"
)
public class DamageMechanic extends SkillMechanic implements INoTarget, IEntityTarget {
	private final Node amount;

	public DamageMechanic(SkillLineConfig config) {
		super(config);
		try {
			if (config.singleArgument()) this.amount = config.getMathNumber(new String[]{null}, 1);
			else this.amount = config.getMathNumber(aliases("amount", "a"), 1);
		} catch (MathParserException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void cast(SkillContext ctx) {
		this.castAtEntity(ctx, ctx.entity());
	}
	
	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		target.hurtServer((ServerLevel)target.level(), target.damageSources().mobAttack(ctx.livingEntity()), amount.evalAsFloat(ctx.evalContext()));
	}
}