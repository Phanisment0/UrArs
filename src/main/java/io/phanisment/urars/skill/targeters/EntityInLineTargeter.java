package io.phanisment.urars.skill.targeters;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.EntityTargeter;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		key = "entityInLine"
)
public class EntityInLineTargeter extends EntityTargeter {
	protected final Node distance;
	protected final Node radius;

	public EntityInLineTargeter(SkillLineConfig config) {
		super(config);
		this.distance = config.getMathNumber(aliases("distance", "d"), 10);
		this.radius = config.getMathNumber(aliases("radius", "r"), 1);
	}

	@Override
	public Collection<Entity> getEntities(SkillContext ctx) {
		Entity caster = ctx.entity();

		double distance = this.distance.evalAsDouble(ctx.evalContext());
		double radius = this.radius.evalAsDouble(ctx.evalContext());

		if (distance < 0 || radius < 0) return List.of();

		Vec3 from = caster.getEyePosition();
		Vec3 direction = caster.getLookAngle().normalize();
		Vec3 to = from.add(direction.scale(distance));

		double radiusSq = radius * radius;
		double distanceSq = distance * distance;

		AABB box = new AABB(from, to).inflate(radius);
		List<Entity> result = new ArrayList<>();
		for (Entity entity : caster.level().getEntities(caster, box, Entity::isAlive)) {
			Vec3 offset = entity.position().subtract(from);

			double projection = offset.dot(direction);

			if (projection < 0 || projection > distance) continue;

			Vec3 closest = from.add(direction.scale(projection));

			if (entity.position().distanceToSqr(closest) > radiusSq) continue;
			if (entity.position().distanceToSqr(from) > distanceSq) continue;

			result.add(entity);
		}

		return result;
	}
}
