package io.phanisment.urars.skill.targeters;

import java.util.Collection;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.EntityTargeter;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		key = "entityInNear",
		aliases = { "eir" }
)
public class EntityInNearTargeter extends EntityTargeter {
	protected final Node radius;
	protected final Node living_only;

	public EntityInNearTargeter(SkillLineConfig config) {
		super(config);
		radius = config.getMathNumber(aliases("radius", "r"), 5d);
		living_only = config.getMathBoolean(aliases("livingOnly", "living", "l"), true);
	}

	@Override
	public Collection<Entity> getEntities(SkillContext ctx) {
		Entity caster = ctx.entity();
		if (caster == null) return null;
		AABB area = caster.getBoundingBox().inflate(radius.evalAsDouble(ctx.evalContext()));
		if (living_only.evalAsBoolean(ctx.evalContext())) return caster.level().getEntities(caster, area, Entity::isAlive);
		return caster.level().getEntities(caster, area, _ -> true);
	}
}
