package io.phanisment.urars.mixin;

import io.phanisment.urars.UrArsComponents;
import io.phanisment.urars.component.MobComponent;
import io.phanisment.urars.mob.MobManager;
import io.phanisment.urars.mob.MobMeta;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.server.level.ServerLevel$EntityCallbacks")
public class EntityLoadMixin {
	@Inject(method = "onTrackingStart(Lnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"))
	private void urars$onLoad(Entity entity, CallbackInfo ci) {
		if (entity instanceof Player) return;
		if (!(entity instanceof Mob)) return;
		MobComponent comp = UrArsComponents.MOB.get(entity);
		String id = comp.id();
		if (id == null || id.isEmpty()) return;

		MobMeta mob = MobManager.getMob(Identifier.tryParse(id));
		if (mob != null) mob.applyAi(entity);
	}
}