package io.phanisment.urars.skill.targeters;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.LocationTargeter;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.Location;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		key = "locationRing"
)
public class LocationRingTargeter extends LocationTargeter {
	protected final Node points;
	protected final Node y_offset;

	public LocationRingTargeter(SkillLineConfig config) {
		super(config);
		this.points = config.getMathNumber(aliases("points", "p"), 8);
		this.y_offset = config.getMathNumber(aliases("yOffset", "y"), 0);
	}

	@Override
	public Collection<Location> getLocations(SkillContext ctx) {
		Entity caster = ctx.entity();

		double radius = this.radius.evalAsDouble(ctx.evalContext());
		int points = this.points.evalAsInt(ctx.evalContext());
		double yOffset = this.y_offset.evalAsDouble(ctx.evalContext());

		if (radius < 0 || points <= 0) return List.of();

		// Prevent excessive location generation.
		points = Math.min(points, 256);

		Vec3 center = caster.position().add(0, yOffset, 0);

		float yaw = caster.getYRot();
		float pitch = caster.getXRot();

		List<Location> result = new ArrayList<>(points);

		for (int i = 0; i < points; i++) {
			double angle = Math.PI * 2.0 * i / points;

			double x = Math.cos(angle) * radius;
			double z = Math.sin(angle) * radius;

			Vec3 pos = center.add(x, 0, z);

			result.add(new Location(caster.level(), pos, yaw, pitch));
		}

		return result;
	}
}

