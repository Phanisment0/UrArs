package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.INoTarget;
import net.minecraft.world.entity.Entity;
import io.phanisment.urars.skill.target.IEntityTarget;

@RegistryInfo(
	author = "Phanisment",
	key = "loop"
)
public class LoopMechanic extends SkillMechanic implements INoTarget, IEntityTarget {
	private final int interval;
	private final MechanicsExecutor mechanics;
	public LoopMechanic(SkillLineConfig config) {
		super(config);

		this.interval = config.getInt(new String[] {"interval", "i"}, 2);
		this.mechanics = config.getMechanics("do");
	}
	
	@Override
	public void cast(SkillContext ctx) {
		this.castAtEntity(ctx, ctx.entity());
	}
	
	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		for (int i = 0; i < interval; i++) mechanics.execute(ctx);
	}
}