package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.exception.MathParserException;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import io.phanisment.urars.skill.target.INoTarget;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;

@RegistryInfo(
	author = "Phanisment",
	key = "damage"
)
public class DamageMechanic extends SkillMechanic implements INoTarget, IEntityTarget {
	private final Node amount;

	public DamageMechanic(SkillLineConfig config) {
		super(config);
		try {
			if (config.singleArgument()) this.amount = config.getNumberMath(new String[]{null}, 1);
			else this.amount = config.getNumberMath(new String[]{"amount", "a"}, 1);
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
		target.hurtServer((ServerLevel)target.level(), target.damageSources().mobAttack(ctx.livingEntity()), Double.valueOf(amount.evalAsDouble(ctx.evalContext())).floatValue());
	}
}