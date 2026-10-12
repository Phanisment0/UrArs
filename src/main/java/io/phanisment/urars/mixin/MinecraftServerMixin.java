package io.phanisment.urars.mixin;

import io.phanisment.urars.UrArs;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Inject(method = "tickServer", at = @At("TAIL"))
	public void urars$tickServer(CallbackInfo ci) {
		UrArs.tick((MinecraftServer)(Object)this);
	}
}
