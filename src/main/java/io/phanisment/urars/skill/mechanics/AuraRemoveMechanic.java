package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.template.aura.AuraManager;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import io.phanisment.urars.skill.target.INoTarget;
import net.minecraft.world.entity.Entity;

import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		key = "removeAura",
		aliases = {"rmvAura", "deleteAura", "delAura"}
)
public class AuraRemoveMechanic extends SkillMechanic implements INoTarget, IEntityTarget {
	protected final Node name;

	public AuraRemoveMechanic(SkillLineConfig config) {
		super(config);
		name = config.getMathString(aliases("name", "n", "aura", "a"), "");
	}

	@Override
	public void cast(SkillContext ctx) {
		castAtEntity(ctx, ctx.entity());
	}

	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		String name_str = name.evalAsString(ctx.evalContext());
		if (name_str.isBlank()) return;

		AuraManager.remove(target, name_str);
	}
}