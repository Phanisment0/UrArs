
package io.phanisment.urars.skill.targeters;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.LocationTargeter;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.Location;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.List;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		key = "forward"
)
public class ForwardTargeter extends LocationTargeter {
	protected final Node distance;
	protected final Node y_offset;
	protected final Node stop_at_block;
	protected final Node lock_pitch;
	protected final Node use_eye_location;
	protected final Node rotate;

	public ForwardTargeter(SkillLineConfig config) {
		super(config);
		this.distance = config.getMathNumber(aliases("forward", "amount", "distance", "f", "a", "d"), 5);
		this.y_offset = config.getMathNumber(aliases("yOffset", "y"), 0);
		this.stop_at_block = config.getMathBoolean(aliases("stopAtBlock", "sab"), true);
		this.lock_pitch = config.getMathBoolean(aliases("lockPitch", "lp"), false);
		this.use_eye_location = config.getMathBoolean(aliases("useEyeLocation", "uel"), true);
		this.rotate = config.getMathBoolean(aliases("rotate", "r"), true);
	}

	@Override
	public Collection<Location> getLocations(SkillContext ctx) {
		Entity caster = ctx.entity();

		double dist = distance.evalAsDouble(ctx.evalContext());
		double y = y_offset.evalAsDouble(ctx.evalContext());

		float yaw = rotate.evalAsBoolean(ctx.evalContext()) ? caster.getYRot() : 0f;
		float pitch = rotate.evalAsBoolean(ctx.evalContext()) ? caster.getXRot() : 0f;

		Vec3 from = use_eye_location.evalAsBoolean(ctx.evalContext()) ? caster.getEyePosition() : caster.position();
		Vec3 direction = lock_pitch.evalAsBoolean(ctx.evalContext()) ? Vec3.directionFromRotation(0.0F, caster.getYRot()) : caster.getLookAngle();
		Vec3 to = from.add(direction.scale(dist));

		if (stop_at_block.evalAsBoolean(ctx.evalContext())) {
			HitResult hit = caster.level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster));
			if (hit.getType() != HitResult.Type.MISS) to = hit.getLocation();
		}

		to = to.add(0, y, 0);
		return List.of(new Location(caster.level(), to, yaw, pitch));
	}
}