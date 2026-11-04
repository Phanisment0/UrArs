package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import net.minecraft.world.entity.Entity;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		key = "setVelocity",
		aliases = { "velocity" }
)
public class SetVelocityMechanic extends SkillMechanic implements IEntityTarget {
	protected final Node x;
	protected final Node y;
	protected final Node z;

	public SetVelocityMechanic(SkillLineConfig config) {
		super(config);
		this.x = config.getMathNumber(aliases("velocityX", "vx", "x"), 0);
		this.y = config.getMathNumber(aliases("velocityY", "vy", "y"), 0);
		this.z = config.getMathNumber(aliases("velocityZ", "vz", "z"), 0);
	}

	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		target.setDeltaMovement(
				x.evalAsDouble(ctx.evalContext()),
				y.evalAsDouble(ctx.evalContext()),
				z.evalAsDouble(ctx.evalContext())
		);
		target.hurtMarked = true;
	}
}
