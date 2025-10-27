package io.phanisment.urars.skill;

import net.minecraft.entity.Entity;

import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import io.phanisment.urars.skill.target.ILocationTarget;
import io.phanisment.urars.skill.target.INoTarget;
import io.phanisment.urars.skill.targeters.SelfTargeter;
import java.util.Optional;

public class SkillMechanic {
	protected final SkillLineConfig config;
	protected Optional<SkillTargeter> targeter = Optional.empty();
	
	public SkillMechanic(SkillLineConfig config) {
		this.config = config;
	}
	
	public void execute(SkillContext ctx) {
		String context = config.getContext();
		if ((context != null || context.isEmpty()) && context.startsWith("@")) {
			var target_config = new SkillLineConfig(context.substring(1));
			try {
				this.targeter = SkillManager.getTargeter(target_config);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (this.targeter.isPresent()) {
			for (Entity target : targeter.get().getTarget(ctx)) {
				if (this instanceof INoTarget no_target) no_target.cast(ctx);
				if (this instanceof IEntityTarget entity_target) entity_target.castAtEntity(ctx, target);
				if (this instanceof ILocationTarget location_target) location_target.castAtLocation(ctx, target.getWorld(), target.getPos());
			}
		} else if (this instanceof INoTarget no_target) no_target.cast(ctx);
	}
}