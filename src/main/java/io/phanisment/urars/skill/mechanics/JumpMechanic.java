package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import net.minecraft.world.entity.Entity;

import static io.phanisment.urars.util.ActionUtils.aliases;
import static io.phanisment.urars.util.ActionUtils.aliasNull;

@RegistryInfo(
		author = "Phanisment",
		key = "jump",
		empty_args = true
)
public class JumpMechanic extends SkillMechanic implements IEntityTarget {
	protected static final double JUMP_FORCE = 0.42;
	protected final Node velocity;

	public JumpMechanic(SkillLineConfig config) {
		super(config);
		if (config.singleArgument()) velocity = config.getMathNumber(aliasNull(), JUMP_FORCE);
		else velocity = config.getMathNumber(aliases("velocity", "v"), JUMP_FORCE);
	}

	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		target.setDeltaMovement(0, velocity.evalAsDouble(ctx.evalContext()), 0);
		target.hurtMarked = true;
	}
}