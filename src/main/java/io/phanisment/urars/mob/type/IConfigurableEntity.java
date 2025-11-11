package io.phanisment.urars.mob.type;

import net.minecraft.util.Identifier;

public interface IConfigurableEntity {
	void setModel(Identifier path);
	void setTexture(Identifier path);
	void setAnimation(Identifier path);
	Identifier getModel();
	Identifier getTexture();
	Identifier getAnimation();
}