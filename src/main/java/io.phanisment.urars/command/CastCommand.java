package io.phanisment.urars.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import io.phanisment.urars.skill.Skill;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillManager;

import java.util.concurrent.CompletableFuture;

public class CastCommand {
	private static CompletableFuture<Suggestions> suggestSkills(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
		for (String name : SkillManager.getSkills()) {
			builder.suggest(name);
		}
		return builder.buildFuture();
	}
	
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register((LiteralArgumentBuilder<ServerCommandSource>) CommandManager.literal("cast")
		.requires((source) -> source.hasPermissionLevel(2))
		.then(CommandManager.argument("skill", StringArgumentType.word())
		.suggests(CastCommand::suggestSkills)
		.executes(context -> execute(context.getSource(), StringArgumentType.getString(context, "skill")))));
	}
	
	private static int execute(ServerCommandSource source, String str_skill) {
		var player = source.getPlayer();
		if (player == null) return 0;
		
		SkillManager.getSkill(str_skill).ifPresentOrElse(skill -> {
			skill.cast(new SkillContext(player));
			source.sendFeedback(() -> Text.literal("§aExecuted skill: " + str_skill), false);
		}, () -> {
			source.sendFeedback(() -> Text.literal("§cSkill not found: " + str_skill), false);
		});
		
		return 1;
	}
}