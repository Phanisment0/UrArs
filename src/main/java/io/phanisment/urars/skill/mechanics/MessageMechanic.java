package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import io.phanisment.urars.skill.target.IEntityTarget;

import static io.phanisment.urars.util.ActionUtils.*;

@RegistryInfo(
		author = "Phanisment",
		key = "message",
		aliases = {
				"msg"
		},
		empty_args = true
)
public class MessageMechanic extends SkillMechanic implements IEntityTarget {
	protected final Node message;
	protected final Node overlay;
	
	public MessageMechanic(SkillLineConfig config) {
		super(config);
		if (config.singleArgument()) this.message = config.getMathString(aliasNull(), "Hi!");
		else this.message = config.getMathString(aliases("text", "txt", "t", "message", "msg", "m"), "Hi!");
		this.overlay = config.getMathBoolean(aliases("overlay", "o"));
	}
	
	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		if (target instanceof ServerPlayer player) {
			if (overlay.evalAsBoolean(ctx.evalContext())) player.sendOverlayMessage(Component.literal(message.evalAsString(ctx.evalContext())));
			else player.sendSystemMessage(Component.literal(message.evalAsString(ctx.evalContext())));
		}
	}
}