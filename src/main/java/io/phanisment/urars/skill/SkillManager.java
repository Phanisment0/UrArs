package io.phanisment.urars.skill;

import net.minecraft.util.Identifier;

import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.AnnotationScanner;

import static io.phanisment.urars.UrArs.ID;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Optional;

/**
 * Class for managing skill registration, class registration and get the registered.
 */
public final class SkillManager {
	private static final Map<String, Class<? extends SkillCondition>> conditions = new HashMap<>();
	private static final Map<String, Class<? extends SkillMechanic>> mechanics = new HashMap<>();
	private static final Map<String, Class<? extends SkillTargeter>> targeters = new HashMap<>();
	
	private static final Map<Identifier, Skill> skills = new HashMap<>();
	
	private SkillManager() {
	}
	
	@SuppressWarnings("unchecked")
	public static void load() {
		registerClasses("io.phanisment.urars.skill.conditions", conditions, SkillCondition.class);
		registerClasses("io.phanisment.urars.skill.mechanics", mechanics, SkillMechanic.class);
		registerClasses("io.phanisment.urars.skill.targeters", targeters, SkillTargeter.class);
		registerClasses("io.phanisment.urars.skill.trigger", targeters, SkillTargeter.class);
	}
	
	/**
	 * Clear all registered data in skill manager.
	 */
	public static void unload() {
		skills.clear();
	}
	
	/**
	 * Get registered skill.
	 * 
	 * @param id Id of the skill
	 * @return   Registered skill
	 */
	public static Optional<Skill> getSkill(Identifier id) {
		return Optional.ofNullable(skills.get(id));
	}
	
	/**
	 * Get id of all registered skills.
	 */
	public static Set<Identifier> getSkills() {
		return skills.keySet();
	}
	
	/**
	 * Get registered mechanic class.
	 * 
	 * @param key Name of mechanic
	 * @return    The mechanic class
	 */
	public static Class<? extends SkillMechanic> getMechanic(String key) {
		return mechanics.get(key.toLowerCase());
	}
	
	/**
	 * Get new instance of mechanic class.
	 * 
	 * @param config Required parameters to create new instance 
	 * @return       Mechanic instance
	 */
	public static Optional<SkillMechanic> getMechanic(SkillLineConfig config) throws Exception {
		return newInstance(getMechanic(config.getKey()), config);
	}
	
	/**
	 * Get registered targeter class.
	 * 
	 * @param key Name of targeter
	 * @return    The targeter class
	 */
	public static Class<? extends SkillTargeter> getTargeter(String key) {
		return targeters.get(key.toLowerCase());
	}
	
	/**
	 * Get new instance of targeter class.
	 * 
	 * @param config Required parameters to create new instance 
	 * @return       Targeter instance
	 */
	public static Optional<SkillTargeter> getTargeter(SkillLineConfig config) throws Exception {
		return newInstance(getTargeter(config.getKey()), config);
	}
	
	/**
	 * Get registered condition class.
	 * 
	 * @param key Name of condition
	 * @return    The condition class
	 */
	public static Class<? extends SkillCondition> getCondition(String key) {
		return conditions.get(key.toLowerCase());
	}
	
	/**
	 * Get new instance of condition class.
	 * 
	 * @param config Required parameters to create new instance 
	 * @return       Condition instance
	 */
	public static Optional<SkillCondition> getCondition(SkillLineConfig config) throws Exception {
	return newInstance(getCondition(config.getKey()), config);
	}
	
	/**
	 * Register skill.
	 * 
	 * @param id    Id skill
	 * @param skill Skill data
	 * @return      True if skill id is absent and false if skill id is registered
	 */
	public static boolean registerSkill(Identifier id, Skill skill) {
		return skills.putIfAbsent(id, skill) == null;
	}
	
	/**
	 * Register mechanic class.
	 * 
	 * @param key      Name mechanic 
	 * @param mechanic Mechanic class
	 * @return         True if mechanic key is absent and false if mechanic key is registered
	 */
	public static boolean registerMechanic(String key, Class<? extends SkillMechanic> mechanic) {
		return mechanics.putIfAbsent(key.toLowerCase(), mechanic) == null;
	}
	
	/**
	 * Register targeter class.
	 * 
	 * @param key      Name targeter 
	 * @param targeter Targeter class
	 * @return         True if mechanic key is absent and false if mechanic key is registered
	 */
	public static boolean registerTargeter(String key, Class<? extends SkillTargeter> targeter) {
		return targeters.putIfAbsent(key.toLowerCase(), targeter) == null;
	}
	
	/**
	 * Register mechanic class.
	 * 
	 * @param key       Name mechanic 
	 * @param condition Mechanic class
	 * @return          True if mechanic key is absent and false if mechanic key is registered
	 */
	public static boolean registerCondition(String key, Class<? extends SkillCondition> condition) {
		return conditions.putIfAbsent(key.toLowerCase(), condition) == null;
	}
	
	/**
	 * Create new instance form the class.
	 * 
	 * @param clazz  Target class to create the instance 
	 * @param config Required parameter for the class
	 * @param <T> Class type
	 * @return New instance of class
	 */
	private static <T> Optional<T> newInstance(Class<? extends T> clazz, SkillLineConfig config) throws Exception {
		return Optional.ofNullable(clazz.getDeclaredConstructor(SkillLineConfig.class).newInstance(config));
	}
	
	/**
	 * Register all class with annotation {@link RegistryInfo} in target package.
	 * 
	 * @param path       Target package
	 * @param registry   Target Registry data
	 * @parsm base_class Extended class
	 */
	@SuppressWarnings("unchecked")
	private static <T> void registerClasses(String path, Map<String, Class<? extends T>> registry, Class<T> base_class) {
		for (Class<?> clazz : AnnotationScanner.find(ID, path, RegistryInfo.class)) {
			if (!base_class.isAssignableFrom(clazz)) return;
			RegistryInfo info = clazz.getAnnotation(RegistryInfo.class);
			if (info == null) return;
			
			registry.put(info.key().toLowerCase(), (Class<? extends T>)clazz);
			for (String alias : info.aliases()) {
				registry.put(alias.toLowerCase(), (Class<? extends T>)clazz);
			}
		}
	} 
}