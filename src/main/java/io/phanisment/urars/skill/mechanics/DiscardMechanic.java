package io.phanisment.urars.skill.mechanics;

import net.minecraft.text.Text;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.INoTarget;
import io.phanisment.urars.skill.target.IEntityTarget;

@RegistryInfo(author="Phanisment", key="discard")
public class DiscardMechanic extends SkillMechanic implements INoTarget, IEntityTarget {
	public DiscardMechanic(SkillLineConfig config) {
		super(config);
	}
	
	@Override
	public void cast(SkillContext ctx) {
		this.castAtEntity(ctx, ctx.getCaster());
	}
	
	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		if (target instanceof PlayerEntity) return;
		target.discard();
	}
}