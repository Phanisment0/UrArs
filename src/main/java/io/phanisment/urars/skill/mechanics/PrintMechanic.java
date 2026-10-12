package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.INoTarget;

@RegistryInfo(
		author = "Phanisment",
		key = "print"
)
public class PrintMechanic extends SkillMechanic implements INoTarget {
	private final String message;
	
	public PrintMechanic(SkillLineConfig config) {
		super(config);
		this.message = config.getString(new String[]{"message", "msg", "m"});
	}
	
	@Override
	public void cast(SkillContext ctx) {
	System.out.println(message);
	}
}
