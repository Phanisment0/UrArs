package io.phanisment.urars.skill;

import net.minecraft.entity.Entity;

import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class SkillTargeter implements ISkillAction {
	protected final SkillLineConfig config;
	protected boolean is_location = false;
	
	public SkillTargeter(SkillLineConfig config) {
		this.config = config;
	}
	
	public Collection<Location> getLocation(SkillContext ctx) {
		List<Location> locs = new ArrayList<>();
		for (Entity entity : getTarget(ctx)) {
			locs.add(new Location(entity.getWorld(), entity.getPos()));
		}
		return locs;
	}
	
	public abstract Collection<Entity> getTarget(SkillContext ctx);
}