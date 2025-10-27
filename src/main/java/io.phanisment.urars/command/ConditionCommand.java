package io.phanisment.urars.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.skill.SkillCondition;
import io.phanisment.urars.skill.config.SkillLineConfig;

import java.util.Optional;

public class ConditionCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register((LiteralArgumentBuilder<ServerCommandSource>)CommandManager.literal("condition")
		.requires((source) -> source.hasPermissionLevel(2)).then(CommandManager.argument("line", StringArgumentType.greedyString())
		.executes(context -> execute(context.getSource(), StringArgumentType.getString(context, "line")))));
	}
	
	private static int execute(ServerCommandSource source, String line) {
		try {
			var player = source.getPlayer();
			if (player == null) return 0;
			
			var config = new SkillLineConfig(line);
			Optional<SkillCondition> condition = SkillManager.getCondition(config);
			boolean result = condition.get().execute(new SkillContext(player));
			if (condition.isPresent()) source.sendFeedback(() -> Text.literal("§aExecuted condition: " + result), false);
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			source.sendFeedback(() -> Text.literal("§cFailed execute the condition: " + e.getMessage()), false);
			return 0;
		}
	}
}