package io.phanisment.urars.mixin;

import io.phanisment.urars.skill.TriggerType;
import io.phanisment.urars.util.TriggerUtils;
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
		TriggerUtils.castEntity(entity, TriggerType.ON_LOAD);
	}
}