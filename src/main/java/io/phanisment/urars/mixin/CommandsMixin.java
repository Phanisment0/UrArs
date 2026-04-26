package io.phanisment.urars.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.brigadier.CommandDispatcher;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import io.phanisment.urars.command.UrArsCommand;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

@Mixin(Commands.class)
public class CommandsMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	public void registerCommands(final Commands.CommandSelection command_selection, final CommandBuildContext context, CallbackInfo ci) {
		CommandDispatcher<CommandSourceStack> dispatcher = ((Commands)(Object)this).getDispatcher();
		
		UrArsCommand.register(dispatcher);
	}
}