package io.phanisment.urars.mob;

import net.minecraft.util.Identifier;
import net.minecraft.resource.Resource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;

import io.phanisment.urars.mob.type.ConfigurableEntity;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.TriggerType;
import io.phanisment.urars.skill.config.SkillConfigSection;
import io.phanisment.urars.skill.mechanics.DelayMechanic;

import static io.phanisment.urars.UrArs.ID;

import java.util.List;
import java.util.Optional;

public class Mob {
	private final Identifier id;
	private final Resource resource;
	private final SkillConfigSection config;
	
	private String type;
	private Identifier model = MobConstant.MODEL;
	private Identifier texture = MobConstant.TEXTURE;
	private Identifier animation = MobConstant.ANIMATION;
	private List<SkillMechanic> mechanics;
	
	public Mob(Identifier id, Resource resource, SkillConfigSection config) {
		this.id = id;
		this.resource = resource;
		this.config = config;
		
		this.type = config.getString("type", "entity");
		SkillConfigSection res = config.getSection("resource").getAsSkillSection();
		this.model = res.getIdentifier("model", this.model);
		this.texture = res.getIdentifier("texture", this.texture);
		this.animation = res.getIdentifier("animation", this.animation);
		
		this.mechanics = config.getMechanics("mechanics");
	}
	
	public void spawn(World world, Vec3d pos) {
		var entity = new ConfigurableEntity(MobManager.CONFIGURABLE_ENTITY, world);
		entity.setPos(pos.getX(), pos.getY(), pos.getZ());
		entity.setMobId(id);
		this.cast(TriggerType.ON_PRE_SPAWN, new SkillContext(entity));
		world.spawnEntity(entity);
		this.cast(TriggerType.ON_SPAWN, new SkillContext(entity));
	}
	
	public void cast(TriggerType trigger, SkillContext ctx) {
		this.cast(trigger.alias(), ctx);
	}
	
	public void cast(String trigger, SkillContext ctx) {
		long delay = 0l;
		for (SkillMechanic mechanic : mechanics) {
			if (!trigger.equalsIgnoreCase(mechanic.getTrigger())) continue;
			if (mechanic instanceof DelayMechanic delay_mechanic) {
				delay += delay_mechanic.getDelay();
				continue;
			}
			if (delay > 0l) mechanic.addDelay(delay);
			mechanic.execute(ctx);
		}
	}
	
	public Identifier getModel() {
		return this.model;
	}
	
	public Identifier getTexture() {
		return this.texture;
	}
	
	public Identifier getAnimation() {
		return this.animation;
	}
	
	public SkillConfigSection getConfig() {
		return this.config;
	}
	
	public Identifier getId() {
		return this.id;
	}
}