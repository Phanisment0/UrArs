package io.phanisment.urars.util;

import io.phanisment.urars.UrArsComponents;
import io.phanisment.urars.component.MobComponent;
import io.phanisment.urars.mob.MobManager;
import io.phanisment.urars.mob.MobMeta;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.TriggerType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public final class TriggerUtils {
	public static void castEntity(Entity entity, TriggerType event) {
		MobComponent comp = UrArsComponents.MOB.get(entity);
		String id = comp.id();
		if (id == null || id.isEmpty()) return;

		MobMeta mob = MobManager.getMob(Identifier.tryParse(id));
		if (mob == null) return;
		var context = new SkillContext(entity);
		mob.apply(context, entity);
		mob.mechanics().execute(event, context);
	}
}