package io.phanisment.urars.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import io.phanisment.urars.pack.PackManager;
import io.phanisment.urars.skill.SkillManager;

public class SkillReloadCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register((LiteralArgumentBuilder<ServerCommandSource>)CommandManager.literal("skillreload")
		.requires((source) -> source.hasPermissionLevel(2))
		.executes(context -> execute(context.getSource())));
	}
	
	private static int execute(ServerCommandSource source) {
		PackManager.load();
		SkillManager.load();
		source.sendFeedback(() -> Text.literal("Reloaded!"), false);
		return 1;
	}
}