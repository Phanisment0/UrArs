package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;

import io.phanisment.urars.skill.template.ModeledMechanic;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import net.minecraft.world.entity.Entity;

@RegistryInfo(
		author = "Phanisment",
		key = "bodyRotation",
		namespace = "bettermodel",
		mods = { "bettermodel" }
)
public class ModelBodyRotationMechanic extends ModeledMechanic {
	protected final Node head_uneven;
	protected final Node max_head;
	protected final Node min_head;
	protected final Node body_uneven;
	protected final Node max_body;
	protected final Node min_body;
	protected final Node player_mode;
	protected final Node stable;
	protected final Node r_delay;
	protected final Node r_duration;

	public ModelBodyRotationMechanic(SkillLineConfig config) {
		super(config);
		head_uneven = config.getMathBoolean("headUneven", "hu", "head");
		max_head = config.getMathNumber("maxHead", "mh", "mxh");
		min_head = config.getMathNumber("minHead", "mnh");
		body_uneven = config.getMathNumber("bodyUneven", "bu", "body");
		max_body = config.getMathNumber("maxBody", "mb", "mxb");
		min_body = config.getMathNumber("minBody", "mnb");
		player_mode = config.getMathNumber("playerMode", "m", "mode", "player");
		stable = config.getMathNumber("stable", "s");
		r_delay = config.getMathNumber("rDelay", "rde");
		r_duration = config.getMathNumber("rDuration", "rdu");
	}

	@Override
	public void update(SkillContext ctx, Entity target, EntityTrackerRegistry registry, String model_id, EntityTracker tracker) {
		tracker.bodyRotator().setValue(s -> {
			s.setHeadUneven(head_uneven.evalAsBoolean(ctx.evalContext()));
			s.setMaxHead(max_head.evalAsFloat(ctx.evalContext()));
			s.setMinHead(min_head.evalAsFloat(ctx.evalContext()));
			s.setBodyUneven(body_uneven.evalAsBoolean(ctx.evalContext()));
			s.setMaxBody(max_body.evalAsFloat(ctx.evalContext()));
			s.setMinBody(min_body.evalAsFloat(ctx.evalContext()));
			s.setPlayerMode(player_mode.evalAsBoolean(ctx.evalContext()));
			s.setStable(stable.evalAsFloat(ctx.evalContext()));
			s.setRotationDelay(r_delay.evalAsInt(ctx.evalContext()));
			s.setRotationDuration(r_duration.evalAsInt(ctx.evalContext()));
		});
	}
}
