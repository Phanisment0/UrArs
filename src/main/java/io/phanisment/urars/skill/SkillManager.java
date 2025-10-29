package io.phanisment.urars.skill;

import net.minecraft.util.Identifier;

import io.phanisment.urars.skill.annotation.ConditionInfo;
import io.phanisment.urars.skill.annotation.MechanicInfo;
import io.phanisment.urars.skill.annotation.TargeterInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.AnnotationScanner;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Optional;

public final class SkillManager {
	private static final Map<String, Class<? extends SkillMechanic>> mechanics = new HashMap<>();
	private static final Map<String, Class<? extends SkillTargeter>> targeters = new HashMap<>();
	private static final Map<String, Class<? extends SkillCondition>> conditions = new HashMap<>();
	
	private static final Map<Identifier, Skill> skills = new HashMap<>();
	
	public static void unload() {
		skills.clear();
	}
	
	@SuppressWarnings("unchecked")
	public static void loadClasses() {
		AnnotationScanner.find("urars", "io.phanisment.urars.skill.mechanics", MechanicInfo.class).forEach(clazz -> {
			var info = clazz.getAnnotation(MechanicInfo.class);
			mechanics.put(info.key().toLowerCase(), (Class<? extends SkillMechanic>)clazz);
			for (String alias : info.aliases()) {
				mechanics.put(alias.toLowerCase(), (Class<? extends SkillMechanic>)clazz);
			}
		});
		
		AnnotationScanner.find("urars", "io.phanisment.urars.skill.targeters", TargeterInfo.class).forEach(clazz -> {
			var info = clazz.getAnnotation(TargeterInfo.class);
			targeters.put(info.key().toLowerCase(), (Class<? extends SkillTargeter>)clazz);
			for (String alias : info.aliases()) {
				targeters.put(alias.toLowerCase(), (Class<? extends SkillTargeter>)clazz);
			}
		});
		
		AnnotationScanner.find("urars", "io.phanisment.urars.skill.conditions", ConditionInfo.class).forEach(clazz -> {
			var info = clazz.getAnnotation(ConditionInfo.class);
			conditions.put(info.key().toLowerCase(), (Class<? extends SkillCondition>)clazz);
			for (String alias : info.aliases()) {
				conditions.put(alias.toLowerCase(), (Class<? extends SkillCondition>)clazz);
			}
		});
	}
	
	public static Optional<Skill> getSkill(Identifier id) {
		return Optional.ofNullable(skills.get(id));
	}
	
	public static Set<Identifier> getSkills() {
		return skills.keySet();
	}
	
	public static Class<? extends SkillMechanic> getMechanic(String key) {
		return mechanics.get(key.toLowerCase());
	}
	
	public static Optional<SkillMechanic> getMechanic(SkillLineConfig config) throws Exception {
		Class<? extends SkillMechanic> clazz = getMechanic(config.getKey());
		if (clazz == null) return Optional.empty();
		var mechanic = (SkillMechanic)clazz.getDeclaredConstructor(SkillLineConfig.class).newInstance(config);
		return Optional.of(mechanic);
	}
	
	public static Class<? extends SkillTargeter> getTargeter(String key) {
		return targeters.get(key.toLowerCase());
	}
	
	public static Optional<SkillTargeter> getTargeter(SkillLineConfig config) throws Exception {
		Class<? extends SkillTargeter> clazz = getTargeter(config.getKey());
		if (clazz == null) return Optional.empty();
		var targeter = (SkillTargeter)clazz.getDeclaredConstructor(SkillLineConfig.class).newInstance(config);
		return Optional.of(targeter);
	}
	
	public static Class<? extends SkillCondition> getCondition(String key) {
		return conditions.get(key.toLowerCase());
	}
	
	public static Optional<SkillCondition> getCondition(SkillLineConfig config) throws Exception {
		Class<? extends SkillCondition> clazz = getCondition(config.getKey());
		if (clazz == null) return Optional.empty();
		var condition = (SkillCondition)clazz.getDeclaredConstructor(SkillLineConfig.class).newInstance(config);
		return Optional.of(condition);
	}
	
	public static boolean registerSkill(Identifier id, Skill skill) {
		if (skills.containsKey(id)) return false;
		skills.put(id, skill);
		return true;
	}
	
	public static boolean registerMechanic(String key, Class<? extends SkillMechanic> mechanic) {
		if (mechanics.containsKey(key)) return false;
		mechanics.put(key, mechanic);
		return true;
	}
	
	public static boolean registerTargeter(String key, Class<? extends SkillTargeter> targeter) {
		if (targeters.containsKey(key)) return false;
		targeters.put(key, targeter);
		return true;
	}
	
	public static boolean registerCondition(String key, Class<? extends SkillCondition> condition) {
		if (conditions.containsKey(key)) return false;
		conditions.put(key, condition);
		return true;
	}
}