package io.phanisment.urars;

import static io.phanisment.urars.UrArs.of;

import org.jspecify.annotations.NonNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

import io.phanisment.urars.component.MobComponent;
import net.minecraft.world.entity.Entity;

public class UrArsComponents implements EntityComponentInitializer {
	@SuppressWarnings("null")
	public static final ComponentKey<MobComponent> MOB = ComponentRegistry.getOrCreate(of("mob_components"), MobComponent.class);

	@Override
	public void registerEntityComponentFactories(@NonNull EntityComponentFactoryRegistry registry) {
		registry.registerFor(Entity.class, MOB, entity -> new MobComponent());
	}
}
