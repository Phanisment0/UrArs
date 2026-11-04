package io.phanisment.urars.skill.targeters;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.EntityTargeter;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.HashSet;

import static io.phanisment.urars.util.ActionUtils.aliases;
import static io.phanisment.urars.util.ActionUtils.aliasNull;

@RegistryInfo(
		author = "Phanisment",
		key = "player",
		aliases = { "p" }
)
public class PlayerTargeter extends EntityTargeter {
	protected final Node name;

	public PlayerTargeter(SkillLineConfig config) {
		super(config);
		if (config.singleArgument()) this.name = config.getMathString(aliasNull(), "");
		else this.name = config.getMathString(aliases("name", "n"), "");
	}

	@Override
	public Collection<Entity> getEntities(SkillContext ctx) {
		Collection<Entity> targets = new HashSet<>();

		String playerName = this.name.evalAsString(ctx.evalContext());
		if (playerName == null || playerName.isBlank()) return targets;

		MinecraftServer server = ctx.level().getServer();
		if (server == null) return targets;

		ServerPlayer player = server.getPlayerList().getPlayerByName(playerName);
		if (player != null) targets.add(player);

		return targets;
	}
}