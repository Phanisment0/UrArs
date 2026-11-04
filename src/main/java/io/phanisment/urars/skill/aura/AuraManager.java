package io.phanisment.urars.skill.aura;

import io.phanisment.urars.parser.math.value.NumberValue;
import io.phanisment.urars.parser.math.value.StringValue;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.util.UrArsScheduler;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public final class AuraManager {
	private static final Map<UUID, Map<String, ActiveAura>> ACTIVE = new HashMap<>();

	private AuraManager() {
	}

	public static ActiveAura start(Aura owner, SkillContext ctx, Entity target, String id, long duration, long interval) {
		Map<String, ActiveAura> map = ACTIVE.computeIfAbsent(target.getUUID(), k -> new HashMap<>());

		ActiveAura old = map.get(id);
		if (old != null) finish(old);

		map = ACTIVE.computeIfAbsent(target.getUUID(), k -> new HashMap<>());

		ActiveAura a = new ActiveAura(id, owner, ctx, target, duration);
		map.put(id, a);
		owner.start(a);
		schedule(a, Math.max(1, interval));
		return a;
	}

	private static void schedule(ActiveAura a, long interval) {
		UrArsScheduler.wait(interval, () -> {
			if (!a.alive) return;

			if (!a.target().isAlive() || a.target().isRemoved()) {
				finish(a);
				return;
			}

			a.owner().tick(a);

			if (a.remaining >= 0) {
				a.remaining -= interval;
				if (a.remaining <= 0) {
					finish(a);
					return;
				}
			}
			schedule(a, interval);
		});
	}

	private static void finish(ActiveAura a) {
		if (!a.alive) return;
		a.alive = false;

		UUID uuid = a.target().getUUID();
		Map<String, ActiveAura> map = ACTIVE.get(uuid);
		if (map != null) {
			map.remove(a.name(), a);
			if (map.isEmpty()) ACTIVE.remove(uuid);
		}
		a.owner().end(a);
	}

	/**
	 * Trigger an event on every aura of the entity that listens to it.
	 * `setup` lets you put data (damage, attacker...) into the context the mechanics run with.
	 */
	public static void trigger(Entity entity, String event, @Nullable Consumer<SkillContext> setup) {
		Map<String, ActiveAura> map = ACTIVE.get(entity.getUUID());
		if (map == null) return;

		for (ActiveAura a : new ArrayList<>(map.values())) {
			MechanicsExecutor executor = a.owner().getEvent(event);
			if (executor == null || !a.running.add(event)) continue;

			try {
				var ctx = new SkillContext(a.context());
				ctx.global_delay = 0;

				ctx.setVar("name", new StringValue(a.name()));
				ctx.setVar("remaining", new NumberValue(a.remaining));

				if (setup != null) setup.accept(ctx);
				executor.execute(ctx);
			} finally {
				a.running.remove(event);
			}
		}
	}

	public static Map<String, ActiveAura> getActives(Entity entity) {
		Map<String, ActiveAura> map = ACTIVE.get(entity.getUUID());
		return map == null ? Collections.emptyMap() : Collections.unmodifiableMap(map);
	}

	public static ActiveAura get(Entity entity, String name) {
		Map<String, ActiveAura> map = ACTIVE.get(entity.getUUID());
		return map == null ? null : map.get(name);
	}

	public static boolean has(Entity entity, String name) {
		return get(entity, name) != null;
	}

	public static boolean remove(Entity entity, String name) {
		ActiveAura a = get(entity, name);
		if (a == null) return false;
		finish(a);
		return true;
	}

	/** Call on death / disconnect. */
	public static void removeAll(Entity entity) {
		Map<String, ActiveAura> map = ACTIVE.get(entity.getUUID());
		if (map == null) return;
		for (ActiveAura a : new ArrayList<>(map.values())) finish(a);
	}
}