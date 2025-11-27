package io.phanisment.urars.skill;

import net.minecraft.entity.Entity;

import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import io.phanisment.urars.skill.target.ILocationTarget;
import io.phanisment.urars.skill.target.INoTarget;
import io.phanisment.urars.util.TickScheduler;

import java.util.Optional;

public class SkillMechanic implements ISkillAction {
	protected final SkillLineConfig config;
	protected long delay = 0l;
	protected Optional<SkillTargeter> targeter = Optional.empty();
	protected String trigger = "";
	
	public SkillMechanic(SkillLineConfig config) {
		this.config = config;
		
		this.delay = config.getLong("delay", 0l);
		
		String targeter = config.getContext();
		if (targeter != null && targeter.startsWith("@")) {
			var targeter_config = new SkillLineConfig(targeter.substring(1));
			try {
				this.targeter = SkillManager.getTargeter(targeter_config);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String trigger = targeter_config.getContext();
			if (trigger != null && trigger.startsWith("~")) this.trigger = trigger.substring(1);
		}
	}
	
	public void addDelay(long duration) {
		this.delay += duration;
	}
	
	public long getDelay() {
		return this.delay;
	}
	
	public String getTrigger() {
		return this.trigger;
	}
	
	public void execute(SkillContext ctx) {
		if (delay > 0l) {
			TickScheduler.wait(this.delay, () -> this.cast(ctx));
			return;
		}
		this.cast(ctx);
	}
	
	private void cast(SkillContext ctx) {
		if (this.targeter.isPresent()) {
			SkillTargeter skill_targeter = targeter.get();
			for (Entity target : skill_targeter.getTarget(ctx)) {
				if (this instanceof INoTarget no_target && target == null) no_target.cast(ctx);
				if (this instanceof IEntityTarget entity_target && target != null) entity_target.castAtEntity(ctx, target);
				if (this instanceof ILocationTarget location_target && target != null && skill_targeter.is_location) location_target.castAtLocation(ctx, target.getWorld(), target.getPos());
			}
		} else if (this instanceof INoTarget no_target) no_target.cast(ctx);
	}
}