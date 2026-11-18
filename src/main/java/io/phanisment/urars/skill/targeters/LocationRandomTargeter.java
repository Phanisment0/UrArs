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
import java.util.concurrent.ThreadLocalRandom;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		key = "locationRandom"
)
public class LocationRandomTargeter extends LocationTargeter {
	protected final Node count;
	protected final Node y_offset;

	public LocationRandomTargeter(SkillLineConfig config) {
		super(config);
		this.count = config.getMathNumber(aliases("count", "c"), 1);
		this.y_offset = config.getMathNumber(aliases("yOffset", "y"), 0);
	}

	@Override
	public Collection<Location> getLocations(SkillContext ctx) {
		Entity caster = ctx.entity();

		double radius = this.radius.evalAsDouble(ctx.evalContext());
		int count = this.count.evalAsInt(ctx.evalContext());
		double yOffset = this.y_offset.evalAsDouble(ctx.evalContext());

		if (radius < 0 || count <= 0) return List.of();

		// Prevent excessive location generation.
		count = Math.min(count, 256);

		ThreadLocalRandom random = ThreadLocalRandom.current();

		Vec3 center = caster.position().add(0, yOffset, 0);

		float yaw = caster.getYRot();
		float pitch = caster.getXRot();

		List<Location> result = new ArrayList<>(count);

		for (int i = 0; i < count; i++) {
			double angle = random.nextDouble() * Math.PI * 2.0;
			double distance = Math.sqrt(random.nextDouble()) * radius;

			double x = Math.cos(angle) * distance;
			double z = Math.sin(angle) * distance;

			Vec3 pos = center.add(x, 0, z);

			result.add(new Location(caster.level(), pos, yaw, pitch));
		}

		return result;
	}
}

