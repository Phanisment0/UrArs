package io.phanisment.urars.skill.mechanics;

import net.minecraft.util.Identifier;
import net.minecraft.entity.Entity;

import io.phanisment.urars.mob.type.IConfigurableEntity;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;

@RegistryInfo(author="Phanisment", key="changemodel", aliases={"model"})
public class ChangeModelMechanic extends SkillMechanic implements IEntityTarget {
	private final Identifier model;
	
	public ChangeModelMechanic(SkillLineConfig config) {
		super(config);
		this.model = Identifier.of(config.getString(new String[]{"path", "p"}));
	}
	
	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		if (target instanceof IConfigurableEntity cfg && model != null) cfg.setModel(this.model);
	}
}