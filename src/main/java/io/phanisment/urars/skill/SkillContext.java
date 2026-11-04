package io.phanisment.urars.skill;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.phanisment.urars.parser.math.eval.EvalContext;
import io.phanisment.urars.parser.math.value.IValue;
import io.phanisment.urars.skill.value.EntityValue;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class SkillContext {
	private static final Map<String, IValue<?>> GLOBAL_VARIABLE = new HashMap<>();

	private final Entity entity;
	private final EvalContext context;

	public long global_delay = 0;
	private final Map<String, Object> storage = new HashMap<>();

	public SkillContext(SkillContext copy) {
		this.entity = copy.entity;
		this.context = copy.context.copy();
		this.global_delay = copy.global_delay;
		this.storage.putAll(copy.storage);
	}

	public SkillContext(Entity entity) {
		this(entity, EvalContext.withStandardLibrary());
	}

	public SkillContext(Entity entity, EvalContext context) {
		this.entity = entity;
		this.context = context;

		setVar("caster", new EntityValue(entity));
		if (!GLOBAL_VARIABLE.isEmpty()) context.put(GLOBAL_VARIABLE);
	}

	public void setVar(String name, IValue<?> value) {
		context.put(name, value);
	}

	public IValue<?> getVar(String name) {
		return context.get(name);
	}

	public void removeVar(String name) {
		context.remove(name);
	}

	public boolean containsVar(String name) {
		return context.contains(name);
	}

	public boolean isPlayer() {
		return entity instanceof Player;
	}

	public UUID uuid() {
		return entity.getUUID();
	}

	public Entity entity() {
		return entity;
	}

	public Player player() throws IllegalArgumentException {
		if (entity instanceof Player p) return p;
		throw new IllegalArgumentException("Unknown Player");
	}

	public Level level() {
		return entity.level();
	}

	public LivingEntity livingEntity() throws IllegalArgumentException {
		if (entity instanceof LivingEntity e) return e;
		throw new IllegalArgumentException("Unknown Entity");
	}

	public Map<String, IValue<?>> global() {
		return GLOBAL_VARIABLE;
	}

	/**
	 * Use when you need data that doest modify the variable
	 *
	 * @return the map storage
	 */
	public Map<String, Object> storage() {
		return storage;
	}

	public Object getStorage(String key) {
		return storage.get(key);
	}

	public @Nullable <T> T getStorage(String key, Class<T> type) {
		Object value = storage.get(key);
		if (type.isInstance(value)) return (T)value;
		return null;
	}

	public void setStorage(String key, Object value) {
		storage.put(key, value);
	}

	public EvalContext evalContext() {
		return context;
	}
}