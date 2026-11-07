package io.phanisment.urars.mixin;

import io.phanisment.urars.skill.aura.AuraManager;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerMixin {

	@Shadow
	public ServerPlayer player;

	@Inject(method = "onDisconnect", at = @At("HEAD"))
	private void urars$onDisconnect(DisconnectionDetails details, CallbackInfo ci) {
		AuraManager.removeAll(player);
	}
}