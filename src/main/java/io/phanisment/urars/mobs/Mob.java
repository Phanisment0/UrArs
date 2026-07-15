package io.phanisment.urars.mobs;

import java.util.ArrayList;
import java.util.List;

import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.TriggerType;
import io.phanisment.urars.skill.config.SkillConfigSection;
import io.phanisment.urars.util.Location;
import io.phanisment.urars.util.MobBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.Entity;

public class Mob {
	private final Identifier id;
	@SuppressWarnings("unused")
	private final Resource resource;
	@SuppressWarnings("unused")
	private final SkillConfigSection config;

	@SuppressWarnings("unused")
	private Identifier type;
	private List<SkillMechanic> mechanics = new ArrayList<>();

	public Mob(final Identifier id, final Resource resource, final SkillConfigSection config) {
		this.id = id;
		this.resource = resource;
		this.config = config;

		this.type = config.getIdentifier("type", Identifier.fromNamespaceAndPath("minecraft", "zombie"));
		this.mechanics = config.getMechanics("mechanics");
	}

	public void spawn(final Location loc) {
		Entity entity = MobBuilder.create(id, loc).beforeSpawn(e -> {
			this.cast(TriggerType.ON_PRE_SPAWN, new SkillContext(e));
		}).spawn();
		this.cast(TriggerType.ON_SPAWN, new SkillContext(entity));
	}

	private void cast(final TriggerType trigger, final SkillContext ctx) {
		this.cast(trigger.toString(), ctx);
	}

	private void cast(final String trigger, final SkillContext ctx) {
		for (SkillMechanic mechanic : mechanics) {
			if (!trigger.equalsIgnoreCase(mechanic.getTrigger())) continue;
			mechanic.execute(ctx);
		}
	}
}
