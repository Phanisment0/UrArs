package io.phanisment.urars.skill.mechanics;

import net.minecraft.text.Text;
import net.minecraft.entity.player.PlayerEntity;

import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.INoTarget;

@RegistryInfo(author="Phanisment", key="message", aliases={"msg", "send"})
public class MessageMechanic extends SkillMechanic implements INoTarget {
	private final String message;
	private final boolean overlay;
	
	public MessageMechanic(SkillLineConfig config) {
		super(config);
		this.message = config.getString(new String[]{"text", "txt", "t", "message", "msg", "m"}, "Hi!");
		this.overlay = config.getBoolean(new String[]{"overlay", "o"});
	}
	
	@Override
	public void cast(SkillContext ctx) {
		if (ctx.getCaster() instanceof PlayerEntity player) {
			player.sendMessage(Text.of(message), overlay);
		}
	}
}