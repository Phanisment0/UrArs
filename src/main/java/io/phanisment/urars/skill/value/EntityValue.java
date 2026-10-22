package io.phanisment.urars.skill.value;

import io.phanisment.urars.parser.math.function.FunctionValue;
import io.phanisment.urars.parser.math.value.NumberValue;
import io.phanisment.urars.parser.math.value.ObjectValue;
import io.phanisment.urars.parser.math.value.StringValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EntityValue extends ObjectValue {
	public EntityValue(Entity entity) {
		super(entity);

		member("x", new NumberValue(entity.getX()));
		member("y", new NumberValue(entity.getY()));
		member("z", new NumberValue(entity.getZ()));

		member("name", new StringValue(entity.getName().getString()));
		member("type", new StringValue(entity.getType().toShortString()));

		if (entity instanceof LivingEntity living) {
			member("hp", new NumberValue(living.getHealth()));
			member("max_hp", new NumberValue(living.getMaxHealth()));
		}
	}
}