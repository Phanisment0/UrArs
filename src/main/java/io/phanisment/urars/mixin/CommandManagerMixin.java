package io.phanisment.urars.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import io.phanisment.urars.command.UrArsCommand;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	public void registerCommands(CommandManager.RegistrationEnvironment environment, CommandRegistryAccess commandRegistryAccess, CallbackInfo ci) {
		CommandDispatcher<ServerCommandSource> dispatcher = ((CommandManager)(Object)this).getDispatcher();
		
		UrArsCommand.register(dispatcher);
	}
}