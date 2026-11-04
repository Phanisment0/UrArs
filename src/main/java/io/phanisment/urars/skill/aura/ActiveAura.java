package io.phanisment.urars.skill.aura;

import io.phanisment.urars.skill.SkillContext;
import net.minecraft.world.entity.Entity;

import java.util.HashSet;
import java.util.Set;

public final class ActiveAura {
	private final String name;
	private final Aura owner;
	private final SkillContext context;
	private final Entity target;

	public long remaining;
	public boolean alive = true;
	public final Set<String> running = new  HashSet<>();

	public ActiveAura(String name, Aura owner, SkillContext context, Entity target, long remaining) {
		this.name = name;
		this.owner = owner;
		this.context = context;
		this.target = target;
	}

	public String name() {
		return name;
	}

	public Aura owner() {
		return owner;
	}

	public SkillContext context() {
		return context;
	}

	public Entity target() {
		return target;
	}
}
