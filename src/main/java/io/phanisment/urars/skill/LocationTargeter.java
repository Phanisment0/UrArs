package io.phanisment.urars.skill;

import io.phanisment.urars.exception.MathParserException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.Location;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class LocationTargeter extends SkillTargeter {
	/** Radius used when an entity-only mechanic is cast at these locations. */
	protected final Node radius;

	public LocationTargeter(SkillLineConfig config) {
		super(config);
		try {
			this.radius = config.getMathNumber(new String[]{"radius", "r"}, 1);
		} catch (MathParserException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public final boolean isLocation() {
		return true;
	}

	@Override
	public Collection<Entity> getEntities(SkillContext ctx) {
		double r = radius.evalAsDouble(ctx.evalContext());
		double r_sq = r * r;
		Set<Entity> result = new LinkedHashSet<>(); // dedupe overlapping locations

		for (Location loc : getLocations(ctx)) {
			ServerLevel level = loc.level();
			Vec3 pos = loc.pos();
			AABB box = new AABB(pos, pos).inflate(r);
			result.addAll(level.getEntities(
					(Entity)null,
					box,
					e -> e != ctx.entity() && e.distanceToSqr(pos) <= r_sq
			));
		}
		return result;
	}
}