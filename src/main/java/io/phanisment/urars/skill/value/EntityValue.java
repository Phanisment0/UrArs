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

		member("x", new FunctionValue(args -> new NumberValue(entity.getX())));
		member("y", new FunctionValue(args -> new NumberValue(entity.getY())));
		member("z", new FunctionValue(args -> new NumberValue(entity.getZ())));

		member("name", new FunctionValue(args -> new StringValue(entity.getName().getString())));
		member("height", new FunctionValue(args -> new NumberValue(entity.getBbHeight())));

		if (entity instanceof LivingEntity living) {
			member("health", new FunctionValue(args -> new NumberValue(living.getHealth())));
			member("maxHealth", new FunctionValue(args -> new NumberValue(living.getMaxHealth())));
		}
	}
}