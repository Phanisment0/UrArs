package io.phanisment.urars.skill.template;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import static io.phanisment.urars.util.ActionUtils.aliases;

public abstract class DamagingMechanic extends SkillMechanic implements IEntityTarget {
	// Source / behavior
	protected final Node source;
	protected final Node prevent_knockback;
	protected final Node ignore_armor;
	protected final Node prevent_immunity;

	// Modifiers
	protected final Node multiplier;
	protected final Node min_damage;
	protected final Node max_damage;
	protected final Node ignore_resistance;
	protected final Node ignore_shield;
	protected final Node no_anger;

	public DamagingMechanic(SkillLineConfig config) {
		super(config);
		this.source = config.getMathString(aliases("source", "s"), "player");
		this.prevent_knockback = config.getMathBoolean(aliases("preventKnockback", "pkb"), false);
		this.ignore_armor = config.getMathBoolean(aliases("ignoreArmor", "ia"), false);
		this.prevent_immunity = config.getMathBoolean(aliases("preventImmunity", "pi"), false);

		this.multiplier = config.getMathNumber(aliases("multiplier", "mul", "m"), 1);
		this.min_damage = config.getMathNumber(aliases("minDamage", "mind"), 0);
		this.max_damage = config.getMathNumber(aliases("maxDamage", "maxd"), -1); // -1 = no cap
		this.ignore_resistance = config.getMathBoolean(aliases("ignoreResistance", "ir"), false);
		this.ignore_shield = config.getMathBoolean(aliases("ignoreShield", "is"), false);
		this.no_anger = config.getMathBoolean(aliases("noAnger", "na"), false);
	}

	protected abstract float calculateDamage(SkillContext ctx, Entity target);

	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		if (target == null) return;
		if (!(target.level() instanceof ServerLevel level)) return;

		float amount = this.calculateDamage(ctx, target);
		amount = this.applyModifiers(ctx, target, amount);
		if (amount <= 0) return;

		this.damage(ctx, level, target, amount);
	}

	protected float applyModifiers(SkillContext ctx, Entity target, float amount) {
		var eval = ctx.evalContext();

		amount *= this.multiplier.evalAsFloat(eval);

		float min = this.min_damage.evalAsFloat(eval);
		float max = this.max_damage.evalAsFloat(eval);
		if (amount < min) amount = min;
		if (max >= 0 && amount > max) amount = max;

		if (this.ignore_resistance.evalAsBoolean(eval) && target instanceof LivingEntity living) {
			MobEffectInstance effect = living.getEffect(MobEffects.RESISTANCE);
			if (effect != null) {
				int level = effect.getAmplifier() + 1;
				if (level < 5) amount /= (1f - 0.2f * level);
			}
		}

		return amount;
	}

	protected void damage(SkillContext ctx, ServerLevel level, Entity target, float amount) {
		var eval = ctx.evalContext();
		Entity caster = ctx.entity();
		DamageSource result = this.createSource(level.damageSources(), caster, this.ignore_armor.evalAsBoolean(eval), ctx);

		if (this.prevent_immunity.evalAsBoolean(eval)) target.invulnerableTime = 0;

		if (this.ignore_shield.evalAsBoolean(eval) && target instanceof LivingEntity living && living.isBlocking()) living.stopUsingItem();

		LivingEntity oldRevenge = null;
		LivingEntity oldTarget = null;
		boolean noAnger = this.no_anger.evalAsBoolean(eval);
		if (noAnger && target instanceof LivingEntity living) {
			oldRevenge = living.getLastHurtByMob();
			if (living instanceof Mob mob) oldTarget = mob.getTarget();
		}

		Vec3 oldVelocity = target.getDeltaMovement();
		target.hurtServer(level, result, amount);

		if (this.prevent_knockback.evalAsBoolean(eval)) {
			target.setDeltaMovement(oldVelocity);
			target.hurtMarked = true;
		}

		if (noAnger && target instanceof LivingEntity living) {
			living.setLastHurtByMob(oldRevenge);
			if (living instanceof Mob mob) mob.setTarget(oldTarget);
		}
	}

	protected DamageSource createSource(DamageSources s, Entity caster, boolean ignoreArmor, SkillContext ctx) {
		String type = this.source.evalAsString(ctx.evalContext()).toLowerCase();
		LivingEntity living = caster instanceof LivingEntity le ? le : null;

		return switch (type) {
			case "player", "mob", "attack" -> {
				if (ignoreArmor) yield caster != null ? s.indirectMagic(caster, caster) : s.magic();
				if (caster instanceof Player p) yield s.playerAttack(p);
				if (living != null) yield s.mobAttack(living);
				yield s.generic();
			}
			case "mobnoaggro", "noaggro" -> living != null ? s.noAggroMobAttack(living) : s.generic();
			case "sting", "bee" -> living != null ? s.sting(living) : s.generic();
			case "thorns" -> caster != null ? s.thorns(caster) : s.generic();
			case "mace" -> caster != null ? s.mace(caster) : s.generic();
			case "sonicboom" -> caster != null ? s.sonicBoom(caster) : s.generic();
			case "explosion" -> s.explosion(caster, caster);

			case "thrown", "projectile" -> caster != null ? s.thrown(caster, caster) : s.generic();
			case "trident" -> caster != null ? s.trident(caster, caster) : s.generic();
			case "mobprojectile" -> living != null ? s.mobProjectile(caster, living) : s.generic();
			case "spit" -> living != null ? s.spit(caster, living) : s.generic();
			case "windcharge" -> caster != null ? s.windCharge(caster, living) : s.generic();

			case "magic" -> caster != null ? s.indirectMagic(caster, caster) : s.magic();
			case "pure_magic", "puremagic" -> s.magic();
			case "wither" -> s.wither();
			case "dragonbreath" -> s.dragonBreath();

			case "anvil" -> caster != null ? s.anvil(caster) : s.generic();
			case "fallingblock" -> caster != null ? s.fallingBlock(caster) : s.generic();
			case "fallingstalactite" -> caster != null ? s.fallingStalactite(caster) : s.generic();

			case "infire", "fire" -> s.inFire();
			case "onfire", "burn" -> s.onFire();
			case "lava" -> s.lava();
			case "hotfloor", "magma" -> s.hotFloor();
			case "campfire" -> s.campfire();
			case "lightning", "lightningbolt" -> s.lightningBolt();

			case "fall" -> s.fall();
			case "enderpearl" -> s.enderPearl();
			case "flyintowall", "kinetic" -> s.flyIntoWall();
			case "inwall", "suffocate" -> s.inWall();
			case "cramming" -> s.cramming();
			case "drown" -> s.drown();
			case "dryout" -> s.dryOut();
			case "starve", "hunger" -> s.starve();
			case "cactus" -> s.cactus();
			case "sweetberrybush", "berrybush" -> s.sweetBerryBush();
			case "freeze" -> s.freeze();
			case "stalagmite" -> s.stalagmite();
			case "outofborder", "worldborder", "border" -> s.outOfBorder();

			case "void", "outofworld", "fellOutOfWorld" -> s.fellOutOfWorld();
			case "kill", "genericKill" -> s.genericKill(); // bypasses armor, resistance, creative invulnerability
			case "generic" -> ignoreArmor ? s.magic() : s.generic();

			default -> throw new IllegalStateException("Unexpected damage source: " + type);
		};
	}
}