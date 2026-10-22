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
import io.phanisment.urars.registry.GoalDataRegistry;
import io.phanisment.urars.registry.RegistryScanner;
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
import org.jspecify.annotations.Nullable;

import static io.phanisment.urars.UrArs.MOD_ID;

public final class MobManager implements Reloadable {
	private static final GoalDataRegistry<@NonNull Goal> GOALS = scan("goals", Goal.class);
	private static final GoalDataRegistry<@NonNull TargetGoal> TARGETS = scan("targets", TargetGoal.class);

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



	public static @Nullable AiGoalFactory<Goal> getGoal(Identifier id) {
		return GOALS.get(id);
	}
	public static @Nullable AiGoalFactory<Goal> getGoal(String alias) {
		return GOALS.get(alias);
	}
	public static GoalDataRegistry<Goal> getGoals() {
		return GOALS;
	}



	public static @Nullable AiGoalFactory<TargetGoal> getTargetGoal(Identifier id) {
		return TARGETS.get(id);
	}
	public static @Nullable AiGoalFactory<TargetGoal> getTargetGoal(String alias) {
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
	private static <T extends Goal> GoalDataRegistry<T> scan(String path, Class<T> base) {
		return RegistryScanner.scan(new GoalDataRegistry<>(), "io.phanisment.urars.mob." + path, base, AiGoalFactory.class);
	}
}