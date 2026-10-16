package io.phanisment.urars.skill;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class EntityTargeter extends SkillTargeter {
	public EntityTargeter(SkillLineConfig config) {
		super(config);
	}

	@Override
	public final boolean isLocation() {
		return false;
	}

	@Override
	public Collection<Location> getLocations(SkillContext ctx) {
		List<Location> locs = new ArrayList<>();
		for (Entity entity : getEntities(ctx)) locs.add(new Location((ServerLevel)entity.level(), entity.position()));
		return locs;
	}
}