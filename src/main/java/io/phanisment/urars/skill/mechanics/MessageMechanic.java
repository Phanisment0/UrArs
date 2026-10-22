package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.UrArs;
import io.phanisment.urars.exception.MathParserException;
import io.phanisment.urars.parser.math.ast.BooleanNode;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.parser.math.ast.StringNode;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.INoTarget;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import io.phanisment.urars.skill.target.IEntityTarget;

import static io.phanisment.urars.util.ActionUtils.*;

@RegistryInfo(
	author = "Phanisment",
	key = "message"
)
public class MessageMechanic extends SkillMechanic implements INoTarget, IEntityTarget {
	protected Node message = new StringNode("Hi!");
	protected Node overlay = new BooleanNode(false);
	
	public MessageMechanic(SkillLineConfig config) {
		super(config);
		try {
			if (config.singleArgument()) this.message = config.getMathString(aliasNull(), "Hi!");
			else this.message = config.getMathString(aliases("text", "txt", "t", "message", "msg", "m"), "Hi!");
			this.overlay = config.getMathBoolean(aliases("overlay", "o"));
		} catch (MathParserException e) {
			UrArs.LOGGER.error("Failed to parse arguments", e);
		}
	}
	
	@Override
	public void cast(SkillContext ctx) {
		this.castAtEntity(ctx, ctx.entity());
	}
	
	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		if (target instanceof ServerPlayer player) {
			if (overlay.evalAsBoolean(ctx.evalContext())) player.sendOverlayMessage(Component.literal(message.evalAsString(ctx.evalContext())));
			else player.sendSystemMessage(Component.literal(message.evalAsString(ctx.evalContext())));
		}
	}
}