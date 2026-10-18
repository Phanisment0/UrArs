package io.phanisment.urars.mob;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.phanisment.urars.mob.ai.AiGoalFactory;
import io.phanisment.urars.reload.ReloadResult;
import io.phanisment.urars.reload.Reloadable;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.util.AnnotationScanner;
import io.phanisment.urars.util.UrArsDebugger;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.jspecify.annotations.NonNull;

import static io.phanisment.urars.UrArs.MOD_ID;

public final class MobManager implements Reloadable {
	private static final GoalDataRegistry<@NonNull Goal> GOALS = scanClasses("io.phanisment.urars.mob.goals", Goal.class);
	private static final GoalDataRegistry<@NonNull TargetGoal> TARGETS = scanClasses("io.phanisment.urars.mob.targets", TargetGoal.class);

	private static final Map<Identifier, MobMeta> MOBS = new HashMap<>();

	@Override
	public ReloadResult load() {
		return ReloadResult.success("Loaded " + MOBS.size() + " mob(s)");
	}

	@Override
	public ReloadResult unload() {
		var msg = ReloadResult.success("Unloading " + MOBS.size() + " mob(s)");
		MOBS.clear();
		return msg;
	}



	public static AiGoalFactory<Goal> getGoal(Identifier id) {
		return GOALS.get(id);
	}

	public static AiGoalFactory<Goal> getGoal(String alias) {
		return GOALS.get(alias);
	}

	public static GoalDataRegistry<Goal> getGoals() {
		return GOALS;
	}



	public static AiGoalFactory<TargetGoal> getTargetGoal(Identifier id) {
		return TARGETS.get(id);
	}

	public static AiGoalFactory<TargetGoal> getTargetGoal(String alias) {
		return TARGETS.get(alias);
	}

	public static GoalDataRegistry<TargetGoal> getTargetGoals() {
		return TARGETS;
	}



	public static void registerMob(Identifier id, MobMeta mob) {
		MobMeta previous = MOBS.putIfAbsent(id, mob);
		if (previous != null) {
			// Always shown: a duplicate id is almost certainly a config mistake
			UrArsDebugger.warn("Mob '{}' is already registered, ignoring duplicate", id);
			return;
		}
		UrArsDebugger.debug("Registered mob '{}' (total: {})", id, MOBS.size());
	}

	public static MobMeta getMob(Identifier id) {
		return MOBS.get(id);
	}

	public static Optional<MobMeta> getMobOptional(Identifier id) {
		return Optional.ofNullable(MOBS.get(id));
	}

	public static Set<Identifier> getMobs() {
		return MOBS.keySet();
	}

	public static <T extends Goal> GoalDataRegistry<T> scanClasses(final String path, final Class<T> base_class) {
		Map<Identifier, AiGoalFactory<T>> registry = new HashMap<>();
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
				MethodHandle constructor = lookup.findConstructor(clazz, MethodType.methodType(void.class, Mob.class, SkillLineConfig.class));
				CallSite site = LambdaMetafactory.metafactory(
						lookup,
						"create", // Method name
						MethodType.methodType(AiGoalFactory.class), // return type the factory
						MethodType.methodType(Goal.class, Mob.class, SkillLineConfig.class), // Signature method
						constructor,
						MethodType.methodType(clazz, Mob.class, SkillLineConfig.class) // Real signature class
				);

				@SuppressWarnings("unchecked")
				AiGoalFactory<T> factory = (AiGoalFactory<T>)site.getTarget().invokeExact();

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

		return new GoalDataRegistry<>(registry, aliases);
	}
}