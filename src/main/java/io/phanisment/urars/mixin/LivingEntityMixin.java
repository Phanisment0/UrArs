package io.phanisment.urars.mixin;

import io.phanisment.urars.skill.TriggerType;
import io.phanisment.urars.skill.template.aura.AuraManager;
import io.phanisment.urars.util.TriggerUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.phanisment.urars.util.ItemUtils.castItem;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "triggerOnDeathMobEffects", at = @At("HEAD"))
	private void urars$triggerOnDeathMobEffects(final ServerLevel level, final Entity.RemovalReason reason, final CallbackInfo ci) {
		LivingEntity entity = (LivingEntity)(Object)this;
		TriggerUtils.castEntity(entity, TriggerType.ON_DEATH);
		AuraManager.removeAll(entity);
	}

	@Inject(method = "startUsingItem", at = @At("HEAD"))
	private void urars$onStartUsingItem(InteractionHand hand, CallbackInfo ci) {
		LivingEntity entity = (LivingEntity)(Object)this;
		TriggerUtils.castEntity(entity, TriggerType.ON_START_USE_ITEM);
		castItem(hand, entity, TriggerType.ON_START_USE_ITEM);
	}

	@Inject(method = "stopUsingItem", at = @At("HEAD"))
	private void urars$onStopUsingItem(CallbackInfo ci) {
		LivingEntity entity = (LivingEntity)(Object)this;
		TriggerUtils.castEntity(entity, TriggerType.ON_STOP_USING_ITEM);
		castItem(entity, TriggerType.ON_STOP_USING_ITEM);
	}

	@Inject(method = "swing(Lnet/minecraft/world/InteractionHand;)V", at = @At("HEAD"))
	private void urars$onSwing(InteractionHand hand, CallbackInfo ci) {
		LivingEntity entity = (LivingEntity)(Object)this;
		TriggerUtils.castEntity(entity, TriggerType.ON_SWING);
		castItem(hand, entity, TriggerType.ON_SWING);
	}
}