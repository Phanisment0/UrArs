package io.phanisment.urars.skill.targeters;

import io.phanisment.urars.exception.MathParserException;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.LocationTargeter;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.Location;

import static io.phanisment.urars.util.ActionUtils.aliases;

import java.util.Collection;
import java.util.List;

@RegistryInfo(
		author = "Phanisment",
		key = "forward"
)
public class ForwardTargeter extends LocationTargeter {
	protected final Node distance;
	protected final Node y_offset;
	protected final Node stop_at_block;
	protected final Node lock_pitch;

	public ForwardTargeter(SkillLineConfig config) {
		super(config);
		try {
			this.distance = config.getMathNumber(aliases("distance", "d"), 5);
			this.y_offset = config.getMathNumber(aliases("yOffset", "y"), 0);
			this.stop_at_block = config.getMathBoolean(aliases("stopAtBlock", "sab"), true);
			this.lock_pitch = config.getMathBoolean(aliases("lockPitch", "lp"), false);
		} catch (MathParserException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Collection<Location> getLocations(SkillContext ctx) {
		Entity caster = ctx.entity();
		ServerLevel level = (ServerLevel)caster.level();

		double dist = distance.evalAsDouble(ctx.evalContext());
		double y = y_offset.evalAsDouble(ctx.evalContext());

		Vec3 from = caster.getEyePosition();
		Vec3 direction = lock_pitch.evalAsBoolean(ctx.evalContext()) ? Vec3.directionFromRotation(0.0F, caster.getYRot()) : caster.getLookAngle();
		Vec3 to = from.add(direction.scale(dist));

		if (stop_at_block.evalAsBoolean(ctx.evalContext())) {
			HitResult hit = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster));
			if (hit.getType() != HitResult.Type.MISS) to = hit.getLocation();
		}

		return List.of(new Location(level, to.add(0, y, 0)));
	}
}