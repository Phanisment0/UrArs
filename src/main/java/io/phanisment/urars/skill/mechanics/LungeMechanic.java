package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.exception.MathParserException;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import io.phanisment.urars.skill.target.ILocationTarget;
import io.phanisment.urars.skill.target.INoTarget;
import io.phanisment.urars.util.Location;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import static io.phanisment.urars.util.ActionUtils.*;

@RegistryInfo(
		author = "Phanisment",
		key = "lunge"
)
public class LungeMechanic extends SkillMechanic implements INoTarget, IEntityTarget, ILocationTarget {
	protected final Node velocity;     // base speed
	protected final Node y_boost;      // extra upward velocity
	protected final Node scale;        // 1 = multiply speed by distance
	protected final Node min_distance; // closer than this: don't lunge
	protected final Node max_distance; // farther than this: don't lunge (also the range for no-target)
	protected final Node max_speed;    // clamp on final speed

	public LungeMechanic(SkillLineConfig config) {
		super(config);
		try {
			this.velocity     = config.getMathNumber(aliases("velocity", "v"), 1);
			this.y_boost      = config.getMathNumber(aliases("yboost", "y"), 0.2);
			this.scale        = config.getMathNumber(aliases("scale", "s"), 0);
			this.min_distance = config.getMathNumber(aliases("mindistance", "min"), 1);
			this.max_distance = config.getMathNumber(aliases("maxdistance", "max"), 32);
			this.max_speed    = config.getMathNumber(aliases("maxspeed"), 4);
		} catch (MathParserException e) {
			throw new RuntimeException(e);
		}
	}

	// Target is an entity: aim at its body center.
	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		Entity caster = ctx.entity();
		if (caster == null || target == caster) return;
		if (caster.level() != target.level()) return;
		lunge(ctx, caster, target.getBoundingBox().getCenter());
	}

	// Target is a location: aim at that exact point.
	@Override
	public void castAtLocation(SkillContext ctx, Location location) {
		Entity caster = ctx.entity();
		if (caster == null) return;
		if (caster.level() != location.level()) return;
		lunge(ctx, caster, location.pos());
	}

	// No target: lunge along the look direction, up to max distance.
	@Override
	public void cast(SkillContext ctx) {
		Entity caster = ctx.entity();
		if (caster == null) return;
		double range = max_distance.evalAsDouble(ctx.evalContext());
		Vec3 point = center(caster).add(caster.getLookAngle().scale(range));
		lunge(ctx, caster, point);
	}

	private void lunge(SkillContext ctx, Entity caster, Vec3 point) {
		var eval = ctx.evalContext();

		Vec3 delta = point.subtract(center(caster));
		double distance = delta.length();

		double min = min_distance.evalAsDouble(eval);
		double max = max_distance.evalAsDouble(eval);
		if (distance < min || distance > max || distance < 1.0E-6) return;

		double speed = velocity.evalAsDouble(eval);
		if (scale.evalAsDouble(eval) != 0) speed *= distance;
		speed = Math.min(speed, max_speed.evalAsDouble(eval));

		Vec3 dir = delta.scale(1.0 / distance); // normalized
		caster.setDeltaMovement(
				dir.x * speed,
				dir.y * speed + y_boost.evalAsDouble(eval),
				dir.z * speed
		);
		caster.hurtMarked = true; // sync velocity to the client (needed for players)
		caster.resetFallDistance();
	}

	private static Vec3 center(Entity e) {
		return e.position().add(0, e.getBbHeight() / 2.0, 0);
	}
}