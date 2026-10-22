package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.UrArs;
import io.phanisment.urars.exception.MathParserException;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.parser.math.ast.NumberNode;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import io.phanisment.urars.skill.target.INoTarget;
import net.minecraft.world.entity.Entity;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		key = "setVelocity",
		aliases = { "velocity" }
)
public class SetVelocityMechanic extends SkillMechanic implements INoTarget, IEntityTarget {
	protected Node x = new NumberNode(0);
	protected Node y = new NumberNode(0);
	protected Node z = new NumberNode(0);

	public SetVelocityMechanic(SkillLineConfig config) {
		super(config);
		try {
			this.x = config.getMathNumber(aliases("velocityX", "vx", "x"), 0);
			this.y = config.getMathNumber(aliases("velocityY", "vy", "y"), 0);
			this.z = config.getMathNumber(aliases("velocityZ", "vz", "z"), 0);
		} catch (MathParserException e) {
			UrArs.LOGGER.error("Failed to parse arguments", e);
		}
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

	@Override
	public void cast(SkillContext ctx) {
		castAtEntity(ctx, ctx.entity());
	}
}
