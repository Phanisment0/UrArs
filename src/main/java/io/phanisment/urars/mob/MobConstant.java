package io.phanisment.urars.mob;

import net.minecraft.util.Identifier;
import static io.phanisment.urars.UrArs.ID;

public interface MobConstant {
	Identifier MODEL = Identifier.of(ID, "geo/configurable_entity.geo.json");
	Identifier TEXTURE = Identifier.of(ID, "textures/entity/configurable_entity.png");
	Identifier ANIMATION = Identifier.of(ID, "animations/configurable_entity.animation.json");
}