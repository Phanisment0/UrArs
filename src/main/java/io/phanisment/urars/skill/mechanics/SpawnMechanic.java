package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.mob.MobManager;
import io.phanisment.urars.mob.MobMeta;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import io.phanisment.urars.skill.target.ILocationTarget;
import io.phanisment.urars.util.Location;

import static io.phanisment.urars.util.ActionUtils.aliases;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

@RegistryInfo(
		author = "Phanisment",
		key = "spawn"
)
public class SpawnMechanic extends SkillMechanic implements IEntityTarget, ILocationTarget {
	protected final Node mob;

	public SpawnMechanic(SkillLineConfig config) {
		super(config);
		mob = config.getMathString(aliases("mob", "m", "type", "t"), "");
	}

	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		castAtLocation(ctx, new Location(target.level(), target.position()));
	}

	@Override
	public void castAtLocation(SkillContext ctx, Location location) {
		Identifier id = Identifier.tryParse(mob.evalAsString(ctx.evalContext()));
		if (id == null) return;

		MobMeta meta = MobManager.getMob(id);
		if (meta == null) return;

		meta.spawn(location);
	}
}