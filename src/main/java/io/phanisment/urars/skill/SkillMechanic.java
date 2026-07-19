package io.phanisment.urars.skill;

import org.jspecify.annotations.Nullable;

import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import io.phanisment.urars.skill.target.ILocationTarget;
import io.phanisment.urars.skill.target.INoTarget;
import io.phanisment.urars.util.Location;
import io.phanisment.urars.util.TickScheduler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class SkillMechanic implements ISkillAction {
	protected final SkillLineConfig config;
	protected long delay = 0l;
	@Nullable
	protected SkillTargeter skill_targeter;
	protected String trigger = "";

	public SkillMechanic(SkillLineConfig config) {
		this.config = config;
		
		this.delay = config.getLong("delay", 0l);
		
		String context = config.getContext();
		if (context != null && context.startsWith("@")) {
			var targeter_config = new SkillLineConfig(context.substring(1));
			this.skill_targeter = SkillManager.getTargeter(targeter_config);
			
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
		long total_delay = this.delay + ctx.global_delay;
		if (total_delay > 0l) TickScheduler.wait(total_delay, () -> this.executeAtTarget(ctx));
		else this.executeAtTarget(ctx);
	}
	
	@SuppressWarnings("null")
	private void executeAtTarget(SkillContext ctx) {
		if (this.skill_targeter != null) {
			for (Entity target : skill_targeter.getTarget(ctx)) {
				if (this instanceof INoTarget no_target && target == null) no_target.cast(ctx);
				if (this instanceof IEntityTarget entity_target && target != null) entity_target.castAtEntity(ctx, target);
				if (this instanceof ILocationTarget location_target && target != null && skill_targeter.is_location) location_target.castAtLocation(ctx, new Location((ServerLevel)target.level(), target.position()));
			}
		} else if (this instanceof INoTarget no_target) no_target.cast(ctx);
	}
}