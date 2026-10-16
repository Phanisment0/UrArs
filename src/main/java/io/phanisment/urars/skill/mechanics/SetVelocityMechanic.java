package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.exception.MathParserException;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import io.phanisment.urars.skill.target.INoTarget;
import net.minecraft.world.entity.Entity;

@RegistryInfo(
		author = "Phanisment",
		key = "setVelocity",
		aliases = { "velocity" }
)
public class SetVelocityMechanic extends SkillMechanic implements INoTarget, IEntityTarget {
	private final Node x;
	private final Node y;
	private final Node z;

	public SetVelocityMechanic(SkillLineConfig config) {
		super(config);
		try {
			this.x = config.getMathNumber(new String[]{"x"}, 0);
			this.y = config.getMathNumber(new String[]{"y"}, 0);
			this.z = config.getMathNumber(new String[]{"z"}, 0);
		} catch (MathParserException e) {
			throw new RuntimeException(e);
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
