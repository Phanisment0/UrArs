package io.phanisment.urars.skill;

import io.phanisment.urars.registry.ActionDataRegistry;
import io.phanisment.urars.registry.RegistryScanner;
import io.phanisment.urars.reload.ReloadResult;
import io.phanisment.urars.reload.Reloadable;
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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Class for managing skill registration, class registration and get the registered.
 */
public final class SkillManager implements Reloadable {
	private static final ActionDataRegistry<@NonNull SkillCondition> CONDITIONS = scan("conditions", SkillCondition.class);
	private static final ActionDataRegistry<@NonNull SkillMechanic> MECHANICS = scan("mechanics", SkillMechanic.class);
	private static final ActionDataRegistry<@NonNull SkillTargeter> TARGETERS = scan("targeters", SkillTargeter.class);

	private static final Map<Identifier, Skill> SKILLS = new HashMap<>();

	@Override
	public ReloadResult load() {
		return ReloadResult.success("Loaded " + SKILLS.size() + " skill(s)");
	}

	@Override
	public ReloadResult unload() {
		var msg = ReloadResult.success("Unloading " + SKILLS.size() + " skill(s)");
		SKILLS.clear();
		return msg;
	}



	public static @Nullable Skill getSkill(final Identifier id) {
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


	// Mechanic //
	public static @Nullable SkillActionFactory<SkillMechanic> getMechanic(Identifier id) {
		return MECHANICS.get(id);
	}
	public static @Nullable SkillActionFactory<SkillMechanic> getMechanic(String alias) {
		return MECHANICS.get(alias);
	}
	public static ActionDataRegistry<SkillMechanic> getMechanics() {
		return MECHANICS;
	}


	// Conditions //
	public static @Nullable SkillActionFactory<SkillCondition> getCondition(Identifier id) {
		return CONDITIONS.get(id);
	}
	public static @Nullable SkillActionFactory<SkillCondition> getCondition(String alias) {
		return CONDITIONS.get(alias);
	}
	public static ActionDataRegistry<SkillCondition> getConditions() {
		return CONDITIONS;
	}


	// Targeter //
	public static @Nullable SkillActionFactory<SkillTargeter> getTargeter(Identifier id) {
		return TARGETERS.get(id);
	}
	public static @Nullable SkillActionFactory<SkillTargeter> getTargeter(String alias) {
		return  TARGETERS.get(alias);
	}
	public static ActionDataRegistry<SkillTargeter> getTargeters() {
		return TARGETERS;
	}

	private static <T extends ISkillAction> ActionDataRegistry<T> scan(String path, Class<T> base) {
		return RegistryScanner.scan(new ActionDataRegistry<>(), "io.phanisment.urars.skill." + path, base, SkillActionFactory.class);
	}
}