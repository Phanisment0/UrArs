package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.exception.MathParserException;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.INoTarget;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import io.phanisment.urars.skill.target.IEntityTarget;

@RegistryInfo(
	author = "Phanisment",
	key = "message"
)
public class MessageMechanic extends SkillMechanic implements INoTarget, IEntityTarget {
	private final Node message;
	private final boolean overlay;
	
	public MessageMechanic(SkillLineConfig config) {
		super(config);
		try {
			if (config.singleArgument()) this.message = config.getMathString(new String[]{null}, "Hi!");
			else this.message = config.getMathString(new String[]{"text", "txt", "t", "message", "msg", "m"}, "Hi!");
		} catch (MathParserException e) {
			throw new RuntimeException(e);
		}
		this.overlay = config.getBoolean(new String[]{"overlay", "o"}, false);
	}
	
	@Override
	public void cast(SkillContext ctx) {
		this.castAtEntity(ctx, ctx.entity());
	}
	
	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		if (target instanceof ServerPlayer player) {
			if (overlay) player.sendOverlayMessage(Component.literal(message.evalAsString(ctx.evalContext())));
			else player.sendSystemMessage(Component.literal(message.evalAsString(ctx.evalContext())));
		}
	}
}