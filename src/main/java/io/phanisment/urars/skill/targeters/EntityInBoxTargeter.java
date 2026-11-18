
package io.phanisment.urars.skill.targeters;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.EntityTargeter;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.OBB;
import io.phanisment.urars.util.TargeterUtils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		key = "entityInBox",
		aliases = { "obb" }
)
public class EntityInBoxTargeter extends EntityTargeter {
	protected final Node width;
	protected final Node height;
	protected final Node length;

	protected final Node forward;
	protected final Node x_offset;
	protected final Node y_offset;

	protected final Node rotate;
	protected final Node lock_pitch;
	protected final Node roll;

	protected final Node use_eye_location;
	protected final Node draw_point;
	protected final Node max_targets;

	public EntityInBoxTargeter(SkillLineConfig config) {
		super(config);

		// Dimensions
		this.width = config.getMathNumber(aliases("width", "w"), 3);
		this.height = config.getMathNumber(aliases("height", "h"), 2);
		this.length = config.getMathNumber(aliases("length", "l"), 4);

		// Position
		this.forward = config.getMathNumber(aliases("forward", "f"), 0.5);
		this.x_offset = config.getMathNumber(aliases("xOffset", "x"), 0);
		this.y_offset = config.getMathNumber(aliases("yOffset", "y"), 0);

		// Rotation
		this.rotate = config.getMathBoolean(aliases("rotate", "r"), true);
		this.lock_pitch = config.getMathBoolean(aliases("lockPitch", "lp"), false);
		this.roll = config.getMathNumber(aliases("roll"), 0);

		// Options
		this.use_eye_location = config.getMathBoolean(aliases("useEyeLocation", "uel"), false);
		this.draw_point = config.getMathBoolean(aliases("drawPoint", "dp"), false);
		this.max_targets = config.getMathNumber(aliases("maxTargets", "mt"), 0);
	}

	@Override
	public Collection<Entity> getEntities(SkillContext ctx) {
		Entity caster = ctx.entity();

		// Evaluate dimensions
		double width = this.width.evalAsDouble(ctx.evalContext());
		double height = this.height.evalAsDouble(ctx.evalContext());
		double length = this.length.evalAsDouble(ctx.evalContext());
		if (width <= 0 || height <= 0 || length <= 0) return List.of();

		// Evaluate position
		double forward = this.forward.evalAsDouble(ctx.evalContext());
		double x_offset = this.x_offset.evalAsDouble(ctx.evalContext());
		double y_offset = this.y_offset.evalAsDouble(ctx.evalContext());
		Vec3 pivot = use_eye_location.evalAsBoolean(ctx.evalContext()) ? caster.getEyePosition() : caster.position();

		// Evaluate rotation
		boolean rotate = this.rotate.evalAsBoolean(ctx.evalContext());
		boolean lock_pitch = this.lock_pitch.evalAsBoolean(ctx.evalContext());

		Collection<Entity> entities;

		if (rotate) {
			// Rotated bounding box
			float yaw = caster.getYRot();
			float pitch = lock_pitch ? 0f : caster.getXRot();
			float roll = this.roll.evalAsFloat(ctx.evalContext());
			Vec3 corner_a = new Vec3(x_offset - width / 2, y_offset, forward);
			Vec3 corner_b = new Vec3(x_offset + width / 2, y_offset + height, forward + length);
			OBB box = OBB.ofRelative(pivot, corner_a, corner_b, yaw, pitch, roll);

			// Debug visualization
			if (draw_point.evalAsBoolean(ctx.evalContext())) TargeterUtils.drawBox(caster.level(), box);
			entities = box.getEntities(caster.level(), entity -> entity != caster && entity.isAlive());
		} else {
			// Axis-aligned bounding box
			AABB box = new AABB( pivot.x + x_offset - width / 2, pivot.y + y_offset, pivot.z + forward, pivot.x + x_offset + width / 2, pivot.y + y_offset + height, pivot.z + forward + length);

			// Debug visualization
			if (draw_point.evalAsBoolean(ctx.evalContext())) TargeterUtils.drawBox(ctx.serverLevel(), box);
			entities = caster.level().getEntities(caster, box, entity -> entity != caster && entity.isAlive());
		}

		// Evaluate maximum targets
		int max_targets = Math.max(0, this.max_targets.evalAsInt(ctx.evalContext()));

		// No target limit
		if (max_targets == 0) return entities;

		// Apply target limit
		List<Entity> result = new ArrayList<>(Math.min(max_targets, entities.size()));

		for (Entity entity : entities) {
			if (result.size() >= max_targets) break;
			result.add(entity);
		}

		return result;
	}
}