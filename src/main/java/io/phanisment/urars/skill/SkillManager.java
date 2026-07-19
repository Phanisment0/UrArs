package io.phanisment.urars.skill;

import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.AnnotationScanner;
import net.minecraft.resources.Identifier;

import static io.phanisment.urars.UrArs.MOD_ID;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Class for managing skill registration, class registration and get the registered.
 */
@SuppressWarnings("null")
public final class SkillManager {
	private static final Map<Identifier, SkillActionFactory<SkillCondition>> CONDITIONS;
	private static final Map<String, Identifier> CONDITIONS_ALIASES;

	private static final Map<Identifier, SkillActionFactory<SkillMechanic>> MECHANICS;
	private static final Map<String, Identifier> MECHANICS_ALIASES;
	
	private static final Map<Identifier, SkillActionFactory<SkillTargeter>> TARGETERS;
	private static final Map<String, Identifier> TARGETERS_ALIASES;
	
	private static final Map<Identifier, Skill> SKILLS = new ConcurrentHashMap<>();
	
	private SkillManager() {
	}
	
	public static void load() {
	}
	
	/**
	 * Clear all registered data in skill manager.
	 */
	public static void unload() {
		SKILLS.clear();
	}
	
	/**
	 * Get registered skill.
	 * 
	 * @param id Id of the skill
	 * @return   Registered skill
	 */
	@Nullable
	public static Skill getSkill(final Identifier id) {
		return SKILLS.get(id);
	}

	public static Optional<Skill> getSkillOptional(final Identifier id) {
		return Optional.ofNullable(getSkill(id));
	}
	
	/**
	 * Get id of all registered skills.
	 */
	public static Set<Identifier> getSkills() {
		return SKILLS.keySet();
	}

	@Nullable
	public static SkillMechanic getMechanic(final SkillLineConfig config) {
		return getMechanic(config.getKey()).create(config);
	}

	@Nullable
	public static SkillActionFactory<SkillMechanic> getMechanic(final String alias) {
		return getMechanicId(MECHANICS_ALIASES.get(alias));
	}
	
	/**
	 * Get registered mechanic class.
	 * 
	 * @param key Name of mechanic
	 * @return    The mechanic class
	 */
	@Nullable
	public static SkillActionFactory<SkillMechanic> getMechanicId(final Identifier id) {
		return MECHANICS.get(id);
	}

	@Nullable
	public static SkillCondition getCondition(final SkillLineConfig config) {
		return getCondition(config.getKey()).create(config);
	}

	@Nullable
	public static SkillActionFactory<SkillCondition> getCondition(final String alias) {
		return getConditionId(CONDITIONS_ALIASES.get(alias));
	}

	/**
	 * Get registered condition class.
	 * 
	 * @param key Name of condition
	 * @return    The condition class
	 */
	@Nullable
	public static SkillActionFactory<SkillCondition> getConditionId(final Identifier id) {
		return CONDITIONS.get(id);
	}

	@Nullable
	public static SkillTargeter getTargeter(final SkillLineConfig config) {
		return getTargeter(config.getKey()).create(config);
	}

	@Nullable
	public static SkillActionFactory<SkillTargeter> getTargeter(String alias) {
		return getTargeterId(TARGETERS_ALIASES.get(alias));
	}

	/**
	 * Get registered targeter class.
	 * 
	 * @param key Name of targeter
	 * @return    The targeter class
	 */
	@Nullable
	public static SkillActionFactory<SkillTargeter> getTargeterId(Identifier id) {
		return TARGETERS.get(id);
	}
	
	public static boolean registerSkill(Identifier id, Skill skill) {
		return SKILLS.putIfAbsent(id, skill) == null;
	}

	public static <T extends ISkillAction> ActionDataRegistery<T> scanClasses(String path, Class<T> base_class) {
		var data = new ActionDataRegistery<T>();
		var lookup = MethodHandles.lookup();

		for (Class<?> clazz : AnnotationScanner.find(MOD_ID, path, RegistryInfo.class)) {
			if (!base_class.isAssignableFrom(clazz)) continue;

			@NonNull
			RegistryInfo info = clazz.getAnnotation(RegistryInfo.class);

			String raw_id = info.key().toLowerCase();
			Identifier id = Identifier.tryParse(raw_id);

			try {
				MethodHandle constructor = lookup.findConstructor(clazz, MethodType.methodType(void.class, SkillLineConfig.class));
				CallSite site = LambdaMetafactory.metafactory(
					lookup,
					"create", // Method name
					MethodType.methodType(SkillActionFactory.class), // return type the factory
					MethodType.methodType(ISkillAction.class, SkillLineConfig.class), // Signature method
					constructor,
					MethodType.methodType(clazz, SkillLineConfig.class) // Real signature class
				);
				SkillActionFactory<T> factory = (SkillActionFactory<T>)site.getTarget().invokeExact();

				data.registry.put(id, factory);
				data.aliases.put(raw_id, id);
				for (String alias : info.aliases()) data.aliases.put(alias.toLowerCase(), id);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	public static class ActionDataRegistery<T extends ISkillAction> {
		final Map<Identifier, SkillActionFactory<T>> registry = new HashMap<>();
		final Map<String, Identifier> aliases = new HashMap<>();
	}

	static {
		ActionDataRegistery<@NonNull SkillCondition> condition = scanClasses("io.phanisment.urars.skill.conditions", SkillCondition.class);
		CONDITIONS = Collections.unmodifiableMap(condition.registry);
		CONDITIONS_ALIASES = Collections.unmodifiableMap(condition.aliases);

		ActionDataRegistery<@NonNull SkillMechanic> mechanics = scanClasses("io.phanisment.urars.skill.mechanics", SkillMechanic.class);
		MECHANICS = Collections.unmodifiableMap(mechanics.registry);
		MECHANICS_ALIASES = Collections.unmodifiableMap(mechanics.aliases);

		ActionDataRegistery<@NonNull SkillTargeter> targeters = scanClasses("io.phanisment.urars.skill.targeters", SkillTargeter.class);
		TARGETERS = Collections.unmodifiableMap(targeters.registry);
		TARGETERS_ALIASES = Collections.unmodifiableMap(targeters.aliases);
	}
}