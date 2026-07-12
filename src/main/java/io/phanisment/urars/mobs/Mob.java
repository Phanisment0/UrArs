package io.phanisment.urars.mobs;

import static io.phanisment.urars.UrArs.LOGGER;

import java.util.ArrayList;
import java.util.List;

import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.TriggerType;
import io.phanisment.urars.skill.config.SkillConfigSection;
import io.phanisment.urars.util.Location;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;

public class Mob {
	private final Identifier id;
	private final Resource resource;
	private final SkillConfigSection config;

	private Identifier type;
	private List<SkillMechanic> mechanics = new ArrayList<>();

	public Mob(Identifier id, Resource resource, SkillConfigSection config) {
		this.id = id;
		this.resource = resource;
		this.config = config;

		this.type = config.getIdentifier("type", Identifier.fromNamespaceAndPath("minecraft", "zombie"));
		this.mechanics = config.getMechanics("mechanics");
	}

	public void spawn(Location loc) {
		var nbt = new CompoundTag();
		nbt.putString("urars:mob_type", type.toString());

		Entity entity = EntityType.loadEntityRecursive(nbt, loc.level(), EntitySpawnReason.COMMAND, e -> {
			e.snapTo(loc.pos());
			this.cast(TriggerType.ON_PRE_SPAWN, new SkillContext(e));
			return e;
		});

		if (entity == null) {
			LOGGER.warn("Unknown entity type: " + type);
			return;
		}
		
		if (entity instanceof net.minecraft.world.entity.Mob mob) mob.finalizeSpawn((ServerLevel)loc.level(), ((ServerLevel)loc.level()).getCurrentDifficultyAt(entity.blockPosition()), EntitySpawnReason.COMMAND, (SpawnGroupData)null);
		this.cast(TriggerType.ON_SPAWN, new SkillContext(entity));
	}

	private void cast(TriggerType trigger, SkillContext ctx) {
		this.cast(trigger.toString(), ctx);
	}

	private void cast(String trigger, SkillContext ctx) {
		for (SkillMechanic mechanic : mechanics) {
			if (!trigger.equalsIgnoreCase(mechanic.getTrigger())) continue;
			mechanic.execute(ctx);
		}
	}
}
