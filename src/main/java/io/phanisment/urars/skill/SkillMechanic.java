package io.phanisment.urars.skill;

import net.minecraft.entity.Entity;

import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import io.phanisment.urars.skill.target.ILocationTarget;
import io.phanisment.urars.skill.target.INoTarget;

import java.util.Optional;

public class SkillMechanic implements ISkillAction {
	protected final SkillLineConfig config;
	protected Optional<SkillTargeter> targeter = Optional.empty();
	
	public SkillMechanic(SkillLineConfig config) {
		this.config = config;
		
		String context = config.getContext();
		if (context != null && context.startsWith("@")) {
			var target_config = new SkillLineConfig(context.substring(1));
			try {
				this.targeter = SkillManager.getTargeter(target_config);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void execute(SkillContext ctx) {
		if (this.targeter.isPresent()) {
			for (Entity target : targeter.get().getTarget(ctx)) {
				if (this instanceof INoTarget no_target) no_target.cast(ctx);
				if (this instanceof IEntityTarget entity_target) entity_target.castAtEntity(ctx, target);
				if (this instanceof ILocationTarget location_target) location_target.castAtLocation(ctx, target.getWorld(), target.getPos());
			}
		} else if (this instanceof INoTarget no_target) no_target.cast(ctx);
	}
}