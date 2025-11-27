package io.phanisment.urars.skill.mechanics;

import net.minecraft.text.Text;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.INoTarget;
import io.phanisment.urars.skill.target.IEntityTarget;

@RegistryInfo(author="Phanisment", key="message")
public class MessageMechanic extends SkillMechanic implements INoTarget, IEntityTarget {
	private final String message;
	private final boolean overlay;
	
	public MessageMechanic(SkillLineConfig config) {
		super(config);
		this.message = config.getString(new String[]{"text", "txt", "t", "message", "msg", "m"}, "Hi!");
		this.overlay = config.getBoolean(new String[]{"overlay", "o"});
	}
	
	@Override
	public void cast(SkillContext ctx) {
		this.castAtEntity(ctx, ctx.getCaster());
	}
	
	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		if (target instanceof ServerPlayerEntity player) player.sendMessage(Text.of(message), overlay);
	}
}