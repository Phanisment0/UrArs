package io.phanisment.urars.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import io.phanisment.urars.UrArs;
import io.phanisment.urars.lib.file_parser.parser.LineParser;
import io.phanisment.urars.mob.Mob;
import io.phanisment.urars.mob.MobManager;
import io.phanisment.urars.skill.Skill;
import io.phanisment.urars.skill.SkillActionFactory;
import io.phanisment.urars.skill.SkillCondition;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillManager;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.Location;
import io.phanisment.urars.util.UrArsDebugger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.Commands.argument;
import static io.phanisment.urars.UrArs.LOGGER;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class UrArsCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(literal("urars")
				.requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
				.then(literal("cast")
						.then(argument("skill", IdentifierArgument.id())
								.suggests(UrArsCommand::cast_suggest)
								.executes(UrArsCommand::cast_default)
								.then(argument("targets", EntityArgument.entities())
										.executes(UrArsCommand::castWithTargets)
								)
						)
				).then(literal("mechanic")
						.then(argument("line", StringArgumentType.greedyString())
								.executes(UrArsCommand::mechanic)
						)
				).then(literal("condition")
						.then(argument("line", StringArgumentType.greedyString())
								.executes(UrArsCommand::condition)
						)
				).then(literal("spawn")
						.then(argument("mob", IdentifierArgument.id())
								.suggests(UrArsCommand::spawn_suggest)
								.executes(UrArsCommand::spawn_default)
								.then(argument("pos", Vec3Argument.vec3())
										.executes(UrArsCommand::spawn_at_pos)
								)
						)
				).then(literal("debug")
						.executes(UrArsCommand::debug_show)
						.then(argument("level", StringArgumentType.word())
								.suggests(UrArsCommand::debug_suggest)
								.executes(UrArsCommand::debug_set)
						)
				).then(literal("reload").executes(UrArsCommand::reload))
		);
	}

	private static CompletableFuture<Suggestions> debug_suggest(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
		String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
		for (UrArsDebugger.Level level : UrArsDebugger.Level.values()) {
			String name = level.name().toLowerCase(Locale.ROOT);
			if (name.startsWith(remaining)) builder.suggest(name);
		}
		return builder.buildFuture();
	}

	private static int debug_show(CommandContext<CommandSourceStack> ctx) {
		ctx.getSource().sendSuccess(() -> Component.literal("Debug level: " + UrArsDebugger.level()), false);
		return 1;
	}

	private static int debug_set(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack source = ctx.getSource();
		String input = StringArgumentType.getString(ctx, "level");

		UrArsDebugger.Level level = parseLevel(input);
		if (level == null) {
			source.sendFailure(Component.literal("Unknown level '" + input + "'. Use off, info, debug, trace (or 0-3)."));
			return 0;
		}

		UrArsDebugger.Level old = UrArsDebugger.level();
		UrArsDebugger.level(level);

		UrArsDebugger.info("Debug level changed {} -> {} by '{}'", old, level, actor(source));
		source.sendSuccess(() -> Component.literal("Debug level set to " + level), true);
		return 1;
	}

	private static UrArsDebugger.Level parseLevel(String input) {
		try {
			int n = Integer.parseInt(input);
			UrArsDebugger.Level[] all = UrArsDebugger.Level.values();
			return n >= 0 && n < all.length ? all[n] : null;
		} catch (NumberFormatException ignored) {
			// not a number, try by name
		}
		try {
			return UrArsDebugger.Level.valueOf(input.toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private static int reload(CommandContext<CommandSourceStack> ctx) {
		UrArsDebugger.debug("Reload requested by '{}'", actor(ctx.getSource()));
		long start = System.nanoTime();
		UrArs.reload();
		UrArsDebugger.debug("Reload finished in {} ms", (System.nanoTime() - start) / 1_000_000);
		return 1;
	}

	private static CompletableFuture<Suggestions> spawn_suggest(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
		String remaining = builder.getRemaining().toLowerCase();
		int count = 0;
		for (Identifier id : MobManager.getMobs()) {
			String mob = id.toString();
			if (mob.startsWith(remaining)) {
				builder.suggest(mob);
				count++;
			}
		}
		UrArsDebugger.trace("Spawn suggestions for '{}': {} match(es)", remaining, count);
		return builder.buildFuture();
	}

	private static CompletableFuture<Suggestions> cast_suggest(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
		String remaining = builder.getRemaining().toLowerCase();
		int count = 0;
		for (Identifier id : SkillManager.getSkills()) {
			String skill = id.toString();
			if (skill.startsWith(remaining)) {
				builder.suggest(skill);
				count++;
			}
		}
		UrArsDebugger.trace("Cast suggestions for '{}': {} match(es)", remaining, count);
		return builder.buildFuture();
	}

	private static int spawn_default(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack source = ctx.getSource();
		Vec3 pos = source.getPosition();
		return executeSpawn(source, pos, ctx);
	}

	private static int spawn_at_pos(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack source = ctx.getSource();
		Vec3 pos = Vec3Argument.getVec3(ctx, "pos");
		return executeSpawn(source, pos, ctx);
	}

	private static int executeSpawn(CommandSourceStack source, Vec3 pos, CommandContext<CommandSourceStack> ctx) {
		Identifier mob_id = IdentifierArgument.getId(ctx, "mob");
		UrArsDebugger.debug("Spawn requested: mob='{}', pos={}, actor='{}'", mob_id, pos, actor(source));
		Optional<Mob> mob = MobManager.getMob(mob_id);

		if (mob.isEmpty()) {
			LOGGER.warn("Cannot spawn unknown mob '{}'. Actor: '{}'", mob_id, actor(source));
			source.sendFailure(Component.literal("Unknown mob: " + mob_id));
			return 0;
		}

		try {
			mob.get().spawn(new Location(source.getLevel(), pos));
			UrArsDebugger.debug("Spawned mob '{}' at {}", mob_id, pos);
			source.sendSuccess(() -> Component.literal("Successfully spawned " + mob_id), false);
			return 1;
		} catch (Exception e) {
			logFailure(source, "spawn mob", "mob=" + mob_id + ", position=" + pos, e);
		}
		return 0;
	}

	private static int cast_default(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
		CommandSourceStack source = ctx.getSource();
		ServerPlayer player = source.getPlayerOrException();
		return executeCast(source, Collections.singleton(player), ctx);
	}

	private static int castWithTargets(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
		CommandSourceStack source = ctx.getSource();
		Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");
		return executeCast(source, targets, ctx);
	}

	private static int executeCast(CommandSourceStack source, Collection<? extends Entity> targets, CommandContext<CommandSourceStack> ctx) {
		Identifier skill_id = IdentifierArgument.getId(ctx, "skill");
		UrArsDebugger.debug("Cast requested: skill='{}', {} target(s), actor='{}'", skill_id, targets.size(), actor(source));
		Optional<Skill> skill = SkillManager.getSkillOptional(skill_id);

		if (skill.isEmpty()) {
			LOGGER.warn("Cannot cast unknown skill '{}'", skill_id);
			source.sendFailure(Component.literal("Unknown skill: " + skill_id));
			return 0;
		}

		int succeeded = 0, failed = 0;
		long start = System.nanoTime();

		for (Entity target : targets) {
			try {
				UrArsDebugger.trace("Casting '{}' on '{}' ({})", skill_id, target.getName().getString(), target.getUUID());
				skill.get().cast(new SkillContext(target));
				succeeded++;
			} catch (Exception e) {
				failed++;
				LOGGER.error("Failed to cast skill '{}'. Target: '{}', UUID: {}", skill_id, target.getName().getString(), target.getUUID(), e);
			}
		}

		UrArsDebugger.debug("Cast '{}' done: {} succeeded, {} failed ({} ms)", skill_id, succeeded, failed, (System.nanoTime() - start) / 1_000_000);

		if (failed == 0) {
			final int succeeded_final = succeeded;
			source.sendSuccess(() -> Component.literal("Casted skill " + skill_id + " on " + succeeded_final + " target(s)"), false);
			return 1;
		}

		source.sendFailure(Component.literal("Skill " + skill_id + ": " + succeeded + " succeeded, " + failed + " failed. Check server log."));
		return succeeded > 0 ? 1 : 0;
	}

	private static int mechanic(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack source = ctx.getSource();
		ServerPlayer player = source.getEntity() instanceof ServerPlayer server_player ? server_player : null;

		if (player == null) {
			source.sendFailure(Component.literal("This command requires a player."));
			return 0;
		}

		String line = StringArgumentType.getString(ctx, "line");
		UrArsDebugger.debug("Mechanic requested by '{}': {}", actor(source), line);
		try {
			LineParser parser = new LineParser(line);
			parser.parse();
			SkillLineConfig config = new SkillLineConfig(parser.config());
			UrArsDebugger.trace("Mechanic parsed: {}", parser);

			SkillActionFactory<SkillMechanic> factory = SkillManager.getMechanic(config.key());
			if (factory == null) {
				LOGGER.warn("Unknown mechanic '{}'. Input: '{}'", config.key(), line);
				source.sendFailure(Component.literal("Mechanic not found: " + config.key()));
				return 0;
			}

			SkillMechanic mechanic = factory.create(config);
			if (mechanic == null) throw new IllegalStateException("Mechanic factory returned null: " + config.key());

			mechanic.execute(new SkillContext(player));
			UrArsDebugger.debug("Mechanic '{}' executed", config.key());
			source.sendSuccess(() -> Component.literal("Executed mechanic: " + config.key()), false);
			return 1;
		} catch (Exception e) {
			logFailure(source, "execute mechanic", "input=" + line, e);
		}
		return 0;
	}

	private static int condition(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack source = ctx.getSource();
		ServerPlayer player = source.getEntity() instanceof ServerPlayer server_player ? server_player : null;

		if (player == null) {
			source.sendFailure(Component.literal("This command requires a player."));
			return 0;
		}

		String line = StringArgumentType.getString(ctx, "line");
		UrArsDebugger.debug("Condition requested by '{}': {}", actor(source), line);
		try {
			LineParser parser = new LineParser(line);
			parser.parse();
			SkillLineConfig config = new SkillLineConfig(parser.config());
			UrArsDebugger.trace("Condition parsed: {}", parser);

			SkillActionFactory<SkillCondition> factory = SkillManager.getCondition(config.key());
			if (factory == null) {
				LOGGER.warn("Unknown condition '{}'. Input: '{}'", config.key(), line);
				source.sendFailure(Component.literal("Condition not found: " + config.key()));
				return 0;
			}

			SkillCondition condition = factory.create(config);
			if (condition == null) throw new IllegalStateException("Condition factory returned null: " + config.key());

			boolean result = condition.check(new SkillContext(player));
			UrArsDebugger.debug("Condition '{}' -> {}", config.key(), result);
			source.sendSuccess(() -> Component.literal("Executed condition '" + config.key() + "' with result: " + result), false);
			return 1;
		} catch (Exception e) {
			logFailure(source, "execute condition", "input=" + line, e);
		}
		return 0;
	}

	private static String actor(CommandSourceStack source) {
		return source.getEntity() == null ? "server/console" : source.getEntity().getName().getString();
	}

	private static void logFailure(CommandSourceStack source, String operation, String details, Exception exception) {
		LOGGER.error("Command '{}' failed. Actor: '{}'. Details: {}", operation, actor(source), details, exception);
		source.sendFailure(Component.literal("Failed to " + operation + ". Check the server log for details."));
	}
}