package io.phanisment.urars.skill.targeters;

import io.phanisment.urars.exception.MathParserException;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.parser.math.ast.NumberNode;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillTargeter;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.TargeterUtils;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RegistryInfo(
		author = "Phanisment",
		key = "target"
)
public class TargetTargeter extends SkillTargeter {
	private final Node range;

	public TargetTargeter(SkillLineConfig config) {
		super(config);
		try {
			this.range = config.getNumberMath(new String[]{"range", "r"}, 16);
		} catch (MathParserException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Collection<Entity> getTarget(SkillContext ctx) {
		List<Entity> targets = new ArrayList<>();
		Entity target = TargeterUtils.getTargetedEntity(ctx.entity(), Double.valueOf(range.evalAsDouble(ctx.evalContext())).intValue());
		targets.add(target);
		return targets;
	}
}
