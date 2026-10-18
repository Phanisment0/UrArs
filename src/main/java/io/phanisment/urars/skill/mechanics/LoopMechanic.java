package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.parser.math.ast.NumberNode;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.INoTarget;
import net.minecraft.world.entity.Entity;
import io.phanisment.urars.skill.target.IEntityTarget;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
	author = "Phanisment",
	key = "loop"
)
public class LoopMechanic extends SkillMechanic implements INoTarget, IEntityTarget {
	private Node interval = new NumberNode(2);
	private MechanicsExecutor mechanics = new MechanicsExecutor();

	public LoopMechanic(SkillLineConfig config) {
		super(config);
		try {
			this.interval = config.getMathNumber(aliases("interval", "i"), 2);
			this.mechanics = config.getMechanics("do");
		} catch (Exception e) {
			// Ignored
		}
	}

	@Override
	public void cast(SkillContext ctx) {
		this.castAtEntity(ctx, ctx.entity());
	}
	
	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		for (int i = 0; i < interval.evalAsInt(ctx.evalContext()); i++) mechanics.execute(ctx);
	}
}