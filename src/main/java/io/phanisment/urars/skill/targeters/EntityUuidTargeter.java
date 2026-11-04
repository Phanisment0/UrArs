package io.phanisment.urars.skill.targeters;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.EntityTargeter;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static io.phanisment.urars.util.ActionUtils.aliasNull;
import static io.phanisment.urars.util.ActionUtils.aliases;

@RegistryInfo(
		author = "Phanisment",
		key = "entity",
		aliases = { "e" }
)
public class EntityUuidTargeter extends EntityTargeter {
	protected final Node raw_uuid;
	protected final Node any_dimension;

	public EntityUuidTargeter(SkillLineConfig config) {
		super(config);
		if (config.singleArgument()) raw_uuid = config.getMathString(aliasNull(), "");
		else raw_uuid = config.getMathString(aliases("uuid", "u"), "");
		any_dimension = config.getMathBoolean(aliases("anyDimension", "ad"), false);
	}

	@Override
	public Collection<Entity> getEntities(SkillContext ctx) {
		UUID uuid = UUID.fromString(raw_uuid.evalAsString(ctx.evalContext()));
		Entity entity = ctx.level().getEntityInAnyDimension(uuid);
		if (entity != null) return List.of(entity);
		return new ArrayList<>();
	}
}