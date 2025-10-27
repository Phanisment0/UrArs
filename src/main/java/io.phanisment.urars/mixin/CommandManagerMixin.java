package io.phanisment.urars.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import io.phanisment.urars.command.MechanicCommand;
import io.phanisment.urars.command.ConditionCommand;
import io.phanisment.urars.command.SkillReloadCommand;
import io.phanisment.urars.command.CastCommand;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	public void registerCommands(CommandManager.RegistrationEnvironment environment, CommandRegistryAccess commandRegistryAccess, CallbackInfo ci) {
		CommandDispatcher<ServerCommandSource> dispatcher = ((CommandManager)(Object)this).getDispatcher();
		
		MechanicCommand.register(dispatcher);
		ConditionCommand.register(dispatcher);
		SkillReloadCommand.register(dispatcher);
		CastCommand.register(dispatcher);
	}
}