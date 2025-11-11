package io.phanisment.urars.mob.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import io.phanisment.urars.mob.type.ConfigurableEntity;
import io.phanisment.urars.mob.model.ConfigurableEntityModel;

public class ConfigurableEntityRender extends GeoEntityRenderer<ConfigurableEntity> {
	public ConfigurableEntityRender(EntityRendererFactory.Context context) {
		super(context, new ConfigurableEntityModel());
	}
}