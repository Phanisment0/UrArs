package io.phanisment.urars.mixin;

import io.phanisment.urars.skill.aura.AuraManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "triggerOnDeathMobEffects", at = @At("HEAD"))
	private void urars$triggerOnDeathMobEffects(final ServerLevel level, final Entity.RemovalReason reason, final CallbackInfo ci) {
		LivingEntity entity = (LivingEntity)(Object)this;
		AuraManager.removeAll(entity);
	}
}