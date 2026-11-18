
package io.phanisment.urars.mixin;

import io.phanisment.urars.util.ItemUtils;
import io.phanisment.urars.skill.TriggerType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {

	/**
	 * Handles right-click air interactions.
	 */
	@Inject(method = "useItem", at = @At("HEAD"))
	private void urars$onUseItem(ServerPlayer player, Level level, ItemStack item, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		ItemUtils.castItem(hand, player, TriggerType.ON_USE);
	}

	/*
	 * Handles right-click block interactions.
	@Inject(method = "useItemOn", at = @At("HEAD"))
	private void urars$onUseItemOn(ServerPlayer player, Level level, ItemStack item, InteractionHand hand, BlockHitResult hit_result, CallbackInfoReturnable<InteractionResult> cir) {
		ItemUtils.castItem(hand, player, TriggerType.ON_USE);
	}*/
}
