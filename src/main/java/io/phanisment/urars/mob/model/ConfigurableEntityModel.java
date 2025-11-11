package io.phanisment.urars.mob.model;

import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

import io.phanisment.urars.mob.Mob;
import io.phanisment.urars.mob.MobManager;
import io.phanisment.urars.mob.type.ConfigurableEntity;

import java.util.Optional;

public class ConfigurableEntityModel extends GeoModel<ConfigurableEntity> {
	@Override
	public Identifier getModelResource(ConfigurableEntity entity) {
		return entity.getModel();
	}
	
	@Override
	public Identifier getTextureResource(ConfigurableEntity entity) {
		return entity.getTexture();
	}
	
	@Override
	public Identifier getAnimationResource(ConfigurableEntity entity) {
		return entity.getAnimation();
	}
}