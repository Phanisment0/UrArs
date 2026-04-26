package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.INoTarget;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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
		if (target instanceof Player) return;
		target.discard();
	}
}