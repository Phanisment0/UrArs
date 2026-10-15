package io.phanisment.urars.skill;

import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.AnnotationScanner;
import io.phanisment.urars.util.UrArsDebugger;
import net.minecraft.resources.Identifier;

import static io.phanisment.urars.UrArs.MOD_ID;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
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
public final class SkillManager {
	private static final ActionDataRegistery<@NonNull SkillCondition> CONDITIONS = scanClasses("io.phanisment.urars.skill.conditions", SkillCondition.class);
	private static final ActionDataRegistery<@NonNull SkillMechanic>  MECHANICS = scanClasses("io.phanisment.urars.skill.mechanics", SkillMechanic.class);
	private static final ActionDataRegistery<@NonNull SkillTargeter>  TARGETERS = scanClasses("io.phanisment.urars.skill.targeters", SkillTargeter.class);

	private static final Map<Identifier, Skill> SKILLS = new ConcurrentHashMap<>();

	private SkillManager() {
	}

	public static void unload() {
		UrArsDebugger.debug("Unloading {} skill(s)", SKILLS.size());
		SKILLS.clear();
	}



	@Nullable
	public static Skill getSkill(final Identifier id) {
		Skill skill = SKILLS.get(id);
		if (skill == null) UrArsDebugger.trace("Skill '{}' not found", id);
		return skill;
	}

	public static Optional<Skill> getSkillOptional(final Identifier id) {
		return Optional.ofNullable(getSkill(id));
	}

	public static Set<Identifier> getSkills() {
		return SKILLS.keySet();
	}

	public static void registerSkill(Identifier id, Skill skill) {
		Skill previous = SKILLS.putIfAbsent(id, skill);
		if (previous != null) {
			// Always shown: a duplicate id is almost certainly a config mistake
			UrArsDebugger.warn("Skill '{}' is already registered, ignoring duplicate", id);
			return;
		}
		UrArsDebugger.debug("Registered skill '{}' (total: {})", id, SKILLS.size());
	}



	public static SkillActionFactory<SkillMechanic> getMechanic(Identifier id) {
		return MECHANICS.get(id);
	}

	public static SkillActionFactory<SkillMechanic> getMechanic(String alias) {
		return MECHANICS.get(alias);
	}

	public static ActionDataRegistery<SkillMechanic> getMechanics() {
		return MECHANICS;
	}



	public static SkillActionFactory<SkillCondition> getCondition(Identifier id) {
		return CONDITIONS.get(id);
	}

	public static SkillActionFactory<SkillCondition> getCondition(String alias) {
		return CONDITIONS.get(alias);
	}

	public static ActionDataRegistery<SkillCondition> getConditions() {
		return CONDITIONS;
	}



	public static SkillActionFactory<SkillTargeter> getTargeter(Identifier id) {
		return TARGETERS.get(id);
	}

	public static SkillActionFactory<SkillTargeter> getTargeter(String alias) {
		return  TARGETERS.get(alias);
	}

	public static ActionDataRegistery<SkillTargeter> getTargeters() {
		return TARGETERS;
	}

	public static <T extends ISkillAction> ActionDataRegistery<T> scanClasses(final String path, final Class<T> base_class) {
		Map<Identifier, SkillActionFactory<T>> registry = new HashMap<>();
		Map<String, Identifier> aliases = new HashMap<>();
		var lookup = MethodHandles.lookup();

		long start = System.nanoTime();
		int failed = 0;
		UrArsDebugger.debug("Scanning package '{}' for {}", path, base_class.getSimpleName());

		for (Class<?> clazz : AnnotationScanner.find(MOD_ID, path, RegistryInfo.class)) {
			if (!base_class.isAssignableFrom(clazz)) {
				UrArsDebugger.trace("Skipped '{}': not a {}", clazz.getName(), base_class.getSimpleName());
				continue;
			}

			RegistryInfo info = clazz.getAnnotation(RegistryInfo.class);

			String author = info.author().toLowerCase();
			String raw_id = info.key().toLowerCase();
			Identifier id = Identifier.fromNamespaceAndPath(author, raw_id);
			/*if (id == null) {
				failed++;
				UrArsDebugger.warn("Invalid registry key '{}' on class '{}', skipping", raw_id, clazz.getSimpleName());
				continue;
			}*/
			if (registry.containsKey(id)) {
				failed++;
				UrArsDebugger.warn("Duplicate registry id '{}' on class '{}', skipping", id, clazz.getSimpleName());
				continue;
			}

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

				@SuppressWarnings("unchecked")
				SkillActionFactory<T> factory = (SkillActionFactory<T>)site.getTarget().invokeExact();

				registry.put(id, factory);
				aliases.put(raw_id, id);
				aliases.put(id.toString(), id);
				for (String alias : info.aliases()) {
					UrArsDebugger.trace("Alias '{}' -> '{}'", alias.toLowerCase(), id);
					aliases.put(alias.toLowerCase(), id);
				}

				UrArsDebugger.debug("Registered id '{}' with class '{}'", id, clazz.getSimpleName());
			} catch (Throwable e) {
				failed++;
				UrArsDebugger.error("Failed to register '" + clazz.getName() + "' (id: " + id + ")", e);
			}
		}

		long ms = (System.nanoTime() - start) / 1_000_000;
		UrArsDebugger.info("Scanned '{}': {} registered, {} failed, {} aliases ({} ms)", path, registry.size(), failed, aliases.size(), ms);

		return new ActionDataRegistery<T>(registry, aliases);
	}
}