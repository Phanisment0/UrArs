package io.phanisment.urars.skill.targeters;

import io.phanisment.urars.skill.LocationTargeter;
import io.phanisment.urars.util.Location;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillTargeter;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@RegistryInfo(
	author = "Phanisment",
	key = "selfLocation"
)
public class SelfLocationTargeter extends LocationTargeter {
	public SelfLocationTargeter(SkillLineConfig config) {
		super(config);
	}

	@Override
	public Collection<Location> getLocations(SkillContext ctx) {
		Entity caster = ctx.entity();
		if (caster == null) return new ArrayList<>();
		return List.of(new Location((ServerLevel)caster.level(), caster.position()));
	}
}