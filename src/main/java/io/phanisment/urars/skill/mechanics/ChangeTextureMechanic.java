package io.phanisment.urars.skill.mechanics;

import net.minecraft.util.Identifier;
import net.minecraft.entity.Entity;

import io.phanisment.urars.mob.type.IConfigurableEntity;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;

@RegistryInfo(author="Phanisment", key="changetexture", aliases={"texture"})
public class ChangeTextureMechanic extends SkillMechanic implements IEntityTarget {
	private final Identifier texture;
	
	public ChangeTextureMechanic(SkillLineConfig config) {
		super(config);
		this.texture = Identifier.of(config.getString(new String[]{"path", "p"}));
	}
	
	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		if (target instanceof IConfigurableEntity cfg && texture != null) cfg.setTexture(this.texture);
	}
}