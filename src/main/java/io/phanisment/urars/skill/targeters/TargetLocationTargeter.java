package io.phanisment.urars.skill.targeters;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.LocationTargeter;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.Location;
import io.phanisment.urars.util.TargeterUtils;

import java.util.Collection;
import java.util.List;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		key = "targetLocation"
)
public class TargetLocationTargeter extends LocationTargeter {
	private final Node range;

	public TargetLocationTargeter(SkillLineConfig config) {
		super(config);
		this.range = config.getMathNumber(aliases("range", "r"), 16);
	}

	@Override
	public Collection<Location> getLocations(SkillContext ctx) {
		Location hit = TargeterUtils.getTargetedLocation(ctx.entity(), range.evalAsDouble(ctx.evalContext()));
		return hit == null ? List.of() : List.of(hit);
	}
}