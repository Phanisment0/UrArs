package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.exception.Unchecked;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.parser.math.parser.MathParser;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.INoTarget;

import java.util.LinkedHashMap;
import java.util.Map;

@RegistryInfo(
		author = "Phanisment",
		key = "skill",
		aliases = { "call" }
)
public class CallMechanic extends SkillMechanic implements INoTarget {
	protected final MechanicsExecutor mechanics;
	protected final Map<String, Node> params = new LinkedHashMap<>();
	public CallMechanic(SkillLineConfig config) {
		super(config);
		mechanics = config.getMechanics("s");
		for (var entry : config.map().entrySet()) {
			String key = entry.getKey();
			if (key == null) continue;
			if (except(key)) continue;
			params.put(key, Unchecked.parse(() -> new MathParser(entry.getValue()).parse()));
		}
	}

	private static boolean except(String key) {
		return key.equalsIgnoreCase("s") || key.equalsIgnoreCase("delay");
	}

	@Override
	public void cast(SkillContext ctx) {
		if (mechanics == null) return;
		for (var entry : params.entrySet()) ctx.setVar(entry.getKey(), entry.getValue().eval(ctx.evalContext()));
		mechanics.execute(ctx);
	}
}