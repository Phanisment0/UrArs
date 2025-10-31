package io.phanisment.urars.command;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.skill.SkillCondition;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.config.SkillLineConfig;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Custom command for this mod
 */
public class UrArsCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register((LiteralArgumentBuilder<ServerCommandSource>)literal("urars")
		.requires(source -> source.hasPermissionLevel(2))
		.then(literal("cast")
			.then(argument("skill", IdentifierArgumentType.identifier())
				.suggests(UrArsCommand::cast_suggest)
				.executes(UrArsCommand::cast)
			)
		)
		.then(literal("mechanic")
			.then(argument("line", StringArgumentType.greedyString())
				.executes(UrArsCommand::mechanic)
			)
		)
		.then(literal("condition")
			.then(argument("line", StringArgumentType.greedyString())
				.executes(UrArsCommand::condition)
			)
		));
	}
	
	private static CompletableFuture<Suggestions> cast_suggest(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
		String remaining = builder.getRemaining().toLowerCase();
		
		for (Identifier id : SkillManager.getSkills()) {
			String skill = id.toString();
			if (skill.startsWith(remaining)) builder.suggest(skill);
		}
		return builder.buildFuture();
	}
	
	private static int cast(CommandContext<ServerCommandSource> ctx) {
		ServerCommandSource source = ctx.getSource();
		ServerPlayerEntity player = source.getPlayer();
		if (player == null) return 0;
		
		Identifier skill_id = IdentifierArgumentType.getIdentifier(ctx, "skill");
		SkillManager.getSkill(skill_id).ifPresentOrElse(skill -> {
			skill.cast(new SkillContext(player));
			source.sendFeedback(() -> Text.literal("§aCast skill: " + skill_id), false);
		}, () -> {
			source.sendFeedback(() -> Text.literal("§cUnknown skill: " + skill_id), false);
		});
		return 1;
	}
	
	private static int mechanic(CommandContext<ServerCommandSource> ctx) {
		ServerCommandSource source = ctx.getSource();
		ServerPlayerEntity player = source.getPlayer();
		if (player == null) return 0;
		
		String line = StringArgumentType.getString(ctx, "line");
		try {
			SkillLineConfig config = new SkillLineConfig(line);
			Optional<SkillMechanic> mechanic = SkillManager.getMechanic(config);
			
			if (mechanic.isPresent()) {
				mechanic.get().execute(new SkillContext(player));
				source.sendFeedback(() -> Text.literal("§aExecuted mechanic: " + config.getKey()), false);
			} else {
				source.sendFeedback(() -> Text.literal("§cMechanic not found: " + config.getKey()), false);
			}
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			source.sendFeedback(() -> Text.literal("§cFailed to execute mechanic: " + e.getMessage()), false);
			return 0;
		}
	}
	
	private static int condition(CommandContext<ServerCommandSource> ctx) {
		ServerCommandSource source = ctx.getSource();
		ServerPlayerEntity player = source.getPlayer();
		if (player == null) return 0;
		
		String line = StringArgumentType.getString(ctx, "line");
		try {
			SkillLineConfig config = new SkillLineConfig(line);
			Optional<SkillCondition> condition = SkillManager.getCondition(config);
			
			if (condition.isPresent()) {
				boolean result = condition.get().execute(new SkillContext(player));
				source.sendFeedback(() -> Text.literal("§aCondition result: " + result), false);
			} else {
				source.sendFeedback(() -> Text.literal("§cCondition not found: " + config.getKey()), false);
			}
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			source.sendFeedback(() -> Text.literal("§cFailed to execute condition: " + e.getMessage()), false);
			return 0;
		}
	}
}