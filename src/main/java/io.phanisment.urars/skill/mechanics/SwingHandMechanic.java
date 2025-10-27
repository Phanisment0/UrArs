package io.phanisment.urars.skill.mechanics;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;

import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.MechanicInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.INoTarget;

import java.util.Map;
import java.util.HashMap;

@MechanicInfo(author="Phanisment", key="swinghand")
public class SwingHandMechanic extends SkillMechanic implements INoTarget {
	private Hand hand = Hand.MAIN_HAND;
	
	public SwingHandMechanic(SkillLineConfig config) {
		super(config);
		try {
			this.hand = Hand.valueOf(config.getString(new String[]{"hand", "h"}, "MAIN_HAND").toUpperCase());
		} catch (IllegalArgumentException e) {
		}
	}
	
	@Override
	public void cast(SkillContext ctx) {
		if (ctx.getCaster() instanceof LivingEntity entity) entity.swingHand(hand);
	}
}