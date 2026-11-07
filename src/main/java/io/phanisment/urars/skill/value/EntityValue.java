package io.phanisment.urars.skill.value;

import io.phanisment.urars.parser.math.function.Function;
import io.phanisment.urars.parser.math.function.FunctionValue;
import io.phanisment.urars.parser.math.value.BooleanValue;
import io.phanisment.urars.parser.math.value.NumberValue;
import io.phanisment.urars.parser.math.value.ObjectValue;
import io.phanisment.urars.parser.math.value.StringValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class EntityValue extends ObjectValue {
	public EntityValue(Entity entity) {
		super(entity);

		// Position
		number("x", entity.getX());
		number("y", entity.getY());
		number("z", entity.getZ());

		number("block_x", entity.getBlockX());
		number("block_y", entity.getBlockY());
		number("block_z", entity.getBlockZ());

		// Rotation
		number("yaw", entity.getYRot());
		number("pitch", entity.getXRot());

		// Velocity
		Vec3 velocity = entity.getDeltaMovement();
		number("velocity_x", velocity.x);
		number("velocity_y", velocity.y);
		number("velocity_z", velocity.z);

		// Dimension
		number("width", entity.getBbWidth());
		number("height", entity.getBbHeight());
		number("eye_height", entity.getEyeHeight());

		// Information
		string("name", entity.getName().getString());
		string("uuid", entity.getStringUUID());
		string("type", entity.getType().toShortString());

		// State
		bool("alive", entity.isAlive());
		bool("on_ground", entity.onGround());
		bool("on_fire", entity.isOnFire());
		bool("in_water", entity.isInWater());
		bool("in_lava", entity.isInLava());
		bool("invisible", entity.isInvisible());
		bool("glowing", entity.isCurrentlyGlowing());
		bool("no_gravity", entity.isNoGravity());
		bool("silent", entity.isSilent());
		bool("sprinting", entity.isSprinting());
		bool("sneaking", entity.isCrouching());
		bool("swimming", entity.isSwimming());

		// Entity data
		number("ticks", entity.tickCount);
		number("fire_ticks", entity.getRemainingFireTicks());
		number("freeze_ticks", entity.getTicksFrozen());

		if (entity instanceof LivingEntity living) {
			// Health
			number("health", living.getHealth());
			number("max_health", living.getMaxHealth());
			number("absorption", living.getAbsorptionAmount());

			// Combat
			number("armor", living.getArmorValue());
			number("hurt_time", living.hurtTime);
			number("death_time", living.deathTime);

			// Other
			number("air", living.getAirSupply());
			number("max_air", living.getMaxAirSupply());
		}
	}

	private void number(String name, Number value) {
		member(name, new NumberValue(value));
	}

	private void string(String name, String value) {
		member(name, new StringValue(value));
	}

	private void bool(String name, boolean value) {
		member(name, new BooleanValue(value));
	}

	private void function(String name, Function value) {
		member(name, new FunctionValue(value));
	}
}