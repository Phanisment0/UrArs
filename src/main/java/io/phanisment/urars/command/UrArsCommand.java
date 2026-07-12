package io.phanisment.urars.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import io.phanisment.urars.mobs.MobManager;
import io.phanisment.urars.skill.SkillCondition;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.Location;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.Commands.argument;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Custom command for this mod
 */
public class UrArsCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register((LiteralArgumentBuilder<CommandSourceStack>)literal("urars")
		.then(literal("cast")
			.then(argument("skill", IdentifierArgument.id())
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
		)
		.then(literal("spawn")
			.then(argument("mob", IdentifierArgument.id())
				.suggests(UrArsCommand::spawn_suggest)
				.executes(UrArsCommand::spawn)
			)
		));
	}

	private static CompletableFuture<Suggestions> spawn_suggest(CommandContext<CommandSourceStack>  context, SuggestionsBuilder builder) {
		String remaining = builder.getRemaining().toLowerCase();
		
		for (Identifier id : MobManager.getMobs()) {
			String mob = id.toString();
			if (mob.startsWith(remaining)) builder.suggest(mob);
		}
		return builder.buildFuture();
	}
	
	private static int spawn(CommandContext<CommandSourceStack>  ctx) {
		CommandSourceStack source = ctx.getSource();
		ServerPlayer player = source.getPlayer();
		if (player == null) return 0;
		
		Identifier mob_id = IdentifierArgument.getId(ctx, "mob");
		MobManager.getMob(mob_id).ifPresentOrElse(mob -> {
			mob.spawn(new Location(source.getLevel(), player.position()));
			source.sendSuccess(() -> Component.literal("Spawned mob: " + mob_id), false);
		}, () -> {
			source.sendSuccess(() -> Component.literal("Unknown mob: " + mob_id), false);
		});
		
		return 1;
	}
	
	private static CompletableFuture<Suggestions> cast_suggest(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
		String remaining = builder.getRemaining().toLowerCase();
		
		for (Identifier id : SkillManager.getSkills()) {
			String skill = id.toString();
			if (skill.startsWith(remaining)) builder.suggest(skill);
		}
		return builder.buildFuture();
	}
	
	private static int cast(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack source = ctx.getSource();
		ServerPlayer player = source.getPlayer();
		if (player == null) return 0;
		
		Identifier skill_id = IdentifierArgument.getId(ctx, "skill");
		SkillManager.getSkill(skill_id).ifPresentOrElse(skill -> {
			source.sendSuccess(() -> Component.literal("Cast Skill: " + skill_id), false);
			skill.cast(new SkillContext(player));
		}, () -> {
			source.sendFailure(Component.literal("Unknown skill: " + skill_id));
		});
		return 1;
	}

	private static int mechanic(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack source = ctx.getSource();
		ServerPlayer player = source.getPlayer();
		if (player == null) return 0;
		
		String line = StringArgumentType.getString(ctx, "line");
		try {
			SkillLineConfig config = new SkillLineConfig(line);
			Optional<SkillMechanic> mechanic = SkillManager.getMechanic(config);
			
			if (mechanic.isPresent()) {
				source.sendSuccess(() -> Component.literal("Executed mechanic: " + config.getKey()), false);
				mechanic.get().execute(new SkillContext(player));
			} else {
				source.sendFailure(Component.literal("Mechanic not found: " + config.getKey()));
			}
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			source.sendFailure(Component.literal("Failed to execute mechanic: " + e.getMessage()));
			return 0;
		}
	}

	private static int condition(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack source = ctx.getSource();
		ServerPlayer player = source.getPlayer();
		if (player == null) return 0;
		
		String line = StringArgumentType.getString(ctx, "line");
		try {
			SkillLineConfig config = new SkillLineConfig(line);
			Optional<SkillCondition> condition = SkillManager.getCondition(config);
			
			if (condition.isPresent()) {
				boolean result = condition.get().execute(new SkillContext(player));
				source.sendSuccess(() -> Component.literal("Condition result: " + result), false);
			} else {
				source.sendFailure(Component.literal("Condition not found: " + config.getKey()));
			}
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			source.sendFailure(Component.literal("Failed to execute condition: " + e.getMessage()));
			return 0;
		}
	}
}